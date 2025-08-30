package cc.farlanders.generate.biomes.modules;

import cc.farlanders.generate.biomes.api.BiomeModule;
import cc.farlanders.generate.biomes.api.BiomePreset;
import cc.farlanders.generate.config.GenerationConfig;

public class FarLandsModule implements BiomeModule {

    @Override
    public String id() {
        return "FarLands";
    }

    @Override
    public double weight() {
        return 28.0; // relative weight matching GOAL.md
    }

    @Override
    public double terrainHeightMultiplier() {
        return 2.0; // very tall/extreme FarLands
    }

    @Override
    public double vegetationDensity() {
        return 0.1; // very sparse vegetation
    }

    @Override
    public BiomePreset preset() {
        return new BiomePreset(vegetationDensity(), java.util.List.of("pillar", "legendary", "obsidian_spire"));
    }

    @Override
    public String selectForCoords(int x, int z) {
        double dist = Math.sqrt((double) x * x + (double) z * z);
        if (dist > GenerationConfig.getFarlandsThreshold()) {
            return id();
        }
        return null;
    }
}
