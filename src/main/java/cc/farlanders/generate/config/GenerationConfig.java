package cc.farlanders.generate.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Configuration manager for FarLands world generation
 * Loads values from config.yml instead of hardcoded constants
 */
public final class GenerationConfig {

    // Private constructor to prevent instantiation
    private GenerationConfig() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private static FileConfiguration config;
    private static boolean initialized = false;

    // Cache commonly used values for performance
    private static int seaLevel;
    private static int maxHeight;
    private static double farlandsThreshold;
    private static double baseTerrainScale;
    private static double caveScale;
    private static double biomeScale;

    /**
     * Initialize the config with a plugin instance
     */
    public static void initialize(Plugin plugin) {
        config = plugin.getConfig();
        plugin.saveDefaultConfig(); // Ensures config.yml exists
        loadCachedValues();
        initialized = true;
    }

    /**
     * Load frequently accessed values into cache for performance
     */
    private static void loadCachedValues() {
        seaLevel = config.getInt("world.sea-level", 64);
        maxHeight = config.getInt("world.max-height", 320);
        farlandsThreshold = config.getDouble("world.farlands-threshold", 12550820.0);
        baseTerrainScale = config.getDouble("terrain.base-scale", 0.005);
        caveScale = config.getDouble("terrain.cave-scale", 0.015);
        biomeScale = config.getDouble("terrain.biome-scale", 0.003);
    }

    /**
     * Reload configuration from file
     */
    public static void reload(Plugin plugin) {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadCachedValues();
    }

    private static void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("GenerationConfig must be initialized before use!");
        }
    }

    // =================================
    // CORE WORLD SETTINGS
    // =================================

    public static int getSeaLevel() {
        return seaLevel;
    }

    public static int getMaxHeight() {
        return maxHeight;
    }

    public static double getFarlandsThreshold() {
        return farlandsThreshold;
    }

    // World generation settings
    public static int getChunkSize() {
        checkInitialized();
        return config.getInt("world.chunk-size", 16);
    }

    public static double getTerrainDensityThreshold() {
        checkInitialized();
        return config.getDouble("world.terrain-density-threshold", 0.5);
    }

    public static double getFarlandsIntensityDivisor() {
        checkInitialized();
        return config.getDouble("world.farlands-intensity-divisor", 1000000.0);
    }

    // =================================
    // TERRAIN GENERATION
    // =================================

    public static double getBaseTerrainScale() {
        return baseTerrainScale;
    }

    public static double getCaveScale() {
        return caveScale;
    }

    public static double getBiomeScale() {
        return biomeScale;
    }

    public static double getTerrainHeightBoost() {
        checkInitialized();
        return config.getDouble("terrain.height-boost", 0.3);
    }

    public static double getSurfaceVariationFactor() {
        checkInitialized();
        return config.getDouble("terrain.surface-variation", 1.2);
    }

    // Terrain factors and thresholds
    public static double getSurfaceFactorHigh() {
        checkInitialized();
        return config.getDouble("terrain.surface-factor-high", 1.2);
    }

    public static double getSurfaceFactorLow() {
        checkInitialized();
        return config.getDouble("terrain.surface-factor-low", 0.4);
    }

    public static double getYScalePrimary() {
        checkInitialized();
        return config.getDouble("terrain.y-scale-primary", 0.3);
    }

    public static double getYScaleSecondary() {
        checkInitialized();
        return config.getDouble("terrain.y-scale-secondary", 0.5);
    }

    public static double getSecondaryScaleMultiplier() {
        checkInitialized();
        return config.getDouble("terrain.secondary-scale-multiplier", 2.0);
    }

    public static double getSecondaryStrength() {
        checkInitialized();
        return config.getDouble("terrain.secondary-strength", 0.5);
    }

    public static double getDetailScaleMultiplier() {
        checkInitialized();
        return config.getDouble("terrain.detail-scale-multiplier", 4.0);
    }

    public static double getDetailStrength() {
        checkInitialized();
        return config.getDouble("terrain.detail-strength", 0.25);
    }

    public static int getBedrockThreshold() {
        checkInitialized();
        return config.getInt("terrain.bedrock-threshold", 5);
    }

    public static int getDeepStoneThreshold() {
        checkInitialized();
        return config.getInt("terrain.deep-stone-threshold", 15);
    }

    public static double getDeepStoneDensity() {
        checkInitialized();
        return config.getDouble("terrain.deep-stone-density", 0.95);
    }

    public static int getSurfaceTransition() {
        checkInitialized();
        return config.getInt("terrain.surface-transition", 45);
    }

    public static int getSurfaceLevel() {
        checkInitialized();
        return config.getInt("terrain.surface-level", 80);
    }

    public static int getHillsThreshold() {
        checkInitialized();
        return config.getInt("terrain.hills-threshold", 140);
    }

    public static double getHighAltitudeMin() {
        checkInitialized();
        return config.getDouble("terrain.high-altitude-min", 0.1);
    }

    // =================================
    // FARLANDS SETTINGS
    // =================================

    public static double getFarlandsDistortionIntensity() {
        checkInitialized();
        return config.getDouble("farlands.distortion-intensity", 0.6);
    }

    public static double getFarlandsSpikeIntensity() {
        checkInitialized();
        return config.getDouble("farlands.spike-intensity", 0.6);
    }

    public static double getFarlandsWallIntensity() {
        checkInitialized();
        return config.getDouble("farlands.wall-intensity", 0.4);
    }

    // FarLands coordinate scaling
    public static double getCoordScaleBase() {
        checkInitialized();
        return config.getDouble("farlands.coord-scale-base", 0.00001);
    }

    public static double getTerrainScaleMultiplier() {
        checkInitialized();
        return config.getDouble("farlands.terrain-scale-multiplier", 3.0);
    }

    // FarLands distortion factors
    public static double getDistortionXStrength() {
        checkInitialized();
        return config.getDouble("farlands.distortion-x-strength", 0.4);
    }

    public static double getDistortionZStrength() {
        checkInitialized();
        return config.getDouble("farlands.distortion-z-strength", 0.4);
    }

    public static double getDistortionYStrength() {
        checkInitialized();
        return config.getDouble("farlands.distortion-y-strength", 0.3);
    }

    // FarLands spikes and walls
    public static double getSpikeCoordScale() {
        checkInitialized();
        return config.getDouble("farlands.spike-coord-scale", 200.0);
    }

    public static double getSpikeYScale() {
        checkInitialized();
        return config.getDouble("farlands.spike-y-scale", 100.0);
    }

    public static double getWallCoordScale() {
        checkInitialized();
        return config.getDouble("farlands.wall-coord-scale", 500.0);
    }

    public static double getWallZScale() {
        checkInitialized();
        return config.getDouble("farlands.wall-z-scale", 300.0);
    }

    public static double getWallThreshold() {
        checkInitialized();
        return config.getDouble("farlands.wall-threshold", 0.8);
    }

    public static double getWallHeight() {
        checkInitialized();
        return config.getDouble("farlands.wall-height", 1.5);
    }

    // FarLands glitch effects
    public static double getGlitchCoordScale() {
        checkInitialized();
        return config.getDouble("farlands.glitch-coord-scale", 1000.0);
    }

    public static double getGlitchYScale() {
        checkInitialized();
        return config.getDouble("farlands.glitch-y-scale", 500.0);
    }

    public static double getGlitchThreshold() {
        checkInitialized();
        return config.getDouble("farlands.glitch-threshold", 0.9);
    }

    public static double getGlitchIntensity() {
        checkInitialized();
        return config.getDouble("farlands.glitch-intensity", 0.8);
    }
    // =================================
    // CAVE GENERATION
    // =================================

    public static double getCaveDensityMultiplier() {
        checkInitialized();
        return config.getDouble("caves.density-multiplier", 0.4);
    }

    public static double getLargeCaveScale() {
        checkInitialized();
        return config.getDouble("caves.large-cave-scale", 0.7);
    }

    public static double getMediumCaveScale() {
        checkInitialized();
        return config.getDouble("caves.medium-cave-scale", 1.2);
    }

    public static double getSmallCaveScale() {
        checkInitialized();
        return config.getDouble("caves.small-cave-scale", 2.0);
    }

    // Cave depth factors
    public static int getNoCaveThreshold() {
        checkInitialized();
        return config.getInt("caves.no-cave-threshold", 10);
    }

    public static int getFewCaveThreshold() {
        checkInitialized();
        return config.getInt("caves.few-cave-threshold", 20);
    }

    public static double getFewCaveFactor() {
        checkInitialized();
        return config.getDouble("caves.few-cave-factor", 0.3);
    }

    public static int getPeakCaveThreshold() {
        checkInitialized();
        return config.getInt("caves.peak-cave-threshold", 50);
    }

    public static double getPeakCaveFactor() {
        checkInitialized();
        return config.getDouble("caves.peak-cave-factor", 1.5);
    }

    public static int getModerateCaveThreshold() {
        checkInitialized();
        return config.getInt("caves.moderate-cave-threshold", 64);
    }

    public static double getModerateCaveFactor() {
        checkInitialized();
        return config.getDouble("caves.moderate-cave-factor", 1.0);
    }

    public static double getSurfaceCaveFactor() {
        checkInitialized();
        return config.getDouble("caves.surface-cave-factor", 0.5);
    }

    public static double getCavernThreshold() {
        checkInitialized();
        return config.getDouble("caves.cavern-threshold", 0.7);
    }

    public static double getCavernDensity() {
        checkInitialized();
        return config.getDouble("caves.cavern-density", 0.8);
    }

    public static double getSmallCaveStrength() {
        checkInitialized();
        return config.getDouble("caves.small-cave-strength", 0.3);
    }
    // =================================
    // ORE GENERATION
    // =================================

    public static double getDiamondOreThreshold() {
        checkInitialized();
        return config.getDouble("ores.diamond-threshold", 0.85);
    }

    public static double getGoldOreThreshold() {
        checkInitialized();
        return config.getDouble("ores.gold-threshold", 0.8);
    }

    public static double getIronOreThreshold() {
        checkInitialized();
        return config.getDouble("ores.iron-threshold", 0.75);
    }

    public static double getCopperOreThreshold() {
        checkInitialized();
        return config.getDouble("ores.copper-threshold", 0.7);
    }

    public static double getCoalOreThreshold() {
        checkInitialized();
        return config.getDouble("ores.coal-threshold", 0.6);
    }

    public static double getRedstoneOreThreshold() {
        checkInitialized();
        return config.getDouble("ores.redstone-threshold", 0.5);
    }

    public static double getEmeraldOreThreshold() {
        checkInitialized();
        return config.getDouble("ores.emerald-threshold", 0.87);
    }

    public static double getLapisOreThreshold() {
        checkInitialized();
        return config.getDouble("ores.lapis-threshold", 0.78);
    }

    // Sky Island Ores
    public static double getSkyNetheriteThreshold() {
        checkInitialized();
        return config.getDouble("sky-island-ores.netherite-threshold", 0.9);
    }

    public static double getSkyAncientDebrisThreshold() {
        checkInitialized();
        return config.getDouble("sky-island-ores.ancient-debris-threshold", 0.85);
    }

    public static double getSkyDiamondThreshold() {
        checkInitialized();
        return config.getDouble("sky-island-ores.sky-diamond-threshold", 0.8);
    }

    public static double getSkyEmeraldThreshold() {
        checkInitialized();
        return config.getDouble("sky-island-ores.sky-emerald-threshold", 0.75);
    }

    // =================================
    // SKY ISLANDS
    // =================================

    public static boolean isSkyIslandsEnabled() {
        checkInitialized();
        return config.getBoolean("sky-islands.enabled", true);
    }

    public static int getSkyIslandMinHeight() {
        checkInitialized();
        return config.getInt("sky-islands.min-height", 200);
    }

    public static int getSkyIslandMaxHeight() {
        checkInitialized();
        return config.getInt("sky-islands.max-height", 280);
    }

    public static double getSkyIslandRarityThreshold() {
        checkInitialized();
        return config.getDouble("sky-islands.rarity-threshold", 0.85);
    }

    public static double getSkyIslandScale() {
        checkInitialized();
        return config.getDouble("sky-islands.island-scale", 0.02);
    }

    public static double getSkyIslandDistanceFalloff() {
        checkInitialized();
        return config.getDouble("sky-islands.distance-falloff", 24.0);
    }

    // Sky island generation settings
    public static double getLocationScale() {
        checkInitialized();
        return config.getDouble("sky-islands.location-scale", 0.001);
    }

    public static double getCenterGridSize() {
        checkInitialized();
        return config.getDouble("sky-islands.center-grid-size", 32.0);
    }

    public static double getShapeYScale() {
        checkInitialized();
        return config.getDouble("sky-islands.shape-y-scale", 2.0);
    }

    public static double getVariationScale() {
        checkInitialized();
        return config.getDouble("sky-islands.variation-scale", 1.5);
    }

    public static double getVariationStrength() {
        checkInitialized();
        return config.getDouble("sky-islands.variation-strength", 0.3);
    }

    public static double getCenterAltitude() {
        checkInitialized();
        return config.getDouble("sky-islands.center-altitude", 240.0);
    }

    public static double getAltitudeFalloff() {
        checkInitialized();
        return config.getDouble("sky-islands.altitude-falloff", 40.0);
    }

    public static double getFarlandsBonus() {
        checkInitialized();
        return config.getDouble("sky-islands.farlands-bonus", 0.2);
    }
    // =================================
    // VEGETATION
    // =================================

    public static double getTreeGenerationChance() {
        checkInitialized();
        return config.getDouble("vegetation.tree-chance", 0.1);
    }

    public static double getGrassGenerationChance() {
        checkInitialized();
        return config.getDouble("vegetation.grass-chance", 0.3);
    }

    public static double getFlowerGenerationChance() {
        checkInitialized();
        return config.getDouble("vegetation.flower-chance", 0.15);
    }

    public static double getMushroomGenerationChance() {
        checkInitialized();
        return config.getDouble("vegetation.mushroom-chance", 0.05);
    }

    public static double getBerryBushGenerationChance() {
        checkInitialized();
        return config.getDouble("vegetation.berry-bush-chance", 0.08);
    }

    // Biome-specific vegetation multipliers
    public static double getForestTreeMultiplier() {
        checkInitialized();
        return config.getDouble("biome-vegetation.forest-tree-multiplier", 2.5);
    }

    public static double getJungleVegetationMultiplier() {
        checkInitialized();
        return config.getDouble("biome-vegetation.jungle-vegetation-multiplier", 3.0);
    }

    public static double getDesertVegetationMultiplier() {
        checkInitialized();
        return config.getDouble("biome-vegetation.desert-vegetation-multiplier", 0.1);
    }

    public static double getPlainsGrassMultiplier() {
        checkInitialized();
        return config.getDouble("biome-vegetation.plains-grass-multiplier", 1.5);
    }

    // Biome-specific vegetation chances
    public static double getPlainsTallGrassChance() {
        checkInitialized();
        return config.getDouble("vegetation.plains.tall-grass-chance", 0.15);
    }

    public static double getPlainsPoppyChance() {
        checkInitialized();
        return config.getDouble("vegetation.plains.poppy-chance", 0.2);
    }

    public static double getPlainsDandelionChance() {
        checkInitialized();
        return config.getDouble("vegetation.plains.dandelion-chance", 0.22);
    }

    public static double getSwampFernChance() {
        checkInitialized();
        return config.getDouble("vegetation.swamp.fern-chance", 0.15);
    }

    public static double getSwampBlueOrchidChance() {
        checkInitialized();
        return config.getDouble("vegetation.swamp.blue-orchid-chance", 0.18);
    }

    public static double getDesertDeadBushChance() {
        checkInitialized();
        return config.getDouble("vegetation.desert.dead-bush-chance", 0.05);
    }

    public static double getDesertCactusChance() {
        checkInitialized();
        return config.getDouble("vegetation.desert.cactus-chance", 0.07);
    }

    public static double getJungleMelonChance() {
        checkInitialized();
        return config.getDouble("vegetation.jungle.melon-chance", 0.1);
    }

    public static double getJungleBambooChance() {
        checkInitialized();
        return config.getDouble("vegetation.jungle.bamboo-chance", 0.15);
    }

    public static double getTaigaFernChance() {
        checkInitialized();
        return config.getDouble("vegetation.taiga.fern-chance", 0.1);
    }

    public static double getMushroomFieldsBrownChance() {
        checkInitialized();
        return config.getDouble("vegetation.mushroom-fields.brown-mushroom-chance", 0.1);
    }

    public static double getMushroomFieldsRedChance() {
        checkInitialized();
        return config.getDouble("vegetation.mushroom-fields.red-mushroom-chance", 0.08);
    }

    // Tree generation chances by biome
    public static double getTreeChanceByBiome(String biome) {
        checkInitialized();
        String biomeLower = biome.toLowerCase().replace(" ", "-");
        return config.getDouble("vegetation.tree-chances." + biomeLower,
                config.getDouble("vegetation.tree-chances.default", 0.1));
    }
    // =================================
    // AGRICULTURE
    // =================================

    public static boolean isAgricultureEnabled() {
        checkInitialized();
        return config.getBoolean("agriculture.enabled", true);
    }

    public static double getFarmGenerationChance() {
        checkInitialized();
        return config.getDouble("agriculture.farm-chance", 0.05);
    }

    public static boolean isCropVarietyEnabled() {
        checkInitialized();
        return config.getBoolean("agriculture.crop-variety", true);
    }

    public static boolean areIrrigationSystemsEnabled() {
        checkInitialized();
        return config.getBoolean("agriculture.irrigation-systems", true);
    }

    public static boolean isFarmInfrastructureEnabled() {
        checkInitialized();
        return config.getBoolean("agriculture.farm-infrastructure", true);
    }

    public static double getGreenhouseGenerationChance() {
        checkInitialized();
        return config.getDouble("agriculture.greenhouse-chance", 0.02);
    }

    // Agriculture random generation settings
    public static long getAgricultureRandomX() {
        checkInitialized();
        return config.getLong("agriculture.agriculture-random-x", 31L);
    }

    public static long getAgricultureRandomZ() {
        checkInitialized();
        return config.getLong("agriculture.agriculture-random-z", 17L);
    }

    public static int getAgricultureFrequency() {
        checkInitialized();
        return config.getInt("agriculture.agriculture-frequency", 20);
    }
    // =================================
    // MOB SPAWNING
    // =================================

    public static boolean isMobSpawningEnabled() {
        checkInitialized();
        return config.getBoolean("mob-spawning.enabled", true);
    }

    public static double getHabitatGenerationChance() {
        checkInitialized();
        return config.getDouble("mob-spawning.habitat-chance", 0.067);
    }

    public static double getWaterSourceChance() {
        checkInitialized();
        return config.getDouble("mob-spawning.water-source-chance", 0.1);
    }

    public static boolean isShelterGenerationEnabled() {
        checkInitialized();
        return config.getBoolean("mob-spawning.shelter-generation", true);
    }

    public static boolean areFeedingAreasEnabled() {
        checkInitialized();
        return config.getBoolean("mob-spawning.feeding-areas", true);
    }

    // Mob spawning random generation settings
    public static long getSpawningRandomX() {
        checkInitialized();
        return config.getLong("mob-spawning.spawning-random-x", 13L);
    }

    public static long getSpawningRandomZ() {
        checkInitialized();
        return config.getLong("mob-spawning.spawning-random-z", 29L);
    }

    public static int getSpawningFrequency() {
        checkInitialized();
        return config.getInt("mob-spawning.spawning-frequency", 15);
    }

    // Biome-specific mob spawning
    public static double getPlainsGrazingMultiplier() {
        checkInitialized();
        return config.getDouble("biome-spawning.plains-grazing-multiplier", 2.0);
    }

    public static double getForestClearingChance() {
        checkInitialized();
        return config.getDouble("biome-spawning.forest-clearing-chance", 0.12);
    }

    public static double getDesertOasisAnimalChance() {
        checkInitialized();
        return config.getDouble("biome-spawning.desert-oasis-animal-chance", 0.15);
    }

    public static double getTaigaDenChance() {
        checkInitialized();
        return config.getDouble("biome-spawning.taiga-den-chance", 0.08);
    }

    public static double getJunglePerchChance() {
        checkInitialized();
        return config.getDouble("biome-spawning.jungle-perch-chance", 0.2);
    }

    // =================================
    // STRUCTURES
    // =================================

    public static boolean areStructuresEnabled() {
        checkInitialized();
        return config.getBoolean("structures.enabled", true);
    }

    public static double getBasicStructureChance() {
        checkInitialized();
        return config.getDouble("structures.basic-structure-chance", 0.01);
    }

    public static double getLegendaryStructureChance() {
        checkInitialized();
        return config.getDouble("structures.legendary-structure-chance", 0.001);
    }

    public static boolean areBiomeSpecificStructuresEnabled() {
        checkInitialized();
        return config.getBoolean("structures.biome-specific-structures", true);
    }

    // =================================
    // NOISE SEEDS
    // =================================

    public static long getTerrainSeed() {
        checkInitialized();
        return config.getLong("seeds.terrain", 42L);
    }

    public static long getCaveSeed() {
        checkInitialized();
        return config.getLong("seeds.caves", 1337L);
    }

    public static long getBiomeSeed() {
        checkInitialized();
        return config.getLong("seeds.biomes", 2021L);
    }

    public static long getOreSeed() {
        checkInitialized();
        return config.getLong("seeds.ores", 404L);
    }

    public static long getSkyIslandSeed() {
        checkInitialized();
        return config.getLong("seeds.sky-islands", 9000L);
    }

    public static long getAgricultureSeed() {
        checkInitialized();
        return config.getLong("seeds.agriculture", 7777L);
    }

    public static long getSpawningSeed() {
        checkInitialized();
        return config.getLong("seeds.spawning", 5555L);
    }

    // Additional noise seed offsets
    public static long getSecondaryTerrainSeed() {
        checkInitialized();
        return getTerrainSeed() + config.getLong("seeds.secondary-terrain", 1000L);
    }

    public static long getDetailTerrainSeed() {
        checkInitialized();
        return getTerrainSeed() + config.getLong("seeds.detail-terrain", 2000L);
    }

    public static long getHillsSeed() {
        checkInitialized();
        return getTerrainSeed() + config.getLong("seeds.hills", 3000L);
    }

    public static long getValleysSeed() {
        checkInitialized();
        return getTerrainSeed() + config.getLong("seeds.valleys", 4000L);
    }

    public static long getElevationSeed() {
        checkInitialized();
        return getTerrainSeed() + config.getLong("seeds.elevation", 5000L);
    }

    public static long getLargeCavesSeed() {
        checkInitialized();
        return getCaveSeed() + config.getLong("seeds.large-caves", 2000L);
    }

    public static long getMediumCavesSeed() {
        checkInitialized();
        return getCaveSeed() + config.getLong("seeds.medium-caves", 3000L);
    }

    public static long getSmallCavesSeed() {
        checkInitialized();
        return getCaveSeed() + config.getLong("seeds.small-caves", 4000L);
    }

    public static long getCavernsSeed() {
        checkInitialized();
        return getCaveSeed() + config.getLong("seeds.caverns", 5000L);
    }

    public static long getFarlandsSpikesSeed() {
        checkInitialized();
        return getTerrainSeed() + config.getLong("seeds.farlands-spikes", 6000L);
    }

    public static long getFarlandsWallsSeed() {
        checkInitialized();
        return getTerrainSeed() + config.getLong("seeds.farlands-walls", 7000L);
    }

    public static long getFarlandsGlitchSeed() {
        checkInitialized();
        return getTerrainSeed() + config.getLong("seeds.farlands-glitch", 8000L);
    }

    public static long getSkyLocationSeed() {
        checkInitialized();
        return getSkyIslandSeed() + config.getLong("seeds.sky-location", 9000L);
    }

    public static long getSkyShapeSeed() {
        checkInitialized();
        return getSkyIslandSeed() + config.getLong("seeds.sky-shape", 10000L);
    }

    public static long getSkyVariationSeed() {
        checkInitialized();
        return getSkyIslandSeed() + config.getLong("seeds.sky-variation", 11000L);
    }
    // =================================
    // CHAOTIC TERRAIN SETTINGS
    // =================================

    // Main chaos settings
    public static double getChaosIntensity() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.chaos-intensity", 1.5);
    }

    public static double getTornadoSpiralFactor() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.tornado-spiral-factor", 0.1);
    }

    // Chaos noise seeds
    public static long getTornadoSeed() {
        checkInitialized();
        return getTerrainSeed() + config.getLong("chaotic-terrain.tornado-seed-offset", 41876L);
    }

    public static long getTunnelSeed() {
        checkInitialized();
        return getTerrainSeed() + config.getLong("chaotic-terrain.tunnel-seed-offset", 86420L);
    }

    public static long getDistortionSeed() {
        checkInitialized();
        return getTerrainSeed() + config.getLong("chaotic-terrain.distortion-seed-offset", 1357L);
    }

    public static long getOvergrowthSeed() {
        checkInitialized();
        return getTerrainSeed() + config.getLong("chaotic-terrain.overgrowth-seed-offset", 2468L);
    }

    // Tunnel system configuration
    public static double getTunnelThreshold() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.tunnels.threshold", 0.3);
    }

    public static double getTunnelScalePrimary() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.tunnels.scale-primary", 0.02);
    }

    public static double getTunnelScaleSecondary() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.tunnels.scale-secondary", 0.035);
    }

    public static double getTunnelScaleTertiary() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.tunnels.scale-tertiary", 0.008);
    }

    public static int getTunnelDeepFluidThreshold() {
        checkInitialized();
        return config.getInt("chaotic-terrain.tunnels.deep-fluid-threshold", 30);
    }

    public static double getTunnelLavaChance() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.tunnels.lava-chance", 0.6);
    }

    public static double getTunnelWaterChance() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.tunnels.water-chance", 0.3);
    }

    public static int getTunnelOvergrowthSurfaceY() {
        checkInitialized();
        return config.getInt("chaotic-terrain.tunnels.overgrowth-surface-y", 60);
    }

    // Chaos distortion effects
    public static double getDistortionCoordScale() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.distortion.coord-scale", 0.005);
    }

    public static double getDistortionSpiralScale() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.distortion.spiral-scale", 0.01);
    }

    public static double getDistortionYScale() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.distortion.y-scale", 0.01);
    }

    public static double getDistortionYOffsetMultiplier() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.distortion.y-offset-multiplier", 20.0);
    }

    // Overgrowth generation
    public static double getOvergrowthDensityThreshold() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.overgrowth.density-threshold", 0.4);
    }

    public static double getOvergrowthScale() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.overgrowth.scale", 0.03);
    }

    public static double getOvergrowthTypeScale() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.overgrowth.type-scale", 0.1);
    }

    public static double getOvergrowthYScale() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.overgrowth.y-scale", 0.02);
    }

    // Chaos ore bonuses
    public static double getChaosDeepOreBonus() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.chaos-ore-bonuses.deep-ore-bonus", 0.4);
    }

    public static double getChaosSurfaceOreBonus() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.chaos-ore-bonuses.surface-ore-bonus", 0.35);
    }

    public static double getChaosTransitionOreBonus() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.chaos-ore-bonuses.transition-ore-bonus", 0.3);
    }

    public static double getChaosSkyIslandOreBonus() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.chaos-ore-bonuses.sky-island-ore-bonus", 0.3);
    }

    // Material corruption and mixing
    public static double getSurfaceCorruptionThreshold() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.corruption.surface-corruption-threshold", 1.2);
    }

    public static double getMaterialMixingThreshold() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.corruption.material-mixing-threshold", 0.6);
    }

    public static double getBiomeMixingThreshold() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.corruption.biome-mixing-threshold", 0.8);
    }

    public static double getStoneVariantThreshold() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.corruption.stone-variant-threshold", 1.0);
    }

    public static double getCorruptionScale() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.corruption.corruption-scale", 0.1);
    }

    public static double getLayerScale() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.corruption.layer-scale", 0.15);
    }

    public static double getVariantScale() {
        checkInitialized();
        return config.getDouble("chaotic-terrain.corruption.variant-scale", 0.12);
    }

    // =================================
    // DEBUG SETTINGS
    // =================================

    public static boolean isVerboseLoggingEnabled() {
        checkInitialized();
        return config.getBoolean("debug.verbose-logging", false);
    }

    public static boolean areGenerationStatsEnabled() {
        checkInitialized();
        return config.getBoolean("debug.generation-stats", false);
    }

    public static boolean isPerformanceMonitoringEnabled() {
        checkInitialized();
        return config.getBoolean("debug.performance-monitoring", false);
    }

    // =================================
    // PLUGIN METADATA
    // =================================

    /**
     * Get the plugin version from config
     */
    public static String getVersion() {
        checkInitialized();
        return config.getString("version", "1.0.0");
    }
}
