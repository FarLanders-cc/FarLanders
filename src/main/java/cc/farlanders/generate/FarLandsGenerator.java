
package cc.farlanders.generate;

import java.util.Random;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import cc.farlanders.generate.biomes.BiomeProvider;
import cc.farlanders.generate.density.DensityFunction;
import cc.farlanders.generate.structures.StructureGenerator;
import cc.farlanders.generate.terrain.TerrainHandler;
import cc.farlanders.generate.vegetation.FloraGenerator;

public class FarLandsGenerator extends ChunkGenerator {

    private static final int SEA_LEVEL = 64;
    private static final int MAX_HEIGHT = 256;
    private static final double FARLANDS_THRESHOLD = 100000;

    private final DensityFunction densityFunction = new DensityFunction();
    private final TerrainHandler terrainHandler = new TerrainHandler();
    private final FloraGenerator floraGenerator = new FloraGenerator();
    private final BiomeProvider biomeProvider = new BiomeProvider(SEA_LEVEL, FARLANDS_THRESHOLD, MAX_HEIGHT);
    private final StructureGenerator structureGenerator = new StructureGenerator();

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

    private void generateColumn(ChunkData chunk, int cx, int cz, int worldX, int worldZ) {
        boolean farlands = Math.abs(worldX) > FARLANDS_THRESHOLD || Math.abs(worldZ) > FARLANDS_THRESHOLD;

        for (int y = 0; y < MAX_HEIGHT; y++) {
            double density = densityFunction.getCombinedDensity(worldX, y, worldZ, farlands);
            String biome = biomeProvider.getBiomeAt(worldX, y);
            TerrainHandler.BlockContext context = new TerrainHandler.BlockContext(cx, cz, worldX, y, worldZ, density,
                    biome);

            if (y > SEA_LEVEL + 48) {
                terrainHandler.handleSkyIslandBlock(chunk, context);
            } else {
                terrainHandler.handleSurfaceOrUndergroundBlock(chunk, context);
            }
        }

        int topY = terrainHandler.findTopY(chunk, cx, cz);
        terrainHandler.generateWaterPocketIfNeeded(chunk, cx, cz, worldX, worldZ);
        String biome = biomeProvider.getBiomeAt(worldX, topY);

        floraGenerator.generateBiomeTree(chunk, cx, cz, worldX, worldZ, topY, biome);
        floraGenerator.generatePlantsAndGrass(chunk, cx, cz, worldX, worldZ, topY, biome);

        StructureGenerator.BiomeStyle biomeStyle = StructureGenerator.BiomeStyle.fromBiome(biome);
        structureGenerator.generateStructures(chunk, cx, cz, worldX, worldZ, topY, biomeStyle);
    }

}