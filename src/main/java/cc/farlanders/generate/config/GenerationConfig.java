package cc.farlanders.generate.config;

/**
 * Configuration constants for FarLands world generation
 */
public final class GenerationConfig {

    // Core world settings
    public static final int SEA_LEVEL = 64;
    public static final int MAX_HEIGHT = 320;
    public static final double FARLANDS_THRESHOLD = 12550820.0; // Classic FarLands distance

    // Noise generation settings
    public static final double BASE_TERRAIN_SCALE = 0.005;
    public static final double CAVE_SCALE = 0.015;
    public static final double BIOME_SCALE = 0.003;

    // Generation intensity settings
    public static final double CAVE_DENSITY_MULTIPLIER = 0.4;
    public static final double FARLANDS_DISTORTION_INTENSITY = 0.6;
    public static final double SURFACE_VARIATION_INTENSITY = 0.3;

    // Ore generation probabilities (higher values = more common)
    public static final double DIAMOND_ORE_THRESHOLD = 0.85;
    public static final double GOLD_ORE_THRESHOLD = 0.8;
    public static final double IRON_ORE_THRESHOLD = 0.75;
    public static final double COPPER_ORE_THRESHOLD = 0.7;
    public static final double COAL_ORE_THRESHOLD = 0.6;
    public static final double REDSTONE_ORE_THRESHOLD = 0.5;

    // Vegetation settings
    public static final double TREE_GENERATION_CHANCE = 0.1;
    public static final double GRASS_GENERATION_CHANCE = 0.3;

    // Structure generation
    public static final double STRUCTURE_CHANCE = 0.01; // 1%
    public static final double LEGENDARY_STRUCTURE_CHANCE = 0.001; // 0.1%

    // Noise seeds (for consistent generation)
    public static final long TERRAIN_SEED = 42L;
    public static final long CAVE_SEED = 1337L;
    public static final long BIOME_SEED = 2021L;
    public static final long ORE_SEED = 404L;

    // Terrain elevation settings
    public static final double TERRAIN_HEIGHT_BOOST = 0.3; // Extra density to keep land above sea level
    public static final double SURFACE_ELEVATION_FACTOR = 1.2; // Amplify surface variation for hills/valleys

    private GenerationConfig() {
        // Utility class - no instantiation
    }
}
