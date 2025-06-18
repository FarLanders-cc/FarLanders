package cc.farlanders.generate;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import cc.farlanders.noise.OpenSimplex2;

public class FarLandsGenerator extends ChunkGenerator {

    private static final int SEA_LEVEL = 64;
    private static final int MAX_HEIGHT = 256;
    private static final double FARLANDS_THRESHOLD = 10000.0;
    private static final int STRUCTURE_GRID_SIZE = 196;

    public FarLandsGenerator() {
        // empty
    }

    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;
                generateColumn(chunk, x, z, worldX, worldZ);
            }
        }
    }

    private boolean shouldGenerateWaterPocket(int x, int z, Random random) {
        double noise = OpenSimplex2.noise3ImproveXZ(4242, x * 0.01, 0, z * 0.01);
        return noise > 0.65 && random.nextDouble() < 0.03; // very rare
    }

    private void generateColumn(ChunkData chunk, int cx, int cz, int worldX, int worldZ) {
        boolean farlands = Math.abs(worldX) > FARLANDS_THRESHOLD || Math.abs(worldZ) > FARLANDS_THRESHOLD;

        for (int y = 0; y < MAX_HEIGHT; y++) {
            double density = getCombinedDensity(worldX, y, worldZ, farlands);

            if (density > 0.3) { // More terrain, less void
                Material blockMat = getBlockMaterial(worldX, y, worldZ, density, farlands);
                chunk.setBlock(cx, y, cz, blockMat);
            } else {
                // Scarce, bedrock-contained water pockets
                double waterChance = OpenSimplex2.noise3ImproveXZ(555, worldX * 0.03, y * 0.03, worldZ * 0.03);
                if (y < SEA_LEVEL - 5 && y > 5 && waterChance > 0.78) {
                    chunk.setBlock(cx, y, cz, Material.WATER);
                    chunk.setBlock(cx, y - 1, cz, Material.BEDROCK); // Bedrock base
                } else {
                    chunk.setBlock(cx, y, cz, Material.AIR);
                }
            }
        }

        // Tree generation on grass at sea level and above
        int topY = SEA_LEVEL;
        while (topY < MAX_HEIGHT && chunk.getBlockData(cx, topY, cz).getMaterial() == Material.AIR) {
            topY++;
        }

        if (chunk.getBlockData(cx, topY - 1, cz).getMaterial() == Material.GRASS_BLOCK) {
            double treeChance = OpenSimplex2.noise3ImproveXZ(888, worldX * 0.05, 0, worldZ * 0.05);
            if (treeChance > 0.5) {
                chunk.setBlock(cx, topY, cz, Material.OAK_SAPLING); // Bukkit replaces saplings with trees after chunk
                                                                    // generation
            }
        }

        // Rare water pocket generation
        if (shouldGenerateWaterPocket(worldX, worldZ, new Random(worldX * 31 + worldZ * 57))) {
            int centerY = 20 + new Random(worldX * 11 + worldZ * 13).nextInt(20); // Y = 20–40
            for (int y = centerY - 1; y <= centerY + 1; y++) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        int bx = cx + dx;
                        int bz = cz + dz;
                        if (bx < 0 || bx >= 16 || bz < 0 || bz >= 16)
                            continue;

                        if (Math.abs(dx) == 1 || Math.abs(dz) == 1 || y == centerY - 1 || y == centerY + 1) {
                            chunk.setBlock(bx, y, bz, Material.BEDROCK);
                        } else {
                            chunk.setBlock(bx, y, bz, Material.WATER);
                        }
                    }
                }
            }
        }

    }

    private double getCombinedDensity(int x, int y, int z, boolean farlands) {
        double tower = getTowerDensity(x, y, z);
        double tunnel = getTunnelDensity(x, y, z);
        double connector = getTunnelBridgeDensity(x, y, z);

        double combined = tower * (1.0 - tunnel) + connector;

        double chaos = 0;
        double amp = 0.4;
        double freq = 0.01;
        for (int i = 0; i < 5; i++) {
            chaos += amp * OpenSimplex2.noise3ImproveXZ(i * 100, x * freq, y * freq, z * freq);
            amp *= 0.5;
            freq *= 2;
        }

        double disruption = 0.25 * Math.abs(OpenSimplex2.noise3ImproveXZ(2048, y * 0.05, x * 0.03, z * 0.03));

        combined += chaos * 0.3 + disruption;
        return Math.clamp(combined, 0, 1);
    }

    private double getTowerDensity(int x, int y, int z) {
        int gridX = Math.floorDiv(x, STRUCTURE_GRID_SIZE);
        int gridZ = Math.floorDiv(z, STRUCTURE_GRID_SIZE);

        long seed = gridX * 734287L + gridZ * 912367L;
        Random rand = new Random(seed);

        double centerX = gridX * STRUCTURE_GRID_SIZE + rand.nextDouble() * STRUCTURE_GRID_SIZE;
        double centerZ = gridZ * STRUCTURE_GRID_SIZE + rand.nextDouble() * STRUCTURE_GRID_SIZE;

        double dx = x - centerX;
        double dz = z - centerZ;
        double dist = Math.sqrt(dx * dx + dz * dz);

        double baseRadius = 30 + rand.nextDouble() * 20;
        double heightFalloff = Math.max(0, 1 - dist / baseRadius);

        double heightBase = 60 + rand.nextDouble() * 50;

        double fractal = 0;
        double amp = 1;
        double freq = 0.015;
        for (int i = 0; i < 6; i++) {
            fractal += amp * OpenSimplex2.noise3ImproveXZ(i * 200, x * freq, y * freq, z * freq);
            amp *= 0.5;
            freq *= 2;
        }

        double maxHeight = heightBase + fractal * 25;
        return (y < maxHeight * heightFalloff) ? 1.0 : 0.0;
    }

    private double getTunnelDensity(int x, int y, int z) {
        double result = 0;
        double freq = 0.01;
        double amp = 1.0;

        for (int i = 0; i < 5; i++) {
            result += amp * OpenSimplex2.noise3ImproveXZ(i * 400, x * freq, y * freq, z * freq);
            amp *= 0.5;
            freq *= 2.0;
        }

        result = Math.abs(result);

        double tunnelTwist = OpenSimplex2.noise3ImproveXZ(1111, y * 0.025, z * 0.015, x * 0.015);
        result += 0.35 * Math.abs(tunnelTwist);
        return Math.min(1.0, result);
    }

    private double getTunnelBridgeDensity(int x, int y, int z) {
        double noise = OpenSimplex2.noise3ImproveXZ(1337, x * 0.008, y * 0.008, z * 0.008);
        double band = Math.sin((x + z) * 0.02 + noise * Math.PI * 2);
        double verticalBand = Math.sin(y * 0.04 + noise * Math.PI);
        double density = 1.0 - Math.abs(band * verticalBand);
        return Math.clamp(density, 0, 1) * 0.4;
    }

    private Material getBlockMaterial(int x, int y, int z, double density, boolean farlands) {
        if (y < SEA_LEVEL - 20)
            return getOreOrStone(x, y, z);
        if (y < SEA_LEVEL)
            return Material.DIRT;
        if (y == SEA_LEVEL)
            return Material.GRASS_BLOCK;

        if (density > 0.75)
            return Material.GRASS_BLOCK;
        if (density > 0.6)
            return Material.DIRT;
        return Material.AIR;
    }

    private Material getOreOrStone(int x, int y, int z) {
        if (!isOreVein(x, y, z, 0.5))
            return Material.STONE;

        double rarityNoise = OpenSimplex2.noise3ImproveXZ(777, x * 0.01, y * 0.01, z * 0.01);
        double value = rarityNoise + OpenSimplex2.noise3ImproveXZ(998, x * 0.05, y * 0.05, z * 0.05);

        if (value > 1.2)
            return Material.DIAMOND_ORE;
        if (value > 1.0)
            return Material.EMERALD_ORE;
        if (value > 0.85)
            return Material.REDSTONE_ORE;
        if (value > 0.7)
            return Material.LAPIS_ORE;
        if (value > 0.55)
            return Material.GOLD_ORE;
        if (value > 0.4)
            return Material.IRON_ORE;
        if (value > 0.25)
            return Material.COAL_ORE;

        return Material.STONE;
    }

    boolean isOreVein(int x, int y, int z, double threshold) {
        double scale = 0.08;
        double n = OpenSimplex2.noise3ImproveXZ(999, x * scale, y * scale, z * scale);
        return n > threshold;
    }
}
