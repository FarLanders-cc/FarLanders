package cc.farlanders.generate.terrain;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator.ChunkData;

import cc.farlanders.noise.OpenSimplex2;

public class TerrainHandler {

    private static final int SEA_LEVEL = 64;
    private static final long NOISE_SEED = 12345L;

    private static final String DESERT = "desert";
    private static final String BADLANDS = "badlands";
    private static final String MUSHROOM_FIELDS = "mushroom_fields";
    private static final String SNOWY_PLAINS = "snowy_plains";

    /**
     * Safely gets a material if it exists in the current Minecraft version,
     * otherwise returns a fallback material
     */
    private static Material getMaterialOrFallback(String materialName, Material fallback) {
        try {
            return Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public static class BlockContext {
        public final int cx;
        public final int cz;
        public final int worldX;
        public final int y;
        public final int worldZ;
        public final double density;
        public final String biome;

        public BlockContext(int cx, int cz, int worldX, int y, int worldZ, double density, String biome) {
            this.cx = cx;
            this.cz = cz;
            this.worldX = worldX;
            this.y = y;
            this.worldZ = worldZ;
            this.density = density;
            this.biome = biome;
        }
    }

    public void handleSkyIslandBlock(ChunkData chunk, BlockContext context) {
        if (context.density > 0.5) {
            Material mat = getSkyIslandMaterial(context.worldX, context.y, context.worldZ, context.biome);
            chunk.setBlock(context.cx, context.y, context.cz, mat);
        } else {
            chunk.setBlock(context.cx, context.y, context.cz, Material.AIR);
        }
    }

    public void handleSurfaceOrUndergroundBlock(ChunkData chunk, BlockContext context) {
        if (context.density > 0.3 && isSolidEnvironment(context)) {
            Material blockType = getSurfaceMaterial(context.worldX, context.y, context.worldZ, context.biome);
            chunk.setBlock(context.cx, context.y, context.cz, blockType);
        } else if (context.y < SEA_LEVEL) {
            chunk.setBlock(context.cx, context.y, context.cz, Material.WATER);
        } else {
            chunk.setBlock(context.cx, context.y, context.cz, Material.AIR);
        }
    }

    public void handleSolidBlock(ChunkData chunk, BlockContext context) {
        Material blockType = getSurfaceMaterial(context.worldX, context.y, context.worldZ, context.biome);
        chunk.setBlock(context.cx, context.y, context.cz, blockType);
    }

    public void handleAirBlock(ChunkData chunk, BlockContext context) {
        chunk.setBlock(context.cx, context.y, context.cz, Material.AIR);
    }

    public void placeWater(ChunkData chunk, int cx, int y, int cz) {
        chunk.setBlock(cx, y, cz, Material.WATER);
    }

    public int findTopY(ChunkData chunk, int cx, int cz) {
        for (int y = 255; y >= 0; y--) {
            if (!chunk.getBlockData(cx, y, cz).getMaterial().isAir()) {
                return y;
            }
        }
        return 0;
    }

    public void generateWaterPocketIfNeeded(ChunkData chunk, int cx, int cz, int worldX, int worldZ) {
        // Generate water pockets in underground areas
        if (chunk.getBlockData(cx, SEA_LEVEL - 1, cz).getMaterial() == Material.STONE) {
            for (int y = SEA_LEVEL - 1; y >= SEA_LEVEL - 5; y--) {
                chunk.setBlock(cx, y, cz, Material.WATER);
            }
        }
    }

    private boolean isSolidEnvironment(BlockContext context) {
        double[] neighbors = new double[] {
                noise3D(context.worldX + 1, context.y, context.worldZ),
                noise3D(context.worldX - 1, context.y, context.worldZ),
                noise3D(context.worldX, context.y + 1, context.worldZ),
                noise3D(context.worldX, context.y - 1, context.worldZ),
                noise3D(context.worldX, context.y, context.worldZ + 1),
                noise3D(context.worldX, context.y, context.worldZ - 1)
        };

        for (double n : neighbors) {
            if (n <= 0.3)
                return false;
        }

        return true;
    }

    private Material getSkyIslandMaterial(int x, int y, int z, String biome) {
        // Sky islands have rare and valuable materials
        double noise = noise3D(x, y, z);
        double rareOreNoise = OpenSimplex2.noise3_ImproveXY(NOISE_SEED + 1000, x * 0.03, y * 0.03, z * 0.03);

        // Sky islands contain much rarer and more valuable ores
        if (rareOreNoise > 0.9) {
            return getMaterialOrFallback("NETHERITE_BLOCK", Material.DIAMOND_BLOCK);
        }
        if (rareOreNoise > 0.85) {
            return getMaterialOrFallback("ANCIENT_DEBRIS", Material.DIAMOND_ORE);
        }
        if (rareOreNoise > 0.8) {
            return Material.DIAMOND_ORE; // Rare
        }
        if (rareOreNoise > 0.75) {
            return Material.EMERALD_ORE; // Rare
        }
        if (rareOreNoise > 0.7) {
            return Material.LAPIS_ORE; // Uncommon
        }
        if (rareOreNoise > 0.65) {
            return Material.GOLD_ORE; // Uncommon
        }

        // Regular sky island materials based on height
        if (y > 260) {
            // Top of sky islands - more ethereal materials
            if (noise > 0.6)
                return Material.END_STONE;
            if (noise > 0.4)
                return getMaterialOrFallback("CALCITE", Material.STONE);
            return getMaterialOrFallback("DRIPSTONE_BLOCK", Material.COBBLESTONE);
        } else if (y > 240) {
            // Middle of sky islands - mixed materials
            if (noise > 0.5)
                return getMaterialOrFallback("DEEPSLATE", Material.ANDESITE);
            return Material.STONE;
        } else {
            // Base of sky islands - solid foundation
            if (noise > 0.7)
                return Material.OBSIDIAN;
            return getMaterialOrFallback("BLACKSTONE", Material.COBBLESTONE);
        }
    }

    private Material getSurfaceMaterial(int x, int y, int z, String biome) {
        if (y < 5)
            return Material.BEDROCK;
        if (y < 15)
            return getDeepMaterial(x, y, z);
        if (y < 40)
            return getOreOrStone(x, y, z);
        if (y < 60)
            return getTransitionMaterial(x, y, z, biome);
        if (y == getSurfaceLevel(x, z, biome))
            return getBiomeTopBlock(biome);
        if (y < getSurfaceLevel(x, z, biome) + 3)
            return getBiomeSubsurfaceBlock(biome);
        return Material.STONE;
    }

    private Material getDeepMaterial(int x, int y, int z) {
        double noise = noise3D(x, y, z);
        if (noise > 0.85)
            return getMaterialOrFallback("DEEPSLATE_DIAMOND_ORE", Material.DIAMOND_ORE);
        if (noise > 0.8)
            return getMaterialOrFallback("DEEPSLATE_GOLD_ORE", Material.GOLD_ORE);
        if (noise > 0.75)
            return getMaterialOrFallback("DEEPSLATE_IRON_ORE", Material.IRON_ORE);
        if (noise > 0.7)
            return getMaterialOrFallback("DEEPSLATE_COPPER_ORE", Material.STONE);
        if (noise > 0.6)
            return getMaterialOrFallback("DEEPSLATE_COAL_ORE", Material.COAL_ORE);
        if (noise > 0.5)
            return getMaterialOrFallback("DEEPSLATE_REDSTONE_ORE", Material.REDSTONE_ORE);

        // Add deep stone variety in clumps
        Material deepStoneVariant = getDeepStoneVariantClump(x, y, z);
        if (deepStoneVariant != null) {
            return deepStoneVariant;
        }

        return getMaterialOrFallback("DEEPSLATE", Material.STONE);
    }

    private Material getTransitionMaterial(int x, int y, int z, String biome) {
        double noise = noise3D(x, y, z);
        if (noise > 0.8)
            return Material.IRON_ORE;
        if (noise > 0.75)
            return getMaterialOrFallback("COPPER_ORE", Material.STONE);
        if (noise > 0.7)
            return Material.COAL_ORE;

        // Generate stone variety in clumps
        Material stoneVariant = getStoneVariantClump(x, y, z, biome);
        if (stoneVariant != null) {
            return stoneVariant;
        }

        // Biome-specific stone variants
        return switch (biome.toLowerCase()) {
            case DESERT, BADLANDS -> Material.SANDSTONE;
            case "mountains", "stony_peaks" -> Material.ANDESITE;
            case "dripstone_caves" -> Material.COBBLESTONE; // Use cobblestone instead
            default -> Material.STONE;
        };
    }

    private int getSurfaceLevel(int x, int z, String biome) {
        // Vary surface level slightly based on biome and position
        double surfaceNoise = noise3D(x, SEA_LEVEL, z) * 3;
        int baseLevel = SEA_LEVEL;

        return switch (biome.toLowerCase()) {
            case "mountains", "jagged_peaks", "stony_peaks" -> baseLevel + 20 + (int) surfaceNoise;
            case "hills" -> baseLevel + 10 + (int) surfaceNoise;
            case "ocean", "deep_ocean" -> baseLevel - 20;
            default -> baseLevel + (int) surfaceNoise;
        };
    }

    private Material getBiomeSubsurfaceBlock(String biome) {
        return switch (biome.toLowerCase()) {
            case DESERT -> Material.SAND;
            case MUSHROOM_FIELDS -> Material.MYCELIUM;
            case SNOWY_PLAINS, "ice_spikes" -> Material.SNOW_BLOCK;
            case BADLANDS -> Material.RED_SAND;
            case "soul_sand_valley" -> Material.SOUL_SAND;
            default -> Material.DIRT;
        };
    }

    private Material getBiomeTopBlock(String biome) {
        return switch (biome.toLowerCase()) {
            case DESERT -> Material.SAND;
            case MUSHROOM_FIELDS -> Material.MYCELIUM;
            case SNOWY_PLAINS, "ice_spikes" -> Material.SNOW_BLOCK;
            case BADLANDS -> Material.RED_SAND;
            case "taiga" -> Material.PODZOL;
            case "jungle" -> Material.GRASS_BLOCK;
            default -> Material.GRASS_BLOCK;
        };
    }

    private Material getOreOrStone(int x, int y, int z) {
        double noise = noise3D(x, y, z);
        if (y < 0) {
            return getDeepslateOre(noise);
        } else {
            return getStoneOre(noise);
        }
    }

    private Material getDeepslateOre(double noise) {
        if (noise > 0.8)
            return getMaterialOrFallback("DEEPSLATE_IRON_ORE", Material.IRON_ORE);
        if (noise > 0.7)
            return getMaterialOrFallback("DEEPSLATE_GOLD_ORE", Material.GOLD_ORE);
        if (noise > 0.6)
            return getMaterialOrFallback("DEEPSLATE_COPPER_ORE", Material.STONE);
        if (noise > 0.5)
            return getMaterialOrFallback("DEEPSLATE_COAL_ORE", Material.COAL_ORE);
        return getMaterialOrFallback("DEEPSLATE", Material.STONE);
    }

    private Material getStoneOre(double noise) {
        if (noise > 0.8)
            return Material.IRON_ORE;
        if (noise > 0.7)
            return getMaterialOrFallback("COPPER_ORE", Material.STONE);
        if (noise > 0.6)
            return Material.COAL_ORE;
        return Material.STONE;
    }

    private double noise3D(int x, int y, int z) {
        return OpenSimplex2.noise3_ImproveXY(NOISE_SEED, x * 0.05, y * 0.05, z * 0.05);
    }

    /**
     * Generates stone variants in clumps for more geological variety
     */
    private Material getStoneVariantClump(int x, int y, int z, String biome) {
        // Use larger scale noise to create clumps rather than scattered variants
        double clumpNoise = OpenSimplex2.noise3_ImproveXY(NOISE_SEED + 2000, x * 0.008, y * 0.008, z * 0.008);
        double varietyNoise = OpenSimplex2.noise3_ImproveXY(NOISE_SEED + 3000, x * 0.012, y * 0.012, z * 0.012);

        // Define stone variant zones based on noise values
        if (clumpNoise > 0.4) {
            // Granite clumps
            if (varietyNoise > 0.3)
                return Material.GRANITE;
            if (varietyNoise > 0.0)
                return Material.POLISHED_GRANITE;
            return Material.GRANITE;
        } else if (clumpNoise > 0.1) {
            // Diorite clumps
            if (varietyNoise > 0.3)
                return Material.DIORITE;
            if (varietyNoise > 0.0)
                return Material.POLISHED_DIORITE;
            return Material.DIORITE;
        } else if (clumpNoise > -0.2) {
            // Andesite clumps
            if (varietyNoise > 0.3)
                return Material.ANDESITE;
            if (varietyNoise > 0.0)
                return Material.POLISHED_ANDESITE;
            return Material.ANDESITE;
        } else if (clumpNoise > -0.5) {
            // Stone variant clumps (calcite and tuff)
            if (varietyNoise > 0.2)
                return getMaterialOrFallback("CALCITE", Material.STONE);
            if (varietyNoise > -0.2)
                return getMaterialOrFallback("TUFF", Material.COBBLESTONE);
            return getMaterialOrFallback("CALCITE", Material.STONE);
        } else if (clumpNoise > -0.7) {
            // Stone variant clumps (deepslate variants)
            if (varietyNoise > 0.1)
                return getMaterialOrFallback("DEEPSLATE", Material.COBBLESTONE);
            if (varietyNoise > -0.3)
                return getMaterialOrFallback("COBBLED_DEEPSLATE", Material.ANDESITE);
            return getMaterialOrFallback("DEEPSLATE", Material.COBBLESTONE);
        }

        // Biome-specific rare stone clumps
        if (clumpNoise < -0.7) {
            return switch (biome.toLowerCase()) {
                case "desert", "badlands" -> varietyNoise > 0.0 ? Material.RED_SANDSTONE : Material.SANDSTONE;
                case "swamp", "mangrove_swamp" ->
                    varietyNoise > 0.0 ? getMaterialOrFallback("MUD", Material.DIRT) : Material.COARSE_DIRT;
                case "jungle" -> varietyNoise > 0.0 ? Material.MOSSY_COBBLESTONE : Material.MOSSY_STONE_BRICKS;
                case "taiga", "snowy_taiga" ->
                    varietyNoise > 0.0 ? Material.PACKED_ICE : getMaterialOrFallback("BLUE_ICE", Material.ICE);
                case "mountains", "stony_peaks" ->
                    varietyNoise > 0.0 ? getMaterialOrFallback("DEEPSLATE", Material.COBBLESTONE) : Material.STONE;
                default -> Material.COBBLESTONE;
            };
        }

        return null; // Return null to use default stone
    }

    /**
     * Generates deep stone variants in clumps for underground variety
     */
    private Material getDeepStoneVariantClump(int x, int y, int z) {
        // Use different noise for deep stone variants (use 1.15.2 compatible materials)
        double deepClumpNoise = OpenSimplex2.noise3_ImproveXY(NOISE_SEED + 4000, x * 0.01, y * 0.01, z * 0.01);

        if (deepClumpNoise > 0.6) {
            return Material.COBBLESTONE;
        } else if (deepClumpNoise > 0.3) {
            return Material.STONE_BRICKS;
        } else if (deepClumpNoise > 0.0) {
            return Material.POLISHED_ANDESITE;
        } else if (deepClumpNoise > -0.3) {
            return Material.ANDESITE;
        } else if (deepClumpNoise > -0.6) {
            return Material.CHISELED_STONE_BRICKS;
        }

        return null; // Use default stone
    }
}
