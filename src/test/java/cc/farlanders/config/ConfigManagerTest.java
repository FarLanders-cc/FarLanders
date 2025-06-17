package cc.farlanders.config;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Captor;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

class ConfigManagerTest {

    @Mock
    JavaPlugin plugin;

    @Mock
    FileConfiguration config;

    @Captor
    ArgumentCaptor<String> stringCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock plugin methods
        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getDataFolder()).thenReturn(new File("build/tmp/testConfig"));
        when(plugin.getLogger()).thenReturn(java.util.logging.Logger.getAnonymousLogger());

        // By default, config contains nothing
        when(config.contains(anyString())).thenReturn(false);

        // Setup ConfigManager with mocked plugin
        ConfigManager.setup(plugin);
    }

    @AfterEach
    void tearDown() {
        // Clean up static plugin reference for isolation
        // (Reflection needed if you want to reset static fields between tests)
    }

    @Test
    void testSetupCreatesConfigFileIfNotExists() {
        // The logger should have received "Creating config file..."
        verify(plugin, atLeastOnce()).saveDefaultConfig();
    }

    @Test
    void testSetDefaultConfigValuesSetsDefaults() {
        ConfigManager.setDefaultConfigValues();

        verify(config).set(eq(ConfigManager.ConfigKey.VERSION.getKey()), eq("1.0.0"));
        verify(config).set(eq(ConfigManager.ConfigKey.SEA_LEVEL.getKey()), eq(64));
        verify(plugin, atLeastOnce()).saveConfig();
    }

    @Test
    void testGetConfigReturnsPluginConfig() {
        assertEquals(config, ConfigManager.getConfig());
    }

    @Test
    void testGetConfigValueReturnsValue() {
        when(config.get("test-key")).thenReturn("test-value");
        String value = ConfigManager.getConfigValue("test-key");
        assertEquals("test-value", value);
    }

    @Test
    void testConfigKeyGetValueReturnsCorrectType() {
        when(config.get("sea-level")).thenReturn(42);
        Integer seaLevel = ConfigManager.ConfigKey.SEA_LEVEL.getValue(Integer.class);
        assertEquals(42, seaLevel);
    }

    @Test
    void testSaveConfigHandlesException() {
        doThrow(new RuntimeException("fail")).when(plugin).saveConfig();
        ConfigManager.saveConfig();
        // Should log error, but not throw
    }
}