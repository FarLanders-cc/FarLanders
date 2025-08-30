package cc.farlanders.generate.biomes.modules;

import cc.farlanders.generate.biomes.api.BiomeModule;
import cc.farlanders.generate.biomes.api.BiomePreset;

public class MysticForestModule implements BiomeModule {

    @Override
    public String id() {
        return "MysticForest";
    }

    @Override
    public double weight() {
        return 22.0;
    }

    @Override
    public double terrainHeightMultiplier() {
        return 1.05;
    }

    @Override
    public double vegetationDensity() {
        return 1.4;
    }

    @Override
    public void register() {
        // module registration hook could pre-load assets or recipes
    }

    @Override
    public BiomePreset preset() {
        return new BiomePreset(vegetationDensity(), java.util.List.of("grove_shrine", "orchard"));
    }

}
