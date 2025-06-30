package cc.farlanders.generate;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cc.farlanders.generate.config.GenerationConfig;

/**
 * Comprehensive tests for the main FarLandsGenerator
 */
class FarLandsGeneratorTest {

    private FarLandsGenerator generator;
    private Plugin mockPlugin;
    private FileConfiguration mockConfig;

    @BeforeEach
    void setUp() {
        mockPlugin = mock(Plugin.class);
        mockConfig = mock(FileConfiguration.class);

        // Setup mock configuration
        when(mockPlugin.getConfig()).thenReturn(mockConfig);
        setupMockConfig();

        // Initialize configuration
        GenerationConfig.initialize(mockPlugin);

        // Create generator
        generator = new FarLandsGenerator();
    }

    private void setupMockConfig() {
        // World settings
        when(mockConfig.getInt("world.sea-level", 64)).thenReturn(64);
        when(mockConfig.getInt("world.max-height", 320)).thenReturn(320);
        when(mockConfig.getDouble("world.farlands-threshold", 12550820.0)).thenReturn(12550820.0);
        when(mockConfig.getInt("world.chunk-size", 16)).thenReturn(16);

        // Terrain settings
        when(mockConfig.getDouble("terrain.base-scale", 0.005)).thenReturn(0.005);
        when(mockConfig.getDouble("terrain.cave-scale", 0.015)).thenReturn(0.015);
        when(mockConfig.getDouble("terrain.biome-scale", 0.003)).thenReturn(0.003);

        // Feature settings
        when(mockConfig.getBoolean("generation.sky-islands.enabled", true)).thenReturn(true);
        when(mockConfig.getBoolean("generation.agriculture.enabled", true)).thenReturn(true);
        when(mockConfig.getBoolean("generation.mob-spawning.enabled", true)).thenReturn(true);
        when(mockConfig.getBoolean("generation.structures.enabled", true)).thenReturn(true);
    }

    @Test
    @DisplayName("FarLandsGenerator creates instance without errors")
    void testGeneratorCreation() {
        // Generator should be created successfully
        assertNotNull(generator, "Generator should be created");
        assertTrue(generator instanceof FarLandsGenerator, "Should be instance of FarLandsGenerator");
    }

    @Test
    @DisplayName("FarLandsGenerator respects configuration values")
    void testConfigurationRespected() {
        // Test that configuration values are loaded correctly
        assertEquals(64, GenerationConfig.getSeaLevel(), "Sea level should match configuration");
        assertEquals(320, GenerationConfig.getMaxHeight(), "Max height should match configuration");
        assertEquals(12550820.0, GenerationConfig.getFarlandsThreshold(), 0.1,
                "FarLands threshold should match configuration");
        assertEquals(0.005, GenerationConfig.getBaseTerrainScale(), 0.001,
                "Base terrain scale should match configuration");
    }

    @Test
    @DisplayName("FarLandsGenerator has reasonable configuration defaults")
    void testReasonableDefaults() {
        // Validate that configuration values are reasonable
        assertTrue(GenerationConfig.getSeaLevel() > 0 && GenerationConfig.getSeaLevel() < 500,
                "Sea level should be reasonable");
        assertTrue(GenerationConfig.getMaxHeight() > GenerationConfig.getSeaLevel(),
                "Max height should be greater than sea level");
        assertTrue(GenerationConfig.getBaseTerrainScale() > 0 && GenerationConfig.getBaseTerrainScale() < 1,
                "Base terrain scale should be between 0 and 1");
        assertTrue(GenerationConfig.getFarlandsThreshold() > 0,
                "FarLands threshold should be positive");
    }

    @Test
    @DisplayName("FarLandsGenerator components are properly initialized")
    void testComponentInitialization() {
        // Test that the generator initializes without throwing exceptions
        assertDoesNotThrow(() -> {
            new FarLandsGenerator();
        }, "Generator creation should not throw exceptions");
    }

    @Test
    @DisplayName("FarLandsGenerator configuration can be reloaded")
    void testConfigurationReload() {
        // Change configuration values
        when(mockConfig.getInt("world.sea-level", 64)).thenReturn(80);
        when(mockConfig.getDouble("terrain.base-scale", 0.005)).thenReturn(0.008);

        // Reload configuration
        GenerationConfig.reload(mockPlugin);

        // Verify new values are loaded
        assertEquals(80, GenerationConfig.getSeaLevel(), "Sea level should be updated after reload");
        assertEquals(0.008, GenerationConfig.getBaseTerrainScale(), 0.001,
                "Base terrain scale should be updated after reload");
    }

    @Test
    @DisplayName("FarLandsGenerator handles various terrain scales")
    void testTerrainScaleVariety() {
        // Test different terrain scale configurations
        double[] scaleValues = { 0.001, 0.005, 0.01, 0.02, 0.05 };

        for (double scale : scaleValues) {
            when(mockConfig.getDouble("terrain.base-scale", 0.005)).thenReturn(scale);
            GenerationConfig.reload(mockPlugin);

            // Should be able to create generator with various scales
            assertDoesNotThrow(() -> {
                new FarLandsGenerator();
            }, "Should create generator with terrain scale: " + scale);

            assertEquals(scale, GenerationConfig.getBaseTerrainScale(), 0.0001,
                    "Terrain scale should be set correctly: " + scale);
        }
    }

    @Test
    @DisplayName("FarLandsGenerator handles various FarLands thresholds")
    void testFarLandsThresholdVariety() {
        // Test different FarLands threshold configurations
        double[] thresholds = { 1000000.0, 12550820.0, 25000000.0, 50000000.0 };

        for (double threshold : thresholds) {
            when(mockConfig.getDouble("world.farlands-threshold", 12550820.0)).thenReturn(threshold);
            GenerationConfig.reload(mockPlugin);

            // Should be able to create generator with various thresholds
            assertDoesNotThrow(() -> {
                new FarLandsGenerator();
            }, "Should create generator with FarLands threshold: " + threshold);

            assertEquals(threshold, GenerationConfig.getFarlandsThreshold(), 0.1,
                    "FarLands threshold should be set correctly: " + threshold);
        }
    }

    @Test
    @DisplayName("FarLandsGenerator maintains proper initialization order")
    void testInitializationOrder() {
        // Test that generator can be created after configuration is initialized
        assertTrue(GenerationConfig.getSeaLevel() > 0, "Configuration should be initialized");

        // Create multiple generators to test independence
        FarLandsGenerator generator1 = new FarLandsGenerator();
        FarLandsGenerator generator2 = new FarLandsGenerator();

        assertNotNull(generator1, "First generator should be created");
        assertNotNull(generator2, "Second generator should be created");
        assertNotSame(generator1, generator2, "Generators should be different instances");
    }

    @Test
    @DisplayName("FarLandsGenerator validates configuration constraints")
    void testConfigurationConstraints() {
        // Configuration should enforce reasonable constraints
        assertTrue(GenerationConfig.getMaxHeight() >= 100,
                "Max height should be at least 100");
        assertTrue(GenerationConfig.getSeaLevel() >= 1,
                "Sea level should be at least 1");
        assertTrue(GenerationConfig.getBaseTerrainScale() <= 1.0,
                "Base terrain scale should not exceed 1.0");
        assertTrue(GenerationConfig.getCaveScale() <= 1.0,
                "Cave scale should not exceed 1.0");
        assertTrue(GenerationConfig.getBiomeScale() <= 1.0,
                "Biome scale should not exceed 1.0");
    }
}
