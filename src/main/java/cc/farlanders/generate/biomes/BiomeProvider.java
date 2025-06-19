package cc.farlanders.generate.biomes;

import java.util.List;

import org.bukkit.Registry;
import org.bukkit.block.Biome;

import cc.farlanders.noise.OpenSimplex2;

public class BiomeProvider {

    private static final List<String> BIOMES = List.of(
            "PLAINS", "FOREST", "DARK_FOREST", "CHERRY_GROVE",
            "SWAMP", "MANGROVE_SWAMP", "JUNGLE", "TAIGA",
            "SNOWY_PLAINS", "ICE_SPIKES", "DESERT", "BADLANDS",
            "SAVANNA", "MUSHROOM_FIELDS", "MOUNTAINS", "OCEAN",
            "DEEP_DARK", "DRIPSTONE_CAVES", "LUSH_CAVES",
            "CRIMSON_FOREST", "NETHER_WASTES", "SOUL_SAND_VALLEY", "BASALT_DELTAS");

    private final double seaLevel;
    private final double farlandsThreshold;
    private final double maxHeight;

    public BiomeProvider(double seaLevel, double farlandsThreshold, double maxHeight) {
        this.seaLevel = seaLevel;
        this.farlandsThreshold = farlandsThreshold;
        this.maxHeight = maxHeight;
    }

    public String getBiomeAt(int x, int z) {
        double scale = 0.005;
        double value = OpenSimplex2.noise2(x * scale, z * scale, 0.0);
        int index = (int) ((value + 1) / 2 * BIOMES.size()); // map noise [-1, 1] -> [0, size)
        index = Math.clamp(index, 0, BIOMES.size() - 1);
        return BIOMES.get(index);
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
