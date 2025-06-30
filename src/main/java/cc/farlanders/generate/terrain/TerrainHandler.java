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
        // Use biome-appropriate materials for sky islands
        String biomeLower = biome.toLowerCase();
        if (y < SEA_LEVEL - 10) {
            return getOreOrStone(x, y, z);
        } else if (y < SEA_LEVEL) {
            return getBiomeTopBlock(biomeLower);
        } else {
            return Material.STONE;
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
            return Material.DEEPSLATE_DIAMOND_ORE;
        if (noise > 0.8)
            return Material.DEEPSLATE_GOLD_ORE;
        if (noise > 0.75)
            return Material.DEEPSLATE_IRON_ORE;
        if (noise > 0.7)
            return Material.DEEPSLATE_COPPER_ORE;
        if (noise > 0.6)
            return Material.DEEPSLATE_COAL_ORE;
        if (noise > 0.5)
            return Material.DEEPSLATE_REDSTONE_ORE;
        return Material.DEEPSLATE;
    }

    private Material getTransitionMaterial(int x, int y, int z, String biome) {
        double noise = noise3D(x, y, z);
        if (noise > 0.8)
            return Material.IRON_ORE;
        if (noise > 0.75)
            return Material.COPPER_ORE;
        if (noise > 0.7)
            return Material.COAL_ORE;

        // Biome-specific stone variants
        return switch (biome.toLowerCase()) {
            case DESERT, BADLANDS -> Material.SANDSTONE;
            case "mountains", "stony_peaks" -> Material.ANDESITE;
            case "dripstone_caves" -> Material.DRIPSTONE_BLOCK;
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
            return Material.DEEPSLATE_IRON_ORE;
        if (noise > 0.7)
            return Material.DEEPSLATE_GOLD_ORE;
        if (noise > 0.6)
            return Material.DEEPSLATE_COPPER_ORE;
        if (noise > 0.5)
            return Material.DEEPSLATE_COAL_ORE;
        return Material.DEEPSLATE;
    }

    private Material getStoneOre(double noise) {
        if (noise > 0.8)
            return Material.IRON_ORE;
        if (noise > 0.7)
            return Material.COPPER_ORE;
        if (noise > 0.6)
            return Material.COAL_ORE;
        return Material.STONE;
    }

    private double noise3D(int x, int y, int z) {
        return OpenSimplex2.noise3_ImproveXY(NOISE_SEED, x * 0.05, y * 0.05, z * 0.05);
    }
}
