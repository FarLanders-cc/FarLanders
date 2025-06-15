package cc.farlanders.generate;

import cc.farlanders.noise.OpenSimplex2S;

public class TerrainGenerate {
    private final OpenSimplex2S noise;
    private final int seaLevel;
    private final double farLandsThreshold;

    public TerrainGenerate(long seed, int seaLevel, double farLandsThreshold) {
        this.noise = new OpenSimplex2S(seed);
        this.seaLevel = seaLevel;
        this.farLandsThreshold = farLandsThreshold;
    }

    public int getHeightAt(int x, int z) {
        double noiseX = x * 0.01;
        double noiseZ = z * 0.01;

        // Simulate Far Lands corruption
        if (Math.abs(x) > farLandsThreshold || Math.abs(z) > farLandsThreshold) {
            noiseX *= 100; // Exaggerated distortion
            noiseZ *= 100;
        }

        @SuppressWarnings("static-access")
        double elevation = noise.noise2(0L, noiseX, noiseZ) * 20 + seaLevel;
        return (int) Math.max(elevation, 0);
    }
}
