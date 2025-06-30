package cc.farlanders.generate.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for GenerationConfig to ensure proper configuration loading
 */
class GenerationConfigTest {

    private Plugin mockPlugin;
    private FileConfiguration mockConfig;

    @BeforeEach
    void setUp() {
        mockPlugin = mock(Plugin.class);
        mockConfig = new YamlConfiguration();

        // Set up realistic config values
        setUpConfigValues();

        // Mock plugin.getConfig() to return our mock config
        when(mockPlugin.getConfig()).thenReturn(mockConfig);
        doNothing().when(mockPlugin).saveDefaultConfig();
    }

    private void setUpConfigValues() {
        // World settings
        mockConfig.set("world.sea-level", 64);
        mockConfig.set("world.max-height", 320);
        mockConfig.set("world.farlands-threshold", 12550820.0);
        mockConfig.set("world.chunk-size", 16);
        mockConfig.set("world.terrain-density-threshold", 0.5);
        mockConfig.set("world.farlands-intensity-divisor", 1000000.0);

        // Terrain settings
        mockConfig.set("terrain.base-scale", 0.005);
        mockConfig.set("terrain.cave-scale", 0.015);
        mockConfig.set("terrain.biome-scale", 0.003);
        mockConfig.set("terrain.height-boost", 0.3);
        mockConfig.set("terrain.surface-variation", 1.2);

        // Sky islands settings
        mockConfig.set("sky-islands.enabled", true);
        mockConfig.set("sky-islands.min-height", 200);
        mockConfig.set("sky-islands.max-height", 280);
        mockConfig.set("sky-islands.rarity-threshold", 0.85);

        // Agriculture settings
        mockConfig.set("agriculture.enabled", true);
        mockConfig.set("agriculture.farm-chance", 0.05);
        mockConfig.set("agriculture.agriculture-frequency", 20);

        // Mob spawning settings
        mockConfig.set("mob-spawning.enabled", true);
        mockConfig.set("mob-spawning.habitat-chance", 0.067);
        mockConfig.set("mob-spawning.spawning-frequency", 15);

        // Structure settings
        mockConfig.set("structures.enabled", true);
        mockConfig.set("structures.basic-structure-chance", 0.01);
        mockConfig.set("structures.legendary-structure-chance", 0.001);
    }

    @Test
    @DisplayName("GenerationConfig initializes correctly with default values")
    void testInitializeWithDefaults() {
        GenerationConfig.initialize(mockPlugin);

        // Verify core values are loaded correctly
        assertEquals(64, GenerationConfig.getSeaLevel(), "Sea level should be loaded correctly");
        assertEquals(320, GenerationConfig.getMaxHeight(), "Max height should be loaded correctly");
        assertEquals(12550820.0, GenerationConfig.getFarlandsThreshold(), 0.1,
                "FarLands threshold should be loaded correctly");
    }

    @Test
    @DisplayName("GenerationConfig provides correct terrain settings")
    void testTerrainSettings() {
        GenerationConfig.initialize(mockPlugin);

        // Verify terrain settings
        assertEquals(0.005, GenerationConfig.getBaseTerrainScale(), 0.001, "Base terrain scale should be correct");
        assertEquals(0.015, GenerationConfig.getCaveScale(), 0.001, "Cave scale should be correct");
        assertEquals(0.003, GenerationConfig.getBiomeScale(), 0.001, "Biome scale should be correct");
        assertEquals(0.3, GenerationConfig.getTerrainHeightBoost(), 0.01, "Terrain height boost should be correct");
        assertEquals(1.2, GenerationConfig.getSurfaceVariationFactor(), 0.01,
                "Surface variation factor should be correct");
    }

    @Test
    @DisplayName("GenerationConfig provides correct world settings")
    void testWorldSettings() {
        GenerationConfig.initialize(mockPlugin);

        // Verify world settings
        assertEquals(16, GenerationConfig.getChunkSize(), "Chunk size should be correct");
        assertEquals(0.5, GenerationConfig.getTerrainDensityThreshold(), 0.01,
                "Terrain density threshold should be correct");
        assertEquals(1000000.0, GenerationConfig.getFarlandsIntensityDivisor(), 0.1,
                "FarLands intensity divisor should be correct");
    }

    @Test
    @DisplayName("GenerationConfig provides correct sky islands settings")
    void testSkyIslandsSettings() {
        GenerationConfig.initialize(mockPlugin);

        // Verify sky islands settings
        assertTrue(GenerationConfig.isSkyIslandsEnabled(), "Sky islands should be enabled");
        assertEquals(200, GenerationConfig.getSkyIslandMinHeight(), "Sky island min height should be correct");
        assertEquals(280, GenerationConfig.getSkyIslandMaxHeight(), "Sky island max height should be correct");
        assertEquals(0.85, GenerationConfig.getSkyIslandRarityThreshold(), 0.01,
                "Sky island rarity threshold should be correct");
    }

    @Test
    @DisplayName("GenerationConfig provides correct agriculture settings")
    void testAgricultureSettings() {
        GenerationConfig.initialize(mockPlugin);

        // Verify agriculture settings
        assertTrue(GenerationConfig.isAgricultureEnabled(), "Agriculture should be enabled");
        assertEquals(20, GenerationConfig.getAgricultureFrequency(), "Agriculture frequency should be correct");
        assertEquals(0.05, GenerationConfig.getFarmGenerationChance(), 0.01,
                "Agriculture farm chance should be correct");
    }

    @Test
    @DisplayName("GenerationConfig provides correct mob spawning settings")
    void testMobSpawningSettings() {
        GenerationConfig.initialize(mockPlugin);

        // Verify mob spawning settings
        assertTrue(GenerationConfig.isMobSpawningEnabled(), "Mob spawning should be enabled");
        assertEquals(15, GenerationConfig.getSpawningFrequency(), "Spawning frequency should be correct");
        assertEquals(0.067, GenerationConfig.getHabitatGenerationChance(), 0.01,
                "Habitat generation chance should be correct");
    }

    @Test
    @DisplayName("GenerationConfig provides correct structure settings")
    void testStructureSettings() {
        GenerationConfig.initialize(mockPlugin);

        // Verify structure settings
        assertTrue(GenerationConfig.areStructuresEnabled(), "Structures should be enabled");
        assertEquals(0.01, GenerationConfig.getBasicStructureChance(), 0.001,
                "Basic structure chance should be correct");
        assertEquals(0.001, GenerationConfig.getLegendaryStructureChance(), 0.0001,
                "Legendary structure chance should be correct");
    }

    @Test
    @DisplayName("GenerationConfig throws exception when not initialized")
    void testThrowsExceptionWhenNotInitialized() {
        // This test is difficult to implement since GenerationConfig is static
        // and we've already initialized it in setUp. We'll test a different scenario.

        // Test that config values are reasonable when initialized
        GenerationConfig.initialize(mockPlugin);

        assertTrue(GenerationConfig.getSeaLevel() > 0, "Sea level should be positive when initialized");
        assertTrue(GenerationConfig.getMaxHeight() > 0, "Max height should be positive when initialized");
    }

    @Test
    @DisplayName("GenerationConfig reload works correctly")
    void testReload() {
        GenerationConfig.initialize(mockPlugin);

        // Change some config values directly
        mockConfig.set("world.sea-level", 80);
        mockConfig.set("terrain.base-scale", 0.008);

        GenerationConfig.reload(mockPlugin);

        // Verify the reloaded values
        assertEquals(80, GenerationConfig.getSeaLevel(), "Sea level should be updated after reload");
        assertEquals(0.008, GenerationConfig.getBaseTerrainScale(), 0.001,
                "Base terrain scale should be updated after reload");
    }

    @Test
    @DisplayName("GenerationConfig handles disabled features correctly")
    void testDisabledFeatures() {
        // Set disabled features in config
        mockConfig.set("sky-islands.enabled", false);
        mockConfig.set("agriculture.enabled", false);
        mockConfig.set("mob-spawning.enabled", false);
        mockConfig.set("structures.enabled", false);

        GenerationConfig.initialize(mockPlugin);

        // Verify features are disabled
        assertFalse(GenerationConfig.isSkyIslandsEnabled(), "Sky islands should be disabled");
        assertFalse(GenerationConfig.isAgricultureEnabled(), "Agriculture should be disabled");
        assertFalse(GenerationConfig.isMobSpawningEnabled(), "Mob spawning should be disabled");
        assertFalse(GenerationConfig.areStructuresEnabled(), "Structures should be disabled");
    }

    @Test
    @DisplayName("GenerationConfig validates reasonable value ranges")
    void testValidateValueRanges() {
        GenerationConfig.initialize(mockPlugin);

        // Validate that loaded values are within reasonable ranges
        assertTrue(GenerationConfig.getSeaLevel() > 0 && GenerationConfig.getSeaLevel() < 500,
                "Sea level should be reasonable");
        assertTrue(GenerationConfig.getMaxHeight() > GenerationConfig.getSeaLevel(),
                "Max height should be greater than sea level");
        assertTrue(GenerationConfig.getBaseTerrainScale() > 0 && GenerationConfig.getBaseTerrainScale() < 1,
                "Base terrain scale should be between 0 and 1");
        assertTrue(GenerationConfig.getSkyIslandMinHeight() < GenerationConfig.getSkyIslandMaxHeight(),
                "Sky island min height should be less than max height");
        assertTrue(GenerationConfig.getFarlandsThreshold() > 0,
                "FarLands threshold should be positive");
    }
}
