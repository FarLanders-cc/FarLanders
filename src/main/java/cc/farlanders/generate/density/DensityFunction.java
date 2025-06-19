package cc.farlanders.generate.density;

import cc.farlanders.noise.OpenSimplex2;

public class DensityFunction {

    private static final double BASE_SCALE = 0.01;
    private static final double FRACTAL_AMPLITUDE = 0.3;

    public DensityFunction() {
        //
    }

    public double getCombinedDensity(int x, int y, int z, boolean farlands) {
        // Base noise
        double baseDensity = terrainNoise(x, y, z, farlands);

        // Fractal overlay to enhance complexity
        double fractal = 0;
        double amp = FRACTAL_AMPLITUDE;
        double scale = BASE_SCALE;
        for (int i = 0; i < 4; i++) {
            fractal += amp * OpenSimplex2.noise2(x * scale, y * scale, z * scale);
            amp *= 0.5;
            scale *= 2;
        }

        double density = baseDensity + fractal;
        return Math.clamp(density, 0.0, 1.0);
    }

    private double terrainNoise(int x, int y, int z, boolean farlands) {
        double scale = farlands ? 0.05 : 0.01;
        double height = y / 256.0;
        double noiseVal = OpenSimplex2.noise2(x * scale, y * scale, z * scale);
        return 0.5 + noiseVal * (1.0 - height);
    }
}