package cc.farlanders.generate.biomes.api;

import java.util.List;
import java.util.Map;

/**
 * Simple data holder for per-biome generation presets.
 */
public final class BiomePreset {
    private final double vegetationDensity;
    private final List<String> preferredStructures; // simple identifiers
    private final Map<String, Double> structureRarity; // 0.0-1.0 per-structure
    private final Map<String, List<String>> structureVariants; // variant ids per structure
    private final List<String> vegetationTypes; // preferred vegetation categories
    private final double waterBias; // -1.0..1.0, negative = dryer, positive = wetter
    private final double temperatureModifier; // -1.0..1.0
    private final Map<String, Double> resourceBonuses; // resource id -> bonus multiplier

    private BiomePreset(Builder b) {
        this.vegetationDensity = b.vegetationDensity;
        this.preferredStructures = b.preferredStructures;
        this.structureRarity = b.structureRarity;
        this.structureVariants = b.structureVariants;
        this.vegetationTypes = b.vegetationTypes;
        this.waterBias = b.waterBias;
        this.temperatureModifier = b.temperatureModifier;
        this.resourceBonuses = b.resourceBonuses;
    }

    /**
     * Backwards-compatible convenience constructor used by existing modules.
     */
    public BiomePreset(double vegetationDensity, List<String> preferredStructures) {
        this(BiomePreset.builder()
                .vegetationDensity(vegetationDensity)
                .preferredStructures(preferredStructures));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private double vegetationDensity = 0.0;
        private List<String> preferredStructures = List.of();
        private Map<String, Double> structureRarity = Map.of();
        private Map<String, List<String>> structureVariants = Map.of();
        private List<String> vegetationTypes = List.of();
        private double waterBias = 0.0;
        private double temperatureModifier = 0.0;
        private Map<String, Double> resourceBonuses = Map.of();

        public Builder vegetationDensity(double d) {
            this.vegetationDensity = d;
            return this;
        }

        public Builder preferredStructures(List<String> s) {
            this.preferredStructures = s;
            return this;
        }

        public Builder structureRarity(Map<String, Double> m) {
            this.structureRarity = m;
            return this;
        }

        public Builder structureVariants(Map<String, List<String>> m) {
            this.structureVariants = m;
            return this;
        }

        public Builder vegetationTypes(List<String> t) {
            this.vegetationTypes = t;
            return this;
        }

        public Builder waterBias(double b) {
            this.waterBias = b;
            return this;
        }

        public Builder temperatureModifier(double t) {
            this.temperatureModifier = t;
            return this;
        }

        public Builder resourceBonuses(Map<String, Double> m) {
            this.resourceBonuses = m;
            return this;
        }

        public BiomePreset build() {
            return new BiomePreset(this);
        }
    }

    public double vegetationDensity() {
        return vegetationDensity;
    }

    public List<String> preferredStructures() {
        return preferredStructures;
    }

    public Map<String, Double> structureRarity() {
        return structureRarity;
    }

    public Map<String, List<String>> structureVariants() {
        return structureVariants;
    }

    public List<String> vegetationTypes() {
        return vegetationTypes;
    }

    public double waterBias() {
        return waterBias;
    }

    public double temperatureModifier() {
        return temperatureModifier;
    }

    public Map<String, Double> resourceBonuses() {
        return resourceBonuses;
    }
}
