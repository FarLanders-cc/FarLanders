package cc.farlanders.generate.biomes.modules;

import cc.farlanders.generate.biomes.api.BiomeModule;
import cc.farlanders.generate.biomes.api.BiomePreset;

public class SunkenRuinsModule implements BiomeModule {

    @Override
    public String id() {
        return "SunkenRuins";
    }

    @Override
    public double weight() {
        return 29.0;
    }

    @Override
    public double terrainHeightMultiplier() {
        return 0.45;
    }

    @Override
    public double vegetationDensity() {
        return 0.2;
    }

    @Override
    public void onGenerateColumn(int worldX, int worldZ, ColumnGenerationContext ctx) {
        // Encourage lower surface heights and water features
        // Lower the surface slightly but never below 1 to avoid invalid indices
        ctx.setSurfaceHeight(Math.max(1, ctx.getSurfaceHeight() - 2));
    }

    @Override
    public BiomePreset preset() {
        return new BiomePreset(vegetationDensity(), java.util.List.of("ruin_fragment", "underwater_altar"));
    }

}
