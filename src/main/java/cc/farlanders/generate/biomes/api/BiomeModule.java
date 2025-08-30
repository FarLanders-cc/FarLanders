package cc.farlanders.generate.biomes.api;

/**
 * Lightweight interface representing a high-level biome module.
 * Implementations should be small descriptors + optional hooks for generation
 * or selection.
 */
public interface BiomeModule {
    /**
     * Unique id for the biome module (use simple names like "FarLands",
     * "MysticForest").
     */
    String id();

    /**
     * Relative weight/probability for procedural selection. Higher = more likely.
     */
    double weight();

    /**
     * Optional: allow modules (or adapters) to select a biome deterministically for
     * coordinates.
     * Return null to indicate no opinion (registry will use fallback selection).
     */
    default String selectForCoords(int x, int z) {
        return null;
    }

    /**
     * Lifecycle hook for module registration.
     */
    default void register() {
        // no-op default
    }

    /**
     * Relative terrain height multiplier for this biome (1.0 = default).
     */
    default double terrainHeightMultiplier() {
        return 1.0;
    }

    /**
     * Vegetation density modifier (0.0-1.0) controlling flora chance.
     */
    default double vegetationDensity() {
        return 1.0;
    }

    /**
     * Optional hook called during column generation to allow biome modules to
     * tweak column data. Implementations should be lightweight.
     */
    default void onGenerateColumn(int worldX, int worldZ, ColumnGenerationContext ctx) {
        // no-op
    }

    /**
     * Small context passed to biome modules on generation.
     */
    final class ColumnGenerationContext {
        private int surfaceHeight;

        public int getSurfaceHeight() {
            return surfaceHeight;
        }

        public void setSurfaceHeight(int h) {
            this.surfaceHeight = h;
        }
    }

    /**
     * Provide a simple preset for structures/vegetation preferences.
     */
    default BiomePreset preset() {
        return new BiomePreset(vegetationDensity(), java.util.List.of());
    }
}
