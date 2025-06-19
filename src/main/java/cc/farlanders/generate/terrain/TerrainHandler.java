package cc.farlanders.generate.terrain;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator.ChunkData;

import cc.farlanders.noise.OpenSimplex2;

public class TerrainHandler {

    private static final int SEA_LEVEL = 64;

    public static class BlockContext {
        public final int cx; // Chunk X coordinate
        public final int cz; // Chunk Z coordinate
        public final int worldX; // World X coordinate
        public final int y; // World Y coordinate
        public final int worldZ; // World Z coordinate
        public final double density; // Density value for this block
        public final String biome; // Biome at this block

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
        if (context.density > 0.3) {
            Material blockType = getLayeredSurfaceMaterial(chunk, context);
            chunk.setBlock(context.cx, context.y, context.cz, blockType);

            // NEW: Place rare lighting features in dark depths
            placeLightingIfNeeded(chunk, context);

        } else if (context.y < SEA_LEVEL) {
            chunk.setBlock(context.cx, context.y, context.cz, Material.WATER);
        } else {
            chunk.setBlock(context.cx, context.y, context.cz, Material.AIR);
        }
    }

    public void placeLightingIfNeeded(ChunkData chunk, BlockContext ctx) {
        // Only place lighting in underground areas
        if (ctx.y >= SEA_LEVEL - 5 || ctx.density < 0.35)
            return;

        double noise = noise3D(ctx.worldX + 999, ctx.y, ctx.worldZ - 999);
        int chance = (int) Math.floor(Math.abs(noise) * 1000);

        if (chance < 1) {
            chunk.setBlock(ctx.cx, ctx.y, ctx.cz, Material.ENCHANTING_TABLE); // ~0.1% rare
        } else if (chance < 3) {
            chunk.setBlock(ctx.cx, ctx.y, ctx.cz, Material.ENDER_CHEST); // ~0.2%
        } else if (chance < 15) {
            chunk.setBlock(ctx.cx, ctx.y, ctx.cz, getRandomLightSource(ctx));
        }
    }

    private Material getRandomLightSource(BlockContext ctx) {
        int v = Math.abs((ctx.worldX * 31 + ctx.y * 17 + ctx.worldZ * 13) % 4);
        return switch (v) {
            case 0 -> Material.SHROOMLIGHT;
            case 1 -> Material.OCHRE_FROGLIGHT;
            case 2 -> Material.PEARLESCENT_FROGLIGHT;
            case 3 -> Material.LAVA;
            default -> Material.SHROOMLIGHT;
        };
    }

    private Material getLayeredSurfaceMaterial(ChunkData chunk, BlockContext ctx) {
        // Top layer detection
        boolean isExposed = isTopOfStack(chunk, ctx.cx, ctx.y, ctx.cz);

        if (isExposed) {
            return getSurfaceTopBlock(ctx.biome);
        }

        return getSubsurfaceBlock(ctx.y, ctx.biome);
    }

    private boolean isTopOfStack(ChunkData chunk, int x, int y, int z) {
        for (int i = 1; i <= 2; i++) {
            if (chunk.getBlockData(x, y + i, z).getMaterial().isAir()) {
                return true;
            }
        }
        return false;
    }

    private Material getSubsurfaceBlock(int y, String biome) {
        String biomeKey = biome.toLowerCase();

        if (y < SEA_LEVEL - 10)
            return Material.STONE;

        if (Math.abs((y * 31 + biome.hashCode()) % 20) == 0) {
            return Material.GRAVEL;
        }

        return switch (biomeKey) {
            case "taiga", "old growth spruce taiga", "snowy taiga" -> Material.PODZOL;
            case "jungle", "bamboo jungle" -> Material.ROOTED_DIRT;
            case "savanna" -> Material.COARSE_DIRT;
            case "mushroom fields" -> Material.MYCELIUM;
            case "desert", "badlands" -> Material.SANDSTONE;
            default -> Material.DIRT;
        };
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
        // Optional: Implement logic for larger water pockets, underground lakes, or
        // ponds

        // Example: Create a larger water pocket if conditions are met
        if (chunk.getBlockData(cx, SEA_LEVEL - 1, cz).getMaterial() == Material.STONE) {
            for (int y = SEA_LEVEL - 1; y >= SEA_LEVEL - 5; y--) {
                chunk.setBlock(cx, y, cz, Material.WATER);
            }
        }

    }

    // Determine block types for sky islands - mostly stone variants with some ores
    private Material getSkyIslandMaterial(int x, int y, int z, String biome) {
        // Example: Mostly stone variants, rare ores
        if (y < SEA_LEVEL - 10) {
            return getOreOrStone(x, y, z);
        } else if (y < SEA_LEVEL) {
            return Material.DIRT;
        } else {
            return Material.STONE;
        }
    }

    // Determine block types for surface and underground blocks - change by biome
    // and height
    private Material getSurfaceMaterial(int x, int y, int z, String biome) {
        if (y < SEA_LEVEL - 10) {
            return getOreOrStone(x, y, z);
        } else if (y < SEA_LEVEL) {
            return Material.DIRT;
        } else if (y == SEA_LEVEL) {
            return getSurfaceTopBlock(biome);
        } else if (y < SEA_LEVEL + 5) {
            return Material.DIRT;
        } else {
            return Material.AIR;
        }
    }

    // Surface top block by biome
    private Material getSurfaceTopBlock(String biome) {
        return switch (biome.toLowerCase()) {
            case "desert" -> Material.SAND;
            case "mushroom fields" -> Material.MYCELIUM;
            case "snowy plains", "ice spikes" -> Material.SNOW_BLOCK;
            case "badlands" -> Material.RED_SAND;
            default -> Material.GRASS_BLOCK;
        };
    }

    // Ore or stone with some randomness for ore placement
    private Material getOreOrStone(int x, int y, int z) {
        double noiseValue = noise3D(x, y, z);
        if (y < 0) {
            return getDeepslateOre(noiseValue);
        } else {
            return getStoneOre(noiseValue);
        }
    }

    private Material getDeepslateOre(double noiseValue) {
        if (noiseValue > 0.8) {
            return Material.DEEPSLATE_DIAMOND_ORE;
        }
        if (noiseValue > 0.7) {
            return Material.DEEPSLATE_GOLD_ORE;
        }
        if (noiseValue > 0.5) {
            return Material.DEEPSLATE_IRON_ORE;
        }
        if (noiseValue > 0.3) {
            return Material.DEEPSLATE_COAL_ORE;
        }
        return Material.DEEPSLATE;
    }

    private Material getStoneOre(double noiseValue) {
        if (noiseValue > 0.8) {
            return Material.DIAMOND_ORE;
        }
        if (noiseValue > 0.7) {
            return Material.GOLD_ORE;
        }
        if (noiseValue > 0.5) {
            return Material.IRON_ORE;
        }
        if (noiseValue > 0.3) {
            return Material.COAL_ORE;
        }
        return Material.STONE;
    }

    // Simple 3D noise stub, replace with your OpenSimplex2 3D noise call
    private static final long NOISE_SEED = 12345L; // You can change this seed as needed

    private double noise3D(int x, int y, int z) {
        // Use your noise library, seeded consistently
        return OpenSimplex2.noise3_ImproveXY(NOISE_SEED, x * 0.05, y * 0.05, z * 0.05);
    }
}
