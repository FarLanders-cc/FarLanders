package cc.farlanders.generate.density;

import cc.farlanders.noise.OpenSimplex2;

public class DensityFunction {

    private static final double BASE_SCALE = 0.005;
    private static final long NOISE_SEED = 42L;

    public DensityFunction() {
        //
    }

    public double getCombinedDensity(int x, int y, int z, boolean farlands, double farlandsIntensity) {
        // Base terrain shape using proper 3D noise
        double terrainDensity = getTerrainDensity(x, y, z, farlands, farlandsIntensity);

        // Add caves and overhangs
        double caveDensity = getCaveDensity(x, y, z, farlandsIntensity);

        // Combine densities
        double finalDensity = terrainDensity - caveDensity;

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
        double scale = farlands ? BASE_SCALE * (1 + farlandsIntensity * 3) : BASE_SCALE;

        // Enhanced height-based density gradient for more realistic terrain
        double heightFactor = getHeightFactor(y);

        // Multi-octave terrain noise for realistic landscapes
        double primaryNoise = OpenSimplex2.noise3ImproveXZ(NOISE_SEED, x * scale, y * scale * 0.3, z * scale);
        double secondaryNoise = OpenSimplex2.noise3ImproveXZ(NOISE_SEED + 1000,
                x * scale * 2, y * scale * 0.5, z * scale * 2) * 0.5;
        double detailNoise = OpenSimplex2.noise3ImproveXZ(NOISE_SEED + 2000,
                x * scale * 4, y * scale, z * scale * 4) * 0.25;

        double combinedNoise = primaryNoise + secondaryNoise + detailNoise;

        // Add surface variation for more interesting terrain
        double surfaceVariation = getSurfaceVariation(x, z, y, scale);

        // Boost terrain height to ensure more land above sea level
        double heightBoost = y < 80 ? 0.3 : 0.0; // Extra density boost near surface

        return (combinedNoise + surfaceVariation) * heightFactor + (heightFactor - 0.2) + heightBoost;
    }

    private double getHeightFactor(int y) {
        if (y < 5)
            return 1.0; // Solid bedrock
        if (y < 15)
            return 0.95; // Deep stone layer
        if (y < 45)
            return 1.0 - (y / 180.0); // Gradual transition to surface
        if (y < 80)
            return 0.75 - (y / 400.0); // Surface level - keep more land above sea level
        if (y < 140)
            return 0.6 - (y / 350.0); // Hills and mountains
        return Math.max(0.1, 0.4 - (y / 320.0)); // High altitude gets thinner
    }

    private double getSurfaceVariation(int x, int z, int y, double scale) {
        // Add hills and valleys
        double hillNoise = OpenSimplex2.noise2ImproveX(NOISE_SEED + 3000, x * scale * 0.3, z * scale * 0.3);
        double valleyNoise = OpenSimplex2.noise2ImproveX(NOISE_SEED + 4000, x * scale * 0.2, z * scale * 0.2);

        // Surface gets more variation around and above sea level
        double surfaceFactor = y > 30 && y < 120 ? 1.2 : 0.4;

        // Create more elevation differences to ensure varied terrain heights
        double elevationNoise = OpenSimplex2.noise2ImproveX(NOISE_SEED + 5000, x * scale * 0.15, z * scale * 0.15);

        return (hillNoise * 0.4 + valleyNoise * 0.3 + elevationNoise * 0.5) * surfaceFactor;
    }

    private double getCaveDensity(int x, int y, int z, double farlandsIntensity) {
        if (y < 5)
            return 0; // No caves in bedrock

        double caveScale = 0.015 * (1 + farlandsIntensity * 0.8);

        // Create multiple cave systems at different scales
        double largeCaves = OpenSimplex2.noise3ImproveXZ(NOISE_SEED + 2000,
                x * caveScale * 0.7, y * caveScale * 0.6, z * caveScale * 0.7);
        double mediumCaves = OpenSimplex2.noise3ImproveXZ(NOISE_SEED + 3000,
                x * caveScale * 1.2, y * caveScale * 0.8, z * caveScale * 1.2);
        double smallCaves = OpenSimplex2.noise3ImproveXZ(NOISE_SEED + 4000,
                x * caveScale * 2.0, y * caveScale * 1.5, z * caveScale * 2.0);

        // Combine cave systems for complex networks
        double combinedCaves = Math.abs(largeCaves) * Math.abs(mediumCaves) +
                Math.abs(smallCaves) * 0.3;

        // Depth-based cave probability
        double depthFactor = getDepthFactor(y);

        // Add rare large caverns
        double cavernNoise = OpenSimplex2.noise3ImproveXZ(NOISE_SEED + 5000,
                x * caveScale * 0.3, y * caveScale * 0.2, z * caveScale * 0.3);
        double caverns = Math.abs(cavernNoise) > 0.7 ? 0.8 : 0.0;

        return Math.max(combinedCaves * depthFactor * 0.4, caverns * depthFactor);
    }

    private double getDepthFactor(int y) {
        if (y < 10)
            return 0.0; // No caves in bedrock
        if (y < 20)
            return 0.3; // Few caves near bedrock
        if (y < 50)
            return 1.5; // Many caves in stone layer
        if (y < 64)
            return 1.0; // Moderate caves near surface
        return 0.5; // Fewer caves above sea level
    }

    private double applyFarLandsDistortion(double density, int x, int y, int z, double intensity) {
        // Classic FarLands coordinate overflow simulation with enhanced chaos
        double coordScale = 0.00001 * intensity;

        // Create coordinate-based distortions (simulating integer overflow)
        double distortionX = Math.sin(x * coordScale) * intensity * 0.4;
        double distortionZ = Math.cos(z * coordScale) * intensity * 0.4;
        double distortionY = Math.sin(y * coordScale * 2) * intensity * 0.3;

        // Add sharp spikes and valleys characteristic of FarLands
        double spikeNoise = OpenSimplex2.noise3ImproveXZ(NOISE_SEED + 6000,
                x * coordScale * 200, y * coordScale * 100, z * coordScale * 200);
        double spikiness = Math.pow(Math.abs(spikeNoise), 2) * Math.signum(spikeNoise);

        // Create "walls" and "sheets" of terrain
        double wallNoise = Math.abs(OpenSimplex2.noise2ImproveX(NOISE_SEED + 7000,
                x * coordScale * 500, z * coordScale * 300));
        double walls = wallNoise > 0.8 ? 1.5 : 0.0;

        // Amplify density chaotically
        double chaosMultiplier = 1.0 + (distortionX + distortionZ + distortionY);

        // Combine all distortions
        double distortedDensity = density * chaosMultiplier +
                spikiness * intensity * 0.6 +
                walls * intensity * 0.4;

        // Add some "glitchy" terrain formations
        double glitchNoise = OpenSimplex2.noise3ImproveXZ(NOISE_SEED + 8000,
                x * coordScale * 1000, y * coordScale * 500, z * coordScale * 1000);
        if (Math.abs(glitchNoise) > 0.9) {
            distortedDensity += Math.signum(glitchNoise) * intensity * 0.8;
        }

        return distortedDensity;
    }
}