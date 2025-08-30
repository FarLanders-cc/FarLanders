package cc.farlanders.generate.biomes.modules;

import cc.farlanders.generate.biomes.api.BiomeModule;
import cc.farlanders.generate.biomes.api.BiomePreset;

public class AshenWastesModule implements BiomeModule {

    @Override
    public String id() {
        return "AshenWastes";
    }

    @Override
    public double weight() {
        return 21.0;
    }

    @Override
    public double terrainHeightMultiplier() {
        return 0.85;
    }

    @Override
    public double vegetationDensity() {
        return 0.02;
    }

    @Override
    public void register() {
        // Could register heat-resistant structure definitions later
    }

    @Override
    public BiomePreset preset() {
        return new BiomePreset(vegetationDensity(), java.util.List.of("ash_spire", "lava_pool"));
    }

}
