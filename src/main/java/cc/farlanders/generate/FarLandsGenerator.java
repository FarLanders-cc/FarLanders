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
    public long seed;

    public FarLandsGenerator(long seed) {
        this.seed = seed;
    }

    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunk) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;
                int height = getHeightAt(worldX, worldZ);

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
            } else if (y > height - 5) {
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

    int getHeightAt(int x, int z) {
        double elevation = 0;
        double scale = 0.01;
        double amplitude = 32;

        for (int i = 0; i < 4; i++) {
            double nx = x * scale;
            double nz = z * scale;

            if (Math.abs(x) > FARLANDS_THRESHOLD || Math.abs(z) > FARLANDS_THRESHOLD) {
                nx = Math.tan(x * scale * 0.1) * 1000;
                nz = Math.cos(z * scale * 0.1) * 1000;
            }

            elevation += OpenSimplex2S.noise2(seed, nx, nz) * amplitude;

            scale *= 2;
            amplitude /= 2;
        }

        return Math.clamp((int) (elevation + SEA_LEVEL), 0, MAX_HEIGHT - 1);
    }

}
