package cc.farlanders.generate.vegetation;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class FloraGenerator {

    private static final String BIOME_CHERRY_GROVE = "cherry grove";
    private static final String BIOME_SWAMP = "swamp";
    private static final String BIOME_DESERT = "desert";
    private static final String BIOME_JUNGLE = "jungle";
    private static final String BIOME_TAIGA = "taiga";

    public void generatePlantsAndGrass(ChunkData chunk, int cx, int cz, int worldX, int worldZ, int topY,
            String biome) {
        Random random = new Random(hashCoords(worldX, worldZ) ^ 0xDEADBEEF);

        Material topBlock = chunk.getBlockData(cx, topY, cz).getMaterial();
        if (!isSuitableForFlora(topBlock))
            return;

        double chance = random.nextDouble();
        String biomeLower = biome.toLowerCase();

        switch (biomeLower) {
            case "plains", BIOME_CHERRY_GROVE -> handlePlainsAndCherryGrove(chunk, cx, cz, topY, chance);
            case BIOME_SWAMP -> handleSwamp(chunk, cx, cz, topY, chance);
            case BIOME_DESERT, "badlands" -> handleDesertAndBadlands(chunk, cx, cz, topY, chance);
            case BIOME_JUNGLE -> handleJungle(chunk, cx, cz, topY, chance);
            case BIOME_TAIGA -> handleTaiga(chunk, cx, cz, topY, chance);
            case "mushroom fields" -> handleMushroomFields(chunk, cx, cz, topY, chance);
            default -> handlePlainsAndCherryGrove(chunk, cx, cz, topY, chance);
        }
    }

    private void handlePlainsAndCherryGrove(ChunkData chunk, int cx, int cz, int topY, double chance) {
        if (chance < 0.15)
            chunk.setBlock(cx, topY + 1, cz, Material.TALL_GRASS);
        else if (chance < 0.2)
            chunk.setBlock(cx, topY + 1, cz, Material.POPPY);
        else if (chance < 0.22)
            chunk.setBlock(cx, topY + 1, cz, Material.DANDELION);
    }

    private void handleSwamp(ChunkData chunk, int cx, int cz, int topY, double chance) {
        if (chance < 0.15)
            chunk.setBlock(cx, topY + 1, cz, Material.FERN);
        else if (chance < 0.18)
            chunk.setBlock(cx, topY + 1, cz, Material.BLUE_ORCHID);
    }

    private void handleDesertAndBadlands(ChunkData chunk, int cx, int cz, int topY, double chance) {
        if (chance < 0.05)
            chunk.setBlock(cx, topY + 1, cz, Material.DEAD_BUSH);
        else if (chance < 0.07)
            chunk.setBlock(cx, topY + 1, cz, Material.CACTUS);
    }

    private void handleJungle(ChunkData chunk, int cx, int cz, int topY, double chance) {
        if (chance < 0.1)
            chunk.setBlock(cx, topY + 1, cz, Material.MELON);
        else if (chance < 0.15)
            chunk.setBlock(cx, topY + 1, cz, Material.BAMBOO);
    }

    private void handleTaiga(ChunkData chunk, int cx, int cz, int topY, double chance) {
        if (chance < 0.1)
            chunk.setBlock(cx, topY + 1, cz, Material.FERN);
    }

    private void handleMushroomFields(ChunkData chunk, int cx, int cz, int topY, double chance) {
        if (chance < 0.1)
            chunk.setBlock(cx, topY + 1, cz, Material.BROWN_MUSHROOM);
        else if (chance < 0.2)
            chunk.setBlock(cx, topY + 1, cz, Material.RED_MUSHROOM);
    }

    public void generateBiomeTree(ChunkData chunk, int cx, int cz, int worldX, int worldZ, int topY, String biome) {
        Random random = new Random(hashCoords(worldX, worldZ));
        Material topBlock = chunk.getBlockData(cx, topY, cz).getMaterial();
        if (!isSuitableForFlora(topBlock))
            return;
        if (random.nextDouble() > getFloraChance(biome))
            return;

        TreeType treeType = getTreeTypeForBiome(biome);
        int height = 12 + random.nextInt(8);
        placeRoots(chunk, cx, topY, cz, biome, random);
        placeTree(chunk, cx, topY + 1, cz, treeType, height);
        placeHangingFeatures(chunk, cx, topY + height, cz, biome, random);
        maybeAddGlowCore(chunk, cx, topY + height / 2, cz, random);
    }

    private void placeRoots(ChunkData chunk, int cx, int y, int cz, String biome, Random random) {
        int rootDepth = 1 + random.nextInt(2);
        Material rootMat = switch (biome.toLowerCase()) {
            case "swamp", "mangrove swamp" -> Material.MANGROVE_ROOTS;
            case "taiga" -> Material.SPRUCE_LOG;
            case "jungle" -> Material.JUNGLE_LOG;
            default -> Material.DIRT;
        };
        for (int dy = 1; dy <= rootDepth; dy++) {
            chunk.setBlock(cx, y - dy, cz, rootMat);
        }
    }

    private void placeHangingFeatures(ChunkData chunk, int cx, int y, int cz, String biome, Random random) {
        if (!biome.toLowerCase().matches(".*(jungle|swamp).*"))
            return;
        for (int i = 0; i < 4; i++) {
            int dx = random.nextInt(3) - 1;
            int dz = random.nextInt(3) - 1;
            int length = 1 + random.nextInt(3);
            for (int j = 0; j < length; j++) {
                chunk.setBlock(cx + dx, y - j, cz + dz, Material.VINE);
            }
        }
    }

    private void maybeAddGlowCore(ChunkData chunk, int x, int y, int z, Random random) {
        double chance = random.nextDouble();
        if (chance < 0.01) {
            chunk.setBlock(x, y, z, Material.SHROOMLIGHT);
        } else if (chance < 0.005) {
            chunk.setBlock(x, y, z, Material.GLOWSTONE);
        } else if (chance < 0.001) {
            chunk.setBlock(x, y, z, Material.ENCHANTING_TABLE);
        }
    }

    private boolean isSuitableForFlora(Material mat) {
        return mat == Material.GRASS_BLOCK || mat == Material.DIRT || mat == Material.MYCELIUM || mat == Material.PODZOL
                || mat == Material.SAND;
    }

    private double getFloraChance(String biome) {
        return switch (biome.toLowerCase()) {
            case "plains", "forest", "dark forest", BIOME_JUNGLE, "savanna", "mangrove swamp" -> 0.2;
            case BIOME_SWAMP, BIOME_CHERRY_GROVE, BIOME_TAIGA -> 0.15;
            case "snowy plains", "ice spikes", BIOME_DESERT, "badlands" -> 0.05;
            case "mushroom fields" -> 0.01;
            default -> 0.1;
        };
    }

    private TreeType getTreeTypeForBiome(String biome) {
        return switch (biome.toLowerCase()) {
            case "forest" -> TreeType.TREE;
            case "dark forest" -> TreeType.DARK_OAK;
            case BIOME_JUNGLE -> TreeType.JUNGLE;
            case "savanna" -> TreeType.ACACIA;
            case BIOME_TAIGA -> TreeType.REDWOOD;
            case BIOME_CHERRY_GROVE -> TreeType.CHERRY;
            case "mangrove swamp" -> TreeType.MANGROVE;
            case BIOME_SWAMP -> TreeType.SWAMP;
            default -> TreeType.TREE;
        };
    }

    // ...existing code...

    void placeTree(ChunkData chunk, int cx, int y, int cz, TreeType treeType, int height) {
        switch (treeType) {
            case TREE, BIG_TREE -> placeOakTree(chunk, cx, y, cz, height);
            case BIRCH -> placeBirchTree(chunk, cx, y, cz, height);
            case REDWOOD -> placeSpruceTree(chunk, cx, y, cz, height);
            case ACACIA -> placeAcaciaTree(chunk, cx, y, cz, height);
            case CHERRY -> placeCherryTree(chunk, cx, y, cz, height);
            case JUNGLE -> placeJungleTree(chunk, cx, y, cz, height);
            case SWAMP -> placeSwampTree(chunk, cx, y, cz, height);
            case BROWN_MUSHROOM -> placeBrownMushroom(chunk, cx, y, cz, height);
            case RED_MUSHROOM -> placeRedMushroom(chunk, cx, y, cz, height);
            default -> placeOakTree(chunk, cx, y, cz, height);
        }
    }

    private void placeOakTree(ChunkData chunk, int cx, int y, int cz, int height) {
        for (int i = 0; i < height; i++) {
            chunk.setBlock(cx, y + i, cz, Material.OAK_LOG);
        }
        int leafRadius = Math.max(2, height / 4);
        int topY = y + height - 1;
        placeLeavesSphere(chunk, cx, topY, cz, leafRadius, Material.OAK_LEAVES);
    }

    private void placeLeavesSphere(ChunkData chunk, int centerX, int centerY, int centerZ, int radius,
            Material leafType) {
        Position center = new Position(centerX, centerY, centerZ);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    Offset offset = new Offset(dx, dy, dz);
                    placeLeafBlockIfValid(chunk, center, offset, radius, leafType);
                }
            }
        }
    }

    private void placeLeafBlockIfValid(ChunkData chunk, Position center, Offset offset, int radius, Material leafType) {
        int distSq = offset.dx * offset.dx + offset.dz * offset.dz + offset.dy * offset.dy;
        if (distSq <= radius * radius) {
            int lx = center.x + offset.dx;
            int ly = center.y + offset.dy;
            int lz = center.z + offset.dz;
            if (!(offset.dx == 0 && offset.dz == 0 && offset.dy >= 0)) {
                chunk.setBlock(lx, ly, lz, leafType);
            }
        }
    }

    private void placeBirchTree(ChunkData chunk, int cx, int y, int cz, int height) {
        for (int i = 0; i < height; i++) {
            chunk.setBlock(cx, y + i, cz, Material.BIRCH_LOG);
        }
        int leafRadius = Math.max(2, height / 5);
        int topY = y + height - 1;
        placeBirchLeaves(chunk, cx, topY, cz, leafRadius);
    }

    private void placeBirchLeaves(ChunkData chunk, int centerX, int centerY, int centerZ, int radius) {
        Position center = new Position(centerX, centerY, centerZ);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -1; dy <= 1; dy++) {
                    placeBirchLeafBlock(chunk, center, dx, dy, dz, radius);
                }
            }
        }
    }

    private static class Position {
        int x, y, z;

        public Position(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private void placeBirchLeafBlock(ChunkData chunk, Position center, int dx, int dy, int dz, int radius) {
        int distSq = dx * dx + dz * dz + dy * dy;
        if (distSq <= radius * radius) {
            int lx = center.x + dx;
            int ly = center.y + dy;
            int lz = center.z + dz;
            if (!(dx == 0 && dz == 0 && dy >= 0)) {
                chunk.setBlock(lx, ly, lz, Material.BIRCH_LEAVES);
            }
        }
    }

    private void placeSpruceTree(ChunkData chunk, int cx, int y, int cz, int height) {
        for (int i = 0; i < height; i++) {
            chunk.setBlock(cx, y + i, cz, Material.SPRUCE_LOG);
        }
        int radius = 3;
        for (int i = 0; i < radius * 2; i++) {
            int layerY = y + height - i;
            int layerRadius = radius - (i / 2); // Taper cone
            for (int dx = -layerRadius; dx <= layerRadius; dx++) {
                for (int dz = -layerRadius; dz <= layerRadius; dz++) {
                    if (dx * dx + dz * dz <= layerRadius * layerRadius) {
                        chunk.setBlock(cx + dx, layerY, cz + dz, Material.SPRUCE_LEAVES);
                    }
                }
            }
        }
    }

    private void placeAcaciaTree(ChunkData chunk, int cx, int y, int cz, int height) {
        for (int i = 0; i < height; i++) {
            chunk.setBlock(cx, y + i, cz, Material.ACACIA_LOG);
        }
        int leafRadius = 2;
        int topY = y + height - 1;
        for (int dx = -leafRadius; dx <= leafRadius; dx++) {
            for (int dz = -leafRadius; dz <= leafRadius; dz++) {
                int lx = cx + dx;
                int ly = topY;
                int lz = cz + dz;
                if (Math.abs(dx) + Math.abs(dz) <= leafRadius) {
                    chunk.setBlock(lx, ly, lz, Material.ACACIA_LEAVES);
                }
            }
        }
    }

    private void placeCherryTree(ChunkData chunk, int cx, int y, int cz, int height) {
        for (int i = 0; i < height; i++) {
            chunk.setBlock(cx, y + i, cz, Material.CHERRY_LOG);
        }

        int topY = y + height;
        int radius = 3 + (height / 6);
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if (dx * dx + dz * dz + dy * dy <= radius * radius) {
                        chunk.setBlock(cx + dx, topY + dy, cz + dz, Material.CHERRY_LEAVES);
                    }
                }
            }
        }
    }

    private static class Offset {
        int dx, dy, dz;

        public Offset(int dx, int dy, int dz) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }
    }

    private void placeJungleTree(ChunkData chunk, int cx, int y, int cz, int height) {
        for (int i = 0; i < height; i++) {
            chunk.setBlock(cx, y + i, cz, Material.JUNGLE_LOG);
            chunk.setBlock(cx, y + height - 1, cz, Material.VINE); // vines
        }

        int topY = y + height;
        int radius = 4;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -1; dy <= 2; dy++) {
                    if (dx * dx + dz * dz + dy * dy <= radius * radius) {
                        chunk.setBlock(cx + dx, topY + dy, cz + dz, Material.JUNGLE_LEAVES);
                    }
                }
            }
        }
    }

    private void placeSwampTree(ChunkData chunk, int cx, int y, int cz, int height) {
        for (int i = 0; i < height; i++) {
            chunk.setBlock(cx, y + i, cz, Material.OAK_LOG);
        }
        int leafRadius = 2;
        int topY = y + height - 1;
        for (int dx = -leafRadius; dx <= leafRadius; dx++) {
            for (int dz = -leafRadius; dz <= leafRadius; dz++) {
                int lx = cx + dx;
                int ly = topY;
                int lz = cz + dz;
                if (Math.abs(dx) + Math.abs(dz) <= leafRadius) {
                    chunk.setBlock(lx, ly, lz, Material.OAK_LEAVES);
                }
            }
        }
    }

    private void placeBrownMushroom(ChunkData chunk, int cx, int y, int cz, int height) {
        for (int i = 0; i < height; i++) {
            chunk.setBlock(cx, y + i, cz, Material.MUSHROOM_STEM);
        }
        int capRadius = 2;
        int capY = y + height - 1;
        for (int dx = -capRadius; dx <= capRadius; dx++) {
            for (int dz = -capRadius; dz <= capRadius; dz++) {
                int lx = cx + dx;
                int lz = cz + dz;
                if (Math.abs(dx) + Math.abs(dz) <= capRadius) {
                    chunk.setBlock(lx, capY, lz, Material.BROWN_MUSHROOM_BLOCK);
                }
            }
        }
    }

    private void placeRedMushroom(ChunkData chunk, int cx, int y, int cz, int height) {
        for (int i = 0; i < height; i++) {
            chunk.setBlock(cx, y + i, cz, Material.MUSHROOM_STEM);
        }
        int capRadius = 2;
        int capY = y + height - 1;
        for (int dx = -capRadius; dx <= capRadius; dx++) {
            for (int dz = -capRadius; dz <= capRadius; dz++) {
                int lx = cx + dx;
                int lz = cz + dz;
                if (Math.abs(dx) + Math.abs(dz) <= capRadius) {
                    chunk.setBlock(lx, capY, lz, Material.RED_MUSHROOM_BLOCK);
                }
            }
        }
    }

    private long hashCoords(int x, int z) {
        return x * 341873128712L + z * 132897987541L;
    }
}
