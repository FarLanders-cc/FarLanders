package cc.farlanders.generate;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cc.farlanders.generate.config.GenerationConfig;

/**
 * Base test class that provides common setup for all tests
 */
public abstract class BaseTest {

    protected Plugin mockPlugin;
    protected FileConfiguration mockConfig;

    @BeforeEach
    protected void setupBase() {
        // Create mock plugin
        mockPlugin = mock(Plugin.class);

        // Load default config from resources
        try {
            InputStream configStream = getClass().getClassLoader().getResourceAsStream("config.yml");
            if (configStream != null) {
                mockConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(configStream));
            } else {
                mockConfig = new YamlConfiguration();
                // Set default values if config file doesn't exist
                setDefaultConfigValues();
            }
        } catch (Exception e) {
            mockConfig = new YamlConfiguration();
            setDefaultConfigValues();
        }

        // Mock plugin methods
        when(mockPlugin.getConfig()).thenReturn(mockConfig);
        doNothing().when(mockPlugin).saveDefaultConfig();

        // Initialize GenerationConfig with mock plugin
        GenerationConfig.initialize(mockPlugin);
    }

    private void setDefaultConfigValues() {
        // World settings
        mockConfig.set("world.sea-level", 64);
        mockConfig.set("world.max-height", 320);
        mockConfig.set("world.farlands-threshold", 12550820.0);

        // Terrain settings
        mockConfig.set("terrain.base-scale", 0.005);
        mockConfig.set("terrain.cave-scale", 0.015);
        mockConfig.set("terrain.biome-scale", 0.003);

        // Sky islands
        mockConfig.set("sky-islands.enabled", true);
        mockConfig.set("sky-islands.min-height", 200);
        mockConfig.set("sky-islands.max-height", 280);
        mockConfig.set("sky-islands.rarity-threshold", 0.85);

        // Agriculture
        mockConfig.set("agriculture.enabled", true);
        mockConfig.set("agriculture.farm-chance", 0.05);
        mockConfig.set("agriculture.agriculture-frequency", 20);

        // Mob spawning
        mockConfig.set("mob-spawning.enabled", true);
        mockConfig.set("mob-spawning.habitat-chance", 0.067);
        mockConfig.set("mob-spawning.spawning-frequency", 15);

        // Structures
        mockConfig.set("structures.enabled", true);
        mockConfig.set("structures.basic-structure-chance", 0.01);
        mockConfig.set("structures.legendary-structure-chance", 0.001);
    }
}
