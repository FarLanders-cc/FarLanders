package cc.farlanders.generate.biomes;

import java.util.List;

import org.bukkit.Registry;
import org.bukkit.block.Biome;

import cc.farlanders.noise.OpenSimplex2;

public class BiomeProvider {

    private static final String ICE_SPIKES = "ICE_SPIKES";
    private static final List<String> FARLANDS_BIOMES = List.of(
            "CRIMSON_FOREST", "WARPED_FOREST", "SOUL_SAND_VALLEY", "BASALT_DELTAS",
            "MUSHROOM_FIELDS", "DEEP_DARK", ICE_SPIKES);

    private final double seaLevel;
    private final double farlandsThreshold;
    private final double maxHeight;

    public BiomeProvider(double seaLevel, double farlandsThreshold, double maxHeight) {
        this.seaLevel = seaLevel;
        this.farlandsThreshold = farlandsThreshold;
        this.maxHeight = maxHeight;
    }

    public String getBiomeAt(int x, int z) {
        double distanceFromOrigin = Math.sqrt((double) x * x + (double) z * z);

        // FarLands regions get chaotic biomes
        if (distanceFromOrigin > farlandsThreshold) {
            return getFarLandsBiome(x, z);
        }

        // Use multi-octave noise for realistic biome distribution
        double temperatureNoise = OpenSimplex2.noise2(x * 0.003, z * 0.003, 0.0);
        double humidityNoise = OpenSimplex2.noise2(x * 0.003, z * 0.003, 1000.0);
        double weirdnessNoise = OpenSimplex2.noise2(x * 0.002, z * 0.002, 2000.0);

        return selectBiomeByClimate(temperatureNoise, humidityNoise, weirdnessNoise, x, z);
    }

    private String getFarLandsBiome(int x, int z) {
        // Create chaotic biome patterns in FarLands
        int selector = Math.abs((x * 31 + z * 17) % FARLANDS_BIOMES.size());
        return FARLANDS_BIOMES.get(selector);
    }

    private String selectBiomeByClimate(double temperature, double humidity, double weirdness, int x, int z) {
        // Simplified biome selection to reduce complexity
        double chaos = Math.abs(weirdness) * 0.3;

        if (chaos > 0.4) {
            return getWeirdBiome(temperature, humidity);
        }

        if (temperature < -0.2) {
            return getColdBiome(humidity);
        }

        if (temperature > 0.4) {
            return getHotBiome(humidity);
        }

        return getTemperateBiome(humidity, x, z);
    }

    private String getWeirdBiome(double temperature, double humidity) {
        if (temperature < -0.3)
            return ICE_SPIKES;
        if (humidity > 0.5)
            return "MUSHROOM_FIELDS";
        return "DARK_FOREST";
    }

    private String getColdBiome(double humidity) {
        return humidity > 0.3 ? "SNOWY_TAIGA" : "SNOWY_PLAINS";
    }

    private String getHotBiome(double humidity) {
        if (humidity < -0.2)
            return "DESERT";
        if (humidity > 0.3)
            return "JUNGLE";
        return "SAVANNA";
    }

    private String getTemperateBiome(double humidity, int x, int z) {
        if (humidity > 0.3) {
            if (Math.abs((x + z) % 7) < 2)
                return "BIRCH_FOREST";
            return "FOREST";
        }

        if (humidity < -0.2) {
            return "BADLANDS";
        }

        return Math.abs((x * z) % 3) == 0 ? "SUNFLOWER_PLAINS" : "PLAINS";
    }

    public Biome getBiome(int x, int y, int z) {
        String biomeName = getBiomeAt(x, y);
        Biome biome = getBiomeFromRegistry(biomeName);
        if (biome != null) {
            return biome;
        }

        final int threshold = (int) farlandsThreshold;
        int absX = Math.abs(x);
        int absZ = Math.abs(z);

        if (y <= seaLevel - 5) {
            return getDeepOceanOrCaveBiome(absX, absZ, threshold);
        } else if (y <= seaLevel) {
            return getOceanOrSwampBiome(absX, absZ);
        } else if (absX > threshold || absZ > threshold) {
            return getFarLandsBiome(x, y, z);
        } else if (y > maxHeight - 16) {
            return getHighElevationBiome(x, z);
        } else if ((absX > threshold / 2 || absZ > threshold / 2) && y > seaLevel + 32) {
            return getNearFarLandsHighBiome(x, z);
        } else {
            return getTemperateBiome(x, y, z);
        }
    }

    private Biome getBiomeFromRegistry(String biomeName) {
        if (biomeName != null) {
            try {
                return Registry.BIOME.get(org.bukkit.NamespacedKey.minecraft(biomeName.toLowerCase()));
            } catch (IllegalArgumentException ignored) {
                // Fallback to procedural logic below
            }
        }
        return null;
    }

    private Biome getDeepOceanOrCaveBiome(int absX, int absZ, int threshold) {
        return (absX > threshold || absZ > threshold) ? Biome.DEEP_FROZEN_OCEAN : Biome.DEEP_OCEAN;
    }

    private Biome getOceanOrSwampBiome(int absX, int absZ) {
        if ((absX + absZ) % 37 < 5)
            return Biome.SWAMP;
        return Biome.OCEAN;
    }

    private Biome getFarLandsBiome(int x, int y, int z) {
        int selector = Math.abs((x * 31 + z * 17 + y * 13) % 5);
        return switch (selector) {
            case 0 -> Biome.BADLANDS;
            case 1 -> Biome.ICE_SPIKES;
            case 2 -> Biome.MUSHROOM_FIELDS;
            case 3 -> Biome.SNOWY_TAIGA;
            default -> Biome.SAVANNA_PLATEAU;
        };
    }

    private Biome getHighElevationBiome(int x, int z) {
        return (x + z) % 2 == 0 ? Biome.JAGGED_PEAKS : Biome.STONY_PEAKS;
    }

    private Biome getNearFarLandsHighBiome(int x, int z) {
        return (x ^ z) % 2 == 0 ? Biome.TAIGA : Biome.CHERRY_GROVE;
    }

    private Biome getTemperateBiome(int x, int y, int z) {
        int selector = Math.abs((x * 13 + z * 7 + y * 3) % 4);
        return switch (selector) {
            case 0 -> Biome.PLAINS;
            case 1 -> Biome.FOREST;
            case 2 -> Biome.BIRCH_FOREST;
            default -> Biome.SUNFLOWER_PLAINS;
        };
    }
}
