package cc.farlanders.generate;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import cc.farlanders.noise.OpenSimplex2S;

public class FarLandsGenerator extends ChunkGenerator {

    private static final int SEA_LEVEL = 64;
    private static final int MAX_HEIGHT = 256;
    private static final double FARLANDS_THRESHOLD = 10000.0;
    private final OpenSimplex2S noise;

    public FarLandsGenerator() {
        this.noise = new OpenSimplex2S(); // uses time-based seed by default
    }

    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;

                int height = getHeightAt(worldX, worldZ);

                // Chance of terrain holes in Far Lands
                if (isFarlands(worldX, worldZ) && ((worldX ^ worldZ) % 127 < 5)) {
                    continue;
                }

                setTerrainBlocks(chunk, x, z, height);
                fillWaterBelowSeaLevel(chunk, x, z);
            }
        }
    }

    private void setTerrainBlocks(ChunkData chunk, int x, int z, int height) {
        for (int y = 0; y <= height; y++) {
            Material mat;
            if (y == height) {
                mat = Material.GRASS_BLOCK;
            } else if (y > height - 4) {
                mat = Material.DIRT;
            } else {
                mat = Material.STONE;
            }
            chunk.setBlock(x, y, z, mat);
        }
    }

    private void fillWaterBelowSeaLevel(ChunkData chunk, int x, int z) {
        for (int y = 0; y < SEA_LEVEL; y++) {
            if (chunk.getBlockData(x, y, z).getMaterial().isAir()) {
                chunk.setBlock(x, y, z, Material.WATER);
            }
        }
    }

    private boolean isFarlands(int x, int z) {
        return Math.abs(x) > FARLANDS_THRESHOLD || Math.abs(z) > FARLANDS_THRESHOLD;
    }

    private int clamp(int val, int min, int max) {
        return Math.clamp(val, min, max);
    }

    int getHeightAt(int x, int z) {
        double nx = x * 0.004;
        double nz = z * 0.004;
        double elevation = 0;

        boolean farlands = isFarlands(x, z);

        // Core fractal elevation
        double amp = 64;
        double freq = 1;
        for (int i = 0; i < 5; i++) {
            double dx = nx * freq;
            double dz = nz * freq;

            // Farlands distortion
            if (farlands) {
                dx = Math.tan(dx * 0.4) * 200 + Math.sin(dz * 0.6) * 300;
                dz = Math.atan(dz * 0.4) * 200 + Math.cos(dx * 0.6) * 300;
            }

            elevation += OpenSimplex2S.noise2(i, dx, dz) * amp;
            freq *= 2;
            amp *= 0.5;
        }

        // Glitch step distortion
        if (farlands) {
            elevation = Math.abs(elevation % 72 - 36) * 1.7;

            // Add floating ridges
            if ((x + z) % 24 == 0) {
                elevation += 20 * Math.sin(x * 0.05) + 15 * Math.cos(z * 0.05);
            }

            // Occasional floating platforms
            if ((x * z) % 1013 == 0) {
                elevation = SEA_LEVEL + 60 + (int) (OpenSimplex2S.noise2(99, x * 0.01, z * 0.01) * 40);
            }
        }

        // Normalize elevation to fit world height
        elevation += SEA_LEVEL + 10;

        if ((x ^ z) % 101 < 3)
            return 0;

        return clamp((int) elevation, 0, MAX_HEIGHT - 1);
    }
}
