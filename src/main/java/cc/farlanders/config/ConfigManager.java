package cc.farlanders.config;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
    private static JavaPlugin plugin;
    // Removed static File file; now declared locally in setup()
    private static Map<String, Object> defaults;

    private static final String NOISE_VALUES_KEY = "noise_values";

    static void mapValuesIntoConfigObject() {

        for (Values key : Values.getAllValues()) {
            if (!defaults.containsKey(NOISE_VALUES_KEY)) {
                defaults.put(NOISE_VALUES_KEY, key);
            }
            if (!plugin.getConfig().contains(NOISE_VALUES_KEY)) {
                plugin.getConfig().addDefaults(defaults);
            }
        }

        saveConfig();
    }

    public enum ConfigKey {
        // Add your configuration keys here
        NOISE_VALUES(NOISE_VALUES_KEY),
        VERSION("version");

        private final String key;

        ConfigKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        @SuppressWarnings("unchecked")
        public <T> T getValue(Class<T> type) {
            Object value = ConfigManager.getConfig().get(key);

            if (type.isInstance(value)) {
                return (T) value;
            }

            return null;
        }
    }

    public static void setup(JavaPlugin pl) {
        plugin = pl;
        File file = new File(plugin.getDataFolder(), "config.yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!file.exists()) {
            plugin.getLogger().info("Creating config file...");
            plugin.saveDefaultConfig();
        } else {
            plugin.getLogger().info("Config file already exists, loading...");
        }

        mapValuesIntoConfigObject();
    }

    public static FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public static void saveConfig() {
        try {
            plugin.saveConfig();
            plugin.getLogger().info("Config file saved successfully.");
        } catch (Exception e) {
            plugin.getLogger().severe(String.format("Failed to save config file: %s", e.getMessage()));
            plugin.getLogger().severe(String.format("Stack trace: %s", Arrays.toString(e.getStackTrace())));
        }
    }

    public static void setDefaultConfigValues() {
        FileConfiguration config = getConfig();

        // Set default values for configuration keys
        if (!config.contains(ConfigKey.VERSION.getKey())) {
            config.set(ConfigKey.VERSION.getKey(), "1.0.0");
        }

        // Add more default values as needed
        // config.set("some.other.key", "defaultValue");

        saveConfig();
    }

    public static Values getNoiseValues() {
        return ConfigKey.NOISE_VALUES.getValue(Values.class);
    }
}
