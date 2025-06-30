package cc.farlanders.generate.density;

import cc.farlanders.generate.config.GenerationConfig;
import cc.farlanders.noise.OpenSimplex2;

public class DensityFunction {

    public DensityFunction() {
        //
    }

    public double getCombinedDensity(int x, int y, int z, boolean farlands, double farlandsIntensity) {
        // Base terrain shape using proper 3D noise
        double terrainDensity = getTerrainDensity(x, y, z, farlands, farlandsIntensity);

        // Add caves and overhangs
        double caveDensity = getCaveDensity(x, y, z, farlandsIntensity);

        // Add floating sky islands at high altitudes
        double skyIslandDensity = getSkyIslandDensity(x, y, z, farlandsIntensity);

        // Combine densities
        double finalDensity = terrainDensity - caveDensity + skyIslandDensity;

        // Apply FarLands distortion
        if (farlands) {
            finalDensity = applyFarLandsDistortion(finalDensity, x, y, z, farlandsIntensity);
        }

        return Math.clamp(finalDensity, 0.0, 1.0);
    }

    public double getCombinedDensity(int x, int y, int z, boolean farlands) {
        return getCombinedDensity(x, y, z, farlands, farlands ? 1.0 : 0.0);
    }

    private double getTerrainDensity(int x, int y, int z, boolean farlands, double farlandsIntensity) {
        double baseScale = GenerationConfig.getBaseTerrainScale();
        double scale = farlands ? baseScale * (1 + farlandsIntensity * GenerationConfig.getTerrainScaleMultiplier())
                : baseScale;

        // Enhanced height-based density gradient for more realistic terrain
        double heightFactor = getHeightFactor(y);

        // Multi-octave terrain noise for realistic landscapes
        long terrainSeed = GenerationConfig.getTerrainSeed();
        double primaryNoise = OpenSimplex2.noise3ImproveXZ(terrainSeed,
                x * scale, y * scale * GenerationConfig.getYScalePrimary(), z * scale);
        double secondaryNoise = OpenSimplex2.noise3ImproveXZ(GenerationConfig.getSecondaryTerrainSeed(),
                x * scale * GenerationConfig.getSecondaryScaleMultiplier(),
                y * scale * GenerationConfig.getYScaleSecondary(),
                z * scale * GenerationConfig.getSecondaryScaleMultiplier()) * GenerationConfig.getSecondaryStrength();
        double detailNoise = OpenSimplex2.noise3ImproveXZ(GenerationConfig.getDetailTerrainSeed(),
                x * scale * GenerationConfig.getDetailScaleMultiplier(),
                y * scale,
                z * scale * GenerationConfig.getDetailScaleMultiplier()) * GenerationConfig.getDetailStrength();

        double combinedNoise = primaryNoise + secondaryNoise + detailNoise;

        // Add surface variation for more interesting terrain
        double surfaceVariation = getSurfaceVariation(x, z, y, scale);

        // Boost terrain height to ensure more land above sea level
        double heightBoost = y < GenerationConfig.getSurfaceLevel() ? GenerationConfig.getTerrainHeightBoost() : 0.0;

        return (combinedNoise + surfaceVariation) * heightFactor + (heightFactor - 0.2) + heightBoost;
    }

    private double getHeightFactor(int y) {
        if (y < GenerationConfig.getBedrockThreshold())
            return 1.0; // Solid bedrock
        if (y < GenerationConfig.getDeepStoneThreshold())
            return GenerationConfig.getDeepStoneDensity(); // Deep stone layer
        if (y < GenerationConfig.getSurfaceTransition())
            return 1.0 - (y / 180.0); // Gradual transition to surface
        if (y < GenerationConfig.getSurfaceLevel())
            return 0.75 - (y / 400.0); // Surface level - keep more land above sea level
        if (y < GenerationConfig.getHillsThreshold())
            return 0.6 - (y / 350.0); // Hills and mountains
        return Math.max(GenerationConfig.getHighAltitudeMin(), 0.4 - (y / 320.0)); // High altitude gets thinner
    }

    private double getSurfaceVariation(int x, int z, int y, double scale) {
        // Add hills and valleys
        double hillNoise = OpenSimplex2.noise2ImproveX(GenerationConfig.getHillsSeed(), x * scale * 0.3,
                z * scale * 0.3);
        double valleyNoise = OpenSimplex2.noise2ImproveX(GenerationConfig.getValleysSeed(), x * scale * 0.2,
                z * scale * 0.2);

        // Surface gets more variation around and above sea level
        double surfaceFactor = y > 30 && y < 120 ? GenerationConfig.getSurfaceFactorHigh()
                : GenerationConfig.getSurfaceFactorLow();

        // Create more elevation differences to ensure varied terrain heights
        double elevationNoise = OpenSimplex2.noise2ImproveX(GenerationConfig.getElevationSeed(), x * scale * 0.15,
                z * scale * 0.15);

        return (hillNoise * 0.4 + valleyNoise * 0.3 + elevationNoise * 0.5) * surfaceFactor;
    }

    private double getCaveDensity(int x, int y, int z, double farlandsIntensity) {
        if (y < GenerationConfig.getBedrockThreshold())
            return 0; // No caves in bedrock

        double caveScale = GenerationConfig.getCaveScale() * (1 + farlandsIntensity * 0.8);

        // Create multiple cave systems at different scales
        double largeCaves = OpenSimplex2.noise3ImproveXZ(GenerationConfig.getLargeCavesSeed(),
                x * caveScale * GenerationConfig.getLargeCaveScale(),
                y * caveScale * 0.6,
                z * caveScale * GenerationConfig.getLargeCaveScale());
        double mediumCaves = OpenSimplex2.noise3ImproveXZ(GenerationConfig.getMediumCavesSeed(),
                x * caveScale * GenerationConfig.getMediumCaveScale(),
                y * caveScale * 0.8,
                z * caveScale * GenerationConfig.getMediumCaveScale());
        double smallCaves = OpenSimplex2.noise3ImproveXZ(GenerationConfig.getSmallCavesSeed(),
                x * caveScale * GenerationConfig.getSmallCaveScale(),
                y * caveScale * 1.5,
                z * caveScale * GenerationConfig.getSmallCaveScale());

        // Combine cave systems for complex networks
        double combinedCaves = Math.abs(largeCaves) * Math.abs(mediumCaves) +
                Math.abs(smallCaves) * GenerationConfig.getSmallCaveStrength();

        // Depth-based cave probability
        double depthFactor = getDepthFactor(y);

        // Add rare large caverns
        double cavernNoise = OpenSimplex2.noise3ImproveXZ(GenerationConfig.getCavernsSeed(),
                x * caveScale * 0.3, y * caveScale * 0.2, z * caveScale * 0.3);
        double caverns = Math.abs(cavernNoise) > GenerationConfig.getCavernThreshold()
                ? GenerationConfig.getCavernDensity()
                : 0.0;

        return Math.max(combinedCaves * depthFactor * GenerationConfig.getCaveDensityMultiplier(),
                caverns * depthFactor);
    }

    private double getDepthFactor(int y) {
        if (y < GenerationConfig.getNoCaveThreshold())
            return 0.0; // No caves in bedrock
        if (y < GenerationConfig.getFewCaveThreshold())
            return GenerationConfig.getFewCaveFactor(); // Few caves near bedrock
        if (y < GenerationConfig.getPeakCaveThreshold())
            return GenerationConfig.getPeakCaveFactor(); // Many caves in stone layer
        if (y < GenerationConfig.getModerateCaveThreshold())
            return GenerationConfig.getModerateCaveFactor(); // Moderate caves near surface
        return GenerationConfig.getSurfaceCaveFactor(); // Fewer caves above sea level
    }

    private double applyFarLandsDistortion(double density, int x, int y, int z, double intensity) {
        // Classic FarLands coordinate overflow simulation with enhanced chaos
        double coordScale = GenerationConfig.getCoordScaleBase() * intensity;

        // Create coordinate-based distortions (simulating integer overflow)
        double distortionX = Math.sin(x * coordScale) * intensity * GenerationConfig.getDistortionXStrength();
        double distortionZ = Math.cos(z * coordScale) * intensity * GenerationConfig.getDistortionZStrength();
        double distortionY = Math.sin(y * coordScale * 2) * intensity * GenerationConfig.getDistortionYStrength();

        // Add sharp spikes and valleys characteristic of FarLands
        double spikeNoise = OpenSimplex2.noise3ImproveXZ(GenerationConfig.getFarlandsSpikesSeed(),
                x * coordScale * GenerationConfig.getSpikeCoordScale(),
                y * coordScale * GenerationConfig.getSpikeYScale(),
                z * coordScale * GenerationConfig.getSpikeCoordScale());
        double spikiness = Math.pow(Math.abs(spikeNoise), 2) * Math.signum(spikeNoise);

        // Create "walls" and "sheets" of terrain
        double wallNoise = Math.abs(OpenSimplex2.noise2ImproveX(GenerationConfig.getFarlandsWallsSeed(),
                x * coordScale * GenerationConfig.getWallCoordScale(),
                z * coordScale * GenerationConfig.getWallZScale()));
        double walls = wallNoise > GenerationConfig.getWallThreshold() ? GenerationConfig.getWallHeight() : 0.0;

        // Amplify density chaotically
        double chaosMultiplier = 1.0 + (distortionX + distortionZ + distortionY);

        // Combine all distortions
        double distortedDensity = density * chaosMultiplier +
                spikiness * intensity * GenerationConfig.getFarlandsSpikeIntensity() +
                walls * intensity * GenerationConfig.getFarlandsWallIntensity();

        // Add some "glitchy" terrain formations
        double glitchNoise = OpenSimplex2.noise3ImproveXZ(GenerationConfig.getFarlandsGlitchSeed(),
                x * coordScale * GenerationConfig.getGlitchCoordScale(),
                y * coordScale * GenerationConfig.getGlitchYScale(),
                z * coordScale * GenerationConfig.getGlitchCoordScale());
        if (Math.abs(glitchNoise) > GenerationConfig.getGlitchThreshold()) {
            distortedDensity += Math.signum(glitchNoise) * intensity * GenerationConfig.getGlitchIntensity();
        }

        return distortedDensity;
    }

    /**
     * Generates rare floating sky islands at high altitudes (Y 200-280)
     * These islands contain rare ores and are sparsely distributed
     */
    private double getSkyIslandDensity(int x, int y, int z, double farlandsIntensity) {
        // Only generate sky islands at configured altitudes
        if (y < GenerationConfig.getSkyIslandMinHeight() || y > GenerationConfig.getSkyIslandMaxHeight()) {
            return 0.0;
        }

        // Make sky islands very rare using configured location scale
        double islandLocationNoise = OpenSimplex2.noise2ImproveX(GenerationConfig.getSkyLocationSeed(),
                x * GenerationConfig.getLocationScale(), z * GenerationConfig.getLocationScale());

        // Only generate islands where the location noise exceeds configured threshold
        if (Math.abs(islandLocationNoise) < GenerationConfig.getSkyIslandRarityThreshold()) {
            return 0.0;
        }

        // Create the island shape using 3D noise
        double islandScale = GenerationConfig.getSkyIslandScale();
        double islandShape = OpenSimplex2.noise3ImproveXZ(GenerationConfig.getSkyShapeSeed(),
                x * islandScale, y * islandScale * GenerationConfig.getShapeYScale(), z * islandScale);

        // Add some variation and make islands more blob-like
        double variation = OpenSimplex2.noise3ImproveXZ(GenerationConfig.getSkyVariationSeed(),
                x * islandScale * GenerationConfig.getVariationScale(),
                y * islandScale * GenerationConfig.getVariationScale(),
                z * islandScale * GenerationConfig.getVariationScale()) * GenerationConfig.getVariationStrength();

        // Create a falloff based on distance from island center
        double centerX = Math.round(x / GenerationConfig.getCenterGridSize()) * GenerationConfig.getCenterGridSize();
        double centerZ = Math.round(z / GenerationConfig.getCenterGridSize()) * GenerationConfig.getCenterGridSize();
        double distanceFromCenter = Math.sqrt((x - centerX) * (x - centerX) + (z - centerZ) * (z - centerZ));
        double distanceFalloff = Math.max(0, 1.0 - distanceFromCenter / GenerationConfig.getSkyIslandDistanceFalloff());

        // Altitude-based falloff to make islands thicker in the middle
        double altitudeFalloff = 1.0
                - Math.abs(y - GenerationConfig.getCenterAltitude()) / GenerationConfig.getAltitudeFalloff();
        altitudeFalloff = Math.max(0, altitudeFalloff);

        // FarLands intensity makes sky islands slightly more common and chaotic
        double farlandsBonus = farlandsIntensity * GenerationConfig.getFarlandsBonus();

        return Math.max(0, (islandShape + variation + farlandsBonus) * distanceFalloff * altitudeFalloff);
    }
}