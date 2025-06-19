package cc.farlanders.generate.structures;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class StructureGenerator {

    private static final double STRUCTURE_CHANCE = 0.01; // 1% common structure
    private static final double LEGENDARY_STRUCTURE_CHANCE = 0.001; // 0.1% rare structure

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

    public void generateStructures(ChunkData chunk, int cx, int cz, int worldX, int worldZ, int topY,
            BiomeStyle biome) {
        Random random = new Random(hashCoords(worldX, worldZ) ^ 0xC0FFEE);

        if (random.nextDouble() < LEGENDARY_STRUCTURE_CHANCE) {
            placeLegendaryStructure(chunk, cx, topY + 1, cz, random);
            return;
        }

        if (random.nextDouble() > STRUCTURE_CHANCE)
            return;

        switch (biome) {
            case PLAINS -> buildPlainsStructure(chunk, cx, topY + 1, cz, random);
            case DESERT -> buildDesertStructure(chunk, cx, topY + 1, cz, random);
            case TAIGA -> buildTaigaStructure(chunk, cx, topY + 1, cz, random);
            case SWAMP -> buildSwampStructure(chunk, cx, topY + 1, cz, random);
            case JUNGLE -> buildJungleStructure(chunk, cx, topY + 1, cz, random);
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

    private void buildTaigaStructure(ChunkData chunk, int x, int y, int z, Random random) {
        layOutFoundation(chunk, x, y, z, 3, Material.SPRUCE_PLANKS);
        chunk.setBlock(x + 1, y + 1, z + 1, Material.BARREL);
    }

    private void buildSwampStructure(ChunkData chunk, int x, int y, int z, Random random) {
        layOutFoundation(chunk, x - 1, y, z - 1, 3, Material.MANGROVE_PLANKS);
        chunk.setBlock(x, y + 1, z, Material.CAULDRON);
        if (random.nextBoolean())
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
        buildPillar(chunk, x, y, z, height, Material.CRYING_OBSIDIAN);
        chunk.setBlock(x, y + height, z, Material.ENCHANTING_TABLE);

        if (random.nextDouble() < 0.25) {
            chunk.setBlock(x + 1, y + height - 1, z, Material.ENDER_CHEST);
        }

        int radius = 3;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radius * radius && random.nextDouble() < 0.2) {
                    chunk.setBlock(x + dx, y - 1, z + dz, Material.SHROOMLIGHT);
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
