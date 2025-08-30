package cc.farlanders.generate.biomes.modules;

import cc.farlanders.generate.biomes.api.BiomeModule;
import cc.farlanders.generate.biomes.api.BiomePreset;

public class CrystalCavernsModule implements BiomeModule {

    @Override
    public String id() {
        return "CrystalCaverns";
    }

    @Override
    public double weight() {
        return 20.0;
    }

    @Override
    public double terrainHeightMultiplier() {
        return 0.5;
    }

    @Override
    public double vegetationDensity() {
        return 0.05;
    }

    @Override
    public void onGenerateColumn(int worldX, int worldZ, ColumnGenerationContext ctx) {
        // Crystal caverns prefer deeper caves: lower surface height slightly
        ctx.setSurfaceHeight(Math.max(1, ctx.getSurfaceHeight() - 6));
    }

    @Override
    public BiomePreset preset() {
        return new BiomePreset(vegetationDensity(), java.util.List.of("crystal_pillar", "cavern_entrance"));
    }

}
