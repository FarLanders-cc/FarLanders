package cc.farlanders.generate;

import java.util.Random;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import cc.farlanders.generate.biomes.BiomeProvider;
import cc.farlanders.generate.config.GenerationConfig;
import cc.farlanders.generate.density.DensityFunction;
import cc.farlanders.generate.structures.StructureGenerator;
import cc.farlanders.generate.terrain.TerrainHandler;
import cc.farlanders.generate.vegetation.FloraGenerator;

public class FarLandsGenerator extends ChunkGenerator {

    private final DensityFunction densityFunction = new DensityFunction();
    private final TerrainHandler terrainHandler = new TerrainHandler();
    private final FloraGenerator floraGenerator = new FloraGenerator();
    private final BiomeProvider biomeProvider = new BiomeProvider(
            GenerationConfig.getSeaLevel(),
            GenerationConfig.getFarlandsThreshold(),
            GenerationConfig.getMaxHeight());
    private final StructureGenerator structureGenerator = new StructureGenerator();

    @Override
    public void generateSurface(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunk) {
        int chunkSize = GenerationConfig.getChunkSize();
        for (int x = 0; x < chunkSize; x++) {
            for (int z = 0; z < chunkSize; z++) {
                int worldX = chunkX * chunkSize + x;
                int worldZ = chunkZ * chunkSize + z;
                generateColumn(chunk, x, z, worldX, worldZ);
            }
        }
    }

    private void generateColumn(ChunkData chunk, int cx, int cz, int worldX, int worldZ) {
        // Calculate distance from origin to determine FarLands effects
        double distanceFromOrigin = Math.sqrt((double) worldX * worldX + (double) worldZ * worldZ);
        boolean farlands = distanceFromOrigin > GenerationConfig.getFarlandsThreshold();
        double farlandsIntensity = Math.clamp((distanceFromOrigin - GenerationConfig.getFarlandsThreshold())
                / GenerationConfig.getFarlandsIntensityDivisor(), 0.0, 1.0);

        // Get biome for this column
        String biome = biomeProvider.getBiomeAt(worldX, worldZ);

        // Generate terrain column
        ColumnData data = new ColumnData(cx, cz, worldX, worldZ, farlands, farlandsIntensity, biome);
        int surfaceHeight = generateTerrainForColumn(chunk, data);

        // Add water if below sea level
        fillWaterToSeaLevel(chunk, cx, cz, surfaceHeight);

        // Add vegetation and structures on surface
        addSurfaceFeatures(chunk, cx, cz, worldX, worldZ, surfaceHeight, biome);

        // Add agriculture features
        addAgricultureFeatures(chunk, cx, cz, worldX, worldZ, surfaceHeight, biome);

        // Add mob spawning areas
        addMobSpawningFeatures(chunk, cx, cz, worldX, worldZ, biome);
    }

    private static class ColumnData {
        final int cx;
        final int cz;
        final int worldX;
        final int worldZ;
        final boolean farlands;
        final double intensity;
        final String biome;

        ColumnData(int cx, int cz, int worldX, int worldZ, boolean farlands, double intensity, String biome) {
            this.cx = cx;
            this.cz = cz;
            this.worldX = worldX;
            this.worldZ = worldZ;
            this.farlands = farlands;
            this.intensity = intensity;
            this.biome = biome;
        }
    }

    private void addSurfaceFeatures(ChunkData chunk, int cx, int cz, int worldX, int worldZ, int surfaceHeight,
            String biome) {
        if (surfaceHeight > 0) {
            floraGenerator.generateBiomeTree(chunk, cx, cz, worldX, worldZ, surfaceHeight, biome);
            floraGenerator.generatePlantsAndGrass(chunk, cx, cz, worldX, worldZ, surfaceHeight, biome);

            StructureGenerator.BiomeStyle biomeStyle = StructureGenerator.BiomeStyle.fromBiome(biome);
            structureGenerator.generateStructures(chunk, cx, cz, worldX, worldZ, surfaceHeight, biomeStyle);
        }
    }

    private int generateTerrainForColumn(ChunkData chunk, ColumnData data) {
        int surfaceHeight = 0;

        for (int y = 0; y < GenerationConfig.getMaxHeight(); y++) {
            double density = densityFunction.getCombinedDensity(data.worldX, y, data.worldZ, data.farlands,
                    data.intensity);

            TerrainHandler.BlockContext context = new TerrainHandler.BlockContext(
                    data.cx, data.cz, data.worldX, y, data.worldZ, density, data.biome);

            if (density > GenerationConfig.getTerrainDensityThreshold()) {
                // Check if this is a sky island (high altitude)
                if (y >= GenerationConfig.getSkyIslandMinHeight() && y <= GenerationConfig.getSkyIslandMaxHeight()) {
                    terrainHandler.handleSkyIslandBlock(chunk, context);
                } else {
                    terrainHandler.handleSolidBlock(chunk, context);
                }
                surfaceHeight = Math.max(surfaceHeight, y);
            } else {
                terrainHandler.handleAirBlock(chunk, context);
            }
        }

        return surfaceHeight;
    }

    private void fillWaterToSeaLevel(ChunkData chunk, int cx, int cz, int surfaceHeight) {
        if (surfaceHeight < GenerationConfig.getSeaLevel()) {
            for (int y = surfaceHeight + 1; y <= GenerationConfig.getSeaLevel(); y++) {
                terrainHandler.placeWater(chunk, cx, y, cz);
            }
        }
    }

    private void addAgricultureFeatures(ChunkData chunk, int cx, int cz, int worldX, int worldZ, int surfaceHeight,
            String biome) {
        // Add agriculture features using random chance
        if (new Random(
                worldX * GenerationConfig.getAgricultureRandomX() + worldZ * GenerationConfig.getAgricultureRandomZ())
                .nextInt(GenerationConfig.getAgricultureFrequency()) == 0) {
            cc.farlanders.generate.agriculture.AgricultureManager.generateAgriculture(chunk, worldX, worldZ, biome,
                    surfaceHeight);
        }
    }

    private void addMobSpawningFeatures(ChunkData chunk, int cx, int cz, int worldX, int worldZ, String biome) {
        // Add mob spawning areas using random chance
        if (new Random(worldX * GenerationConfig.getSpawningRandomX() + worldZ * GenerationConfig.getSpawningRandomZ())
                .nextInt(GenerationConfig.getSpawningFrequency()) == 0) {
            cc.farlanders.generate.spawning.MobSpawningManager.generateMobSpawningAreas(chunk, worldX, worldZ, biome);
        }
    }

}