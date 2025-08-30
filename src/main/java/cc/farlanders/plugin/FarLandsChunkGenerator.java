package cc.farlanders.plugin;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import cc.farlanders.noise.OpenSimplex2S;

public class FarLandsChunkGenerator extends ChunkGenerator {
    private static final int SEA_LEVEL = 64;
    private static final long SEED = 1337L;
    private static final double FAR_LANDS_THRESHOLD = 12550820;

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);

        int worldX = chunkX << 4;
        int worldZ = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                setColumnBlocks(chunk, worldX, worldZ, x, z);
            }
        }

        return chunk;
    }

    private void setColumnBlocks(ChunkData chunk, int worldX, int worldZ, int x, int z) {
        int absoluteX = worldX + x;
        int absoluteZ = worldZ + z;

        double noiseX = absoluteX * 0.01;
        double noiseZ = absoluteZ * 0.01;

        // Simulate Far Lands corruption
        if (Math.abs(absoluteX) > FAR_LANDS_THRESHOLD || Math.abs(absoluteZ) > FAR_LANDS_THRESHOLD) {
            noiseX *= 100; // Exaggerated distortion
            noiseZ *= 100;
        }

        double elevation = OpenSimplex2S.noise2(SEED, noiseX, noiseZ) * 20 + SEA_LEVEL;

        for (int y = 0; y < (int) elevation; y++) {
            if (y < elevation - 5) {
                chunk.setBlock(x, y, z, Material.STONE);
            } else if (y < elevation - 1) {
                chunk.setBlock(x, y, z, Material.DIRT);
            } else {
                chunk.setBlock(x, y, z, Material.GRASS_BLOCK);
            }
        }

        if (elevation < SEA_LEVEL) {
            for (int y = (int) elevation; y <= SEA_LEVEL; y++) {
                chunk.setBlock(x, y, z, Material.WATER);
            }
        }
    }
}
