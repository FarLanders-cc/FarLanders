package cc.farlanders.generate.progression;

/**
 * Small value object describing a requirement that ties biomes together.
 */
public record BiomeRequirement(String biomeId, String requiredItemId) {
}
