package cc.farlanders.generate.structures;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator.ChunkData;

import cc.farlanders.generate.config.GenerationConfig;

public class StructureGenerator {

    /**
     * Safely gets a material if it exists in the current Minecraft version,
     * otherwise returns a fallback material
     */
    private static Material getMaterialOrFallback(String materialName, Material fallback) {
        try {
            return Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    public enum BiomeStyle {
        PLAINS, DESERT, JUNGLE, SWAMP, TAIGA;

        public static BiomeStyle fromBiome(String biome) {
            return switch (biome.toLowerCase()) {
                case "plains" -> PLAINS;
                case "desert" -> DESERT;
                case "jungle" -> JUNGLE;
                case "swamp" -> SWAMP;
                case "taiga" -> TAIGA;
                default -> PLAINS; // Default to plains if unknown
            };
        }
    }

    private final AncientRuinsGenerator ancientRuinsGenerator = new AncientRuinsGenerator();

    public void generateStructures(ChunkData chunk, int cx, int cz, int worldX, int worldZ, int topY,
            BiomeStyle biome, cc.farlanders.generate.biomes.api.BiomePreset preset) {
        int surfaceY = topY;
        int buildY = topY + 1;
        Random random = new Random(hashCoords(worldX, worldZ) ^ 0xC0FFEE);

        // Try to generate ancient ruins first (very rare)
        ancientRuinsGenerator.tryGenerateRuins(chunk, cx, cz, worldX, worldZ, surfaceY, biome.name().toLowerCase());

        // Legendary structures are very rare and short-circuit generation
        if (tryPlaceLegendary(chunk, cx, cz, buildY, random))
            return;

        // Basic chance to generate anything at all
        if (random.nextDouble() > GenerationConfig.getBasicStructureChance())
            return;

        // If the preset suggests preferred structure identifiers, try those first
        if (preset != null && !preset.preferredStructures().isEmpty()) {
            handlePresetStructures(chunk, cx, cz, buildY, surfaceY, random, preset);
            return;
        }

        // Fallback to biome-based generation
        handleBiomeStructures(chunk, cx, cz, buildY, biome, random);
    }

    private boolean tryPlaceLegendary(ChunkData chunk, int cx, int cz, int buildY, Random random) {
        if (random.nextDouble() < GenerationConfig.getLegendaryStructureChance()) {
            placeLegendaryStructure(chunk, cx, buildY, cz, random);
            return true;
        }
        return false;
    }

    private void handlePresetStructures(ChunkData chunk, int cx, int cz, int buildY, int surfaceY, Random random,
            cc.farlanders.generate.biomes.api.BiomePreset preset) {
        for (String pref : preset.preferredStructures()) {
            double rarity = preset.structureRarity().getOrDefault(pref, 1.0);
            double clamped = Math.clamp(rarity, 0.0, 1.0);
            if (random.nextDouble() > clamped)
                continue;

            String chosen = chooseStructureVariant(pref, preset, random);
            handleChosenStructure(chunk, cx, cz, buildY, chosen, random);

            if (!preset.resourceBonuses().isEmpty()) {
                handleResourceBonuses(chunk, cx, cz, surfaceY, random, preset);
            }
        }
    }

    private String chooseStructureVariant(String pref, cc.farlanders.generate.biomes.api.BiomePreset preset,
            Random random) {
        java.util.List<String> variants = preset.structureVariants().getOrDefault(pref, java.util.List.of());
        if (!variants.isEmpty()) {
            return variants.get(random.nextInt(variants.size()));
        }
        return pref;
    }

    private void handleChosenStructure(ChunkData chunk, int cx, int cz, int buildY, String chosen, Random random) {
        switch (chosen.toLowerCase()) {
            case "pillar", "basic_pillar" -> buildPlainsStructure(chunk, cx, buildY, cz, random);
            case "desert_tower", "sand_tower" -> buildDesertStructure(chunk, cx, buildY, cz, random);
            case "jungle_pole", "vine_pole" -> buildJungleStructure(chunk, cx, buildY, cz, random);
            case "obsidian_spire", "obsidian_monolith" ->
                placeLegendaryStructure(chunk, cx, buildY, cz, random);
            case "underwater_altar" -> buildSwampStructure(chunk, cx, buildY, cz);
            default -> {
                // Unknown preference falls back to biome style (handled outside)
            }
        }
    }

    private void handleResourceBonuses(ChunkData chunk, int cx, int cz, int surfaceY, Random random,
            cc.farlanders.generate.biomes.api.BiomePreset preset) {
        for (var entry : preset.resourceBonuses().entrySet()) {
            String res = entry.getKey();
            double mult = entry.getValue();
            if (mult > 1.0 && random.nextDouble() < (mult - 1.0) * 0.25) {
                int dx = -1 + random.nextInt(3);
                int dz = -1 + random.nextInt(3);
                int by = surfaceY;
                chunk.setBlock(cx + dx, by, cz + dz,
                        getMaterialOrFallback(res.toUpperCase(), Material.DIAMOND_ORE));
            }
        }
    }

    private void handleBiomeStructures(ChunkData chunk, int cx, int cz, int buildY, BiomeStyle biome, Random random) {
        switch (biome) {
            case PLAINS -> buildPlainsStructure(chunk, cx, buildY, cz, random);
            case DESERT -> buildDesertStructure(chunk, cx, buildY, cz, random);
            case TAIGA -> buildTaigaStructure(chunk, cx, buildY, cz);
            case SWAMP -> buildSwampStructure(chunk, cx, buildY, cz);
            case JUNGLE -> buildJungleStructure(chunk, cx, buildY, cz, random);
        }
    }

    private void buildPlainsStructure(ChunkData chunk, int x, int y, int z, Random random) {
        buildPillar(chunk, x, y, z, 2 + random.nextInt(2), Material.COBBLESTONE);
        chunk.setBlock(x, y + 2, z, Material.MOSSY_COBBLESTONE);
    }

    private void buildDesertStructure(ChunkData chunk, int x, int y, int z, Random random) {
        buildPillar(chunk, x, y, z, 5, Material.SANDSTONE);
        if (random.nextDouble() < 0.3) {
            chunk.setBlock(x, y + 5, z, Material.LAVA);
        }
    }

    private void buildTaigaStructure(ChunkData chunk, int x, int y, int z) {
        // Natural stone foundation instead of artificial planks
        layOutFoundation(chunk, x, y, z, 3, Material.COBBLESTONE);
        chunk.setBlock(x + 1, y + 1, z + 1, Material.BARREL);
    }

    private void buildSwampStructure(ChunkData chunk, int x, int y, int z) {
        // Natural mud/stone foundation instead of artificial planks
        layOutFoundation(chunk, x - 1, y, z - 1, 3, Material.MUD);
        chunk.setBlock(x, y + 1, z, Material.CAULDRON);
        chunk.setBlock(x, y + 2, z, Material.BROWN_MUSHROOM_BLOCK);
    }

    private void buildJungleStructure(ChunkData chunk, int x, int y, int z, Random random) {
        buildPillar(chunk, x, y, z, 3, Material.JUNGLE_LOG);
        chunk.setBlock(x, y + 3, z, Material.VINE);
        if (random.nextDouble() < 0.4)
            chunk.setBlock(x, y + 4, z, Material.GLOWSTONE);
    }

    private void placeLegendaryStructure(ChunkData chunk, int x, int y, int z, Random random) {
        int height = 4 + random.nextInt(4);
        buildPillar(chunk, x, y, z, height, Material.OBSIDIAN); // Use regular obsidian
        chunk.setBlock(x, y + height, z, Material.ENCHANTING_TABLE);

        if (random.nextDouble() < 0.25) {
            chunk.setBlock(x + 1, y + height - 1, z, Material.ENDER_CHEST);
        }

        int radius = 3;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radius * radius && random.nextDouble() < 0.2) {
                    chunk.setBlock(x + dx, y - 1, z + dz, getMaterialOrFallback("SHROOMLIGHT", Material.GLOWSTONE));
                }
            }
        }
    }

    private void buildPillar(ChunkData chunk, int x, int y, int z, int height, Material material) {
        for (int i = 0; i < height; i++) {
            chunk.setBlock(x, y + i, z, material);
        }
    }

    private void layOutFoundation(ChunkData chunk, int x, int y, int z, int size, Material material) {
        for (int dx = 0; dx < size; dx++) {
            for (int dz = 0; dz < size; dz++) {
                chunk.setBlock(x + dx, y, z + dz, material);
            }
        }
    }

    private long hashCoords(int x, int z) {
        return x * 341873128712L + z * 132897987541L;
    }
}
