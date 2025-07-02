package cc.farlanders.generate.structures;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.generator.ChunkGenerator.ChunkData;

import cc.farlanders.generate.config.GenerationConfig;

/**
 * Generates rare ancient ruins buried underground with villagers
 * These structures are extremely rare and contain valuable loot and NPCs
 */
public class AncientRuinsGenerator {

    private static final Material[] ANCIENT_MATERIALS = {
            Material.MOSSY_STONE_BRICKS,
            Material.CRACKED_STONE_BRICKS,
            Material.STONE_BRICKS,
            Material.MOSSY_COBBLESTONE,
            Material.COBBLESTONE
    };

    private static final Material[] RARE_BLOCKS = {
            Material.CHISELED_STONE_BRICKS,
            Material.EMERALD_BLOCK,
            Material.DIAMOND_BLOCK,
            Material.GOLD_BLOCK,
            Material.IRON_BLOCK
    };

    private static final Material[] LOOT_CONTAINERS = {
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.BARREL,
            Material.ENDER_CHEST
    };

    /**
     * Attempts to generate ancient ruins at the given location
     * 
     * @param chunk    The chunk to modify
     * @param cx       Chunk-relative X coordinate
     * @param cz       Chunk-relative Z coordinate
     * @param worldX   World X coordinate
     * @param worldZ   World Z coordinate
     * @param surfaceY Surface level Y coordinate
     * @param biome    Current biome
     */
    public void tryGenerateRuins(ChunkData chunk, int cx, int cz, int worldX, int worldZ, int surfaceY, String biome) {
        Random random = new Random(hashCoords(worldX, worldZ) ^ 0xA1C1E17L);

        // Very rare chance - about 1 in 2000 chunks
        if (random.nextDouble() > GenerationConfig.getAncientRuinsChance()) {
            return;
        }

        // Additional distance check - ruins are rarer near spawn
        double distanceFromOrigin = Math.sqrt((double) worldX * worldX + (double) worldZ * worldZ);
        if (distanceFromOrigin < GenerationConfig.getAncientRuinsMinDistance()) {
            return;
        }

        // Bury ruins underground (10-25 blocks below surface)
        int burialDepth = 10 + random.nextInt(16);
        int ruinsY = Math.max(5, surfaceY - burialDepth);

        generateRuinsComplex(chunk, cx, cz, ruinsY, random, biome);
    }

    /**
     * Generates the main ruins complex
     */
    private void generateRuinsComplex(ChunkData chunk, int centerX, int centerZ, int baseY, Random random,
            String biome) {
        RuinsType ruinsType = selectRuinsType(random, biome);

        switch (ruinsType) {
            case TEMPLE -> generateTempleRuins(chunk, centerX, centerZ, baseY, random);
            case LIBRARY -> generateLibraryRuins(chunk, centerX, centerZ, baseY, random);
            case MARKETPLACE -> generateMarketplaceRuins(chunk, centerX, centerZ, baseY, random);
            case FORTRESS -> generateFortressRuins(chunk, centerX, centerZ, baseY, random);
        }
    }

    /**
     * Generates temple-style ruins with ceremonial areas
     */
    private void generateTempleRuins(ChunkData chunk, int centerX, int centerZ, int baseY, Random random) {
        // Central chamber (7x7)
        generateChamber(chunk, centerX - 3, centerZ - 3, baseY, 7, 7, 4, random);

        // Central altar
        placeAltar(chunk, centerX, centerZ, baseY + 1, random);

        // Four smaller chambers around the central one
        generateChamber(chunk, centerX - 7, centerZ - 3, baseY, 3, 3, 3, random);
        generateChamber(chunk, centerX + 5, centerZ - 3, baseY, 3, 3, 3, random);
        generateChamber(chunk, centerX - 3, centerZ - 7, baseY, 3, 3, 3, random);
        generateChamber(chunk, centerX - 3, centerZ + 5, baseY, 3, 3, 3, random);

        // Connect chambers with corridors
        generateCorridor(chunk, centerX - 4, centerZ, baseY + 1, 2, 3, true, random);
        generateCorridor(chunk, centerX + 4, centerZ, baseY + 1, 2, 3, true, random);
        generateCorridor(chunk, centerX, centerZ - 4, baseY + 1, 3, 2, false, random);
        generateCorridor(chunk, centerX, centerZ + 4, baseY + 1, 3, 2, false, random);

        // Place villagers - priests/clerics
        spawnVillager(chunk, centerX - 6, centerZ, baseY + 1, Villager.Profession.CLERIC);
        spawnVillager(chunk, centerX + 6, centerZ, baseY + 1, Villager.Profession.CLERIC);

        // Loot chests
        placeLootContainer(chunk, centerX - 6, centerZ - 1, baseY + 1, random);
        placeLootContainer(chunk, centerX + 6, centerZ + 1, baseY + 1, random);
    }

    /**
     * Generates library-style ruins with book storage
     */
    private void generateLibraryRuins(ChunkData chunk, int centerX, int centerZ, int baseY, Random random) {
        // Main library hall (9x5)
        generateChamber(chunk, centerX - 4, centerZ - 2, baseY, 9, 5, 4, random);

        // Bookshelves along walls
        placeBookshelves(chunk, centerX - 3, centerZ - 1, baseY + 1, 7, random);
        placeBookshelves(chunk, centerX - 3, centerZ + 1, baseY + 1, 7, random);

        // Study chambers
        generateChamber(chunk, centerX - 8, centerZ - 2, baseY, 3, 5, 3, random);
        generateChamber(chunk, centerX + 6, centerZ - 2, baseY, 3, 5, 3, random);

        // Central reading area
        chunk.setBlock(centerX, centerZ, baseY + 1, Material.ENCHANTING_TABLE);
        chunk.setBlock(centerX - 1, centerZ, baseY + 1, Material.LECTERN);
        chunk.setBlock(centerX + 1, centerZ, baseY + 1, Material.LECTERN);

        // Spawn librarian villagers
        spawnVillager(chunk, centerX - 7, centerZ, baseY + 1, Villager.Profession.LIBRARIAN);
        spawnVillager(chunk, centerX + 7, centerZ, baseY + 1, Villager.Profession.LIBRARIAN);

        // Knowledge storage
        placeLootContainer(chunk, centerX - 7, centerZ - 1, baseY + 1, random);
        placeLootContainer(chunk, centerX + 7, centerZ + 1, baseY + 1, random);
    }

    /**
     * Generates marketplace-style ruins with trading areas
     */
    private void generateMarketplaceRuins(ChunkData chunk, int centerX, int centerZ, int baseY, Random random) {
        // Central marketplace (11x11)
        generateChamber(chunk, centerX - 5, centerZ - 5, baseY, 11, 11, 3, random);

        // Market stalls around the edges
        for (int i = 0; i < 4; i++) {
            int stallX = centerX + (i % 2 == 0 ? -3 : 3);
            int stallZ = centerZ + (i < 2 ? -3 : 3);
            placeMarketStall(chunk, stallX, stallZ, baseY + 1, random);
        }

        // Storage rooms
        generateChamber(chunk, centerX - 9, centerZ - 2, baseY, 3, 5, 3, random);
        generateChamber(chunk, centerX + 7, centerZ - 2, baseY, 3, 5, 3, random);
        generateChamber(chunk, centerX - 2, centerZ - 9, baseY, 5, 3, 3, random);
        generateChamber(chunk, centerX - 2, centerZ + 7, baseY, 5, 3, 3, random);

        // Spawn merchant villagers
        spawnVillager(chunk, centerX - 3, centerZ - 3, baseY + 1, Villager.Profession.FARMER);
        spawnVillager(chunk, centerX + 3, centerZ - 3, baseY + 1, Villager.Profession.BUTCHER);
        spawnVillager(chunk, centerX - 3, centerZ + 3, baseY + 1, Villager.Profession.ARMORER);
        spawnVillager(chunk, centerX + 3, centerZ + 3, baseY + 1, Villager.Profession.TOOLSMITH);

        // Trade goods storage
        placeLootContainer(chunk, centerX - 8, centerZ, baseY + 1, random);
        placeLootContainer(chunk, centerX + 8, centerZ, baseY + 1, random);
        placeLootContainer(chunk, centerX, centerZ - 8, baseY + 1, random);
        placeLootContainer(chunk, centerX, centerZ + 8, baseY + 1, random);
    }

    /**
     * Generates fortress-style ruins with defensive structures
     */
    private void generateFortressRuins(ChunkData chunk, int centerX, int centerZ, int baseY, Random random) {
        // Central keep (5x5)
        generateChamber(chunk, centerX - 2, centerZ - 2, baseY, 5, 5, 5, random);

        // Watchtowers at corners
        generateTower(chunk, centerX - 6, centerZ - 6, baseY, 3, 6, random);
        generateTower(chunk, centerX + 4, centerZ - 6, baseY, 3, 6, random);
        generateTower(chunk, centerX - 6, centerZ + 4, baseY, 3, 6, random);
        generateTower(chunk, centerX + 4, centerZ + 4, baseY, 3, 6, random);

        // Connecting walls
        generateWall(chunk, centerX - 5, centerZ - 6, baseY + 1, 9, true, random);
        generateWall(chunk, centerX - 5, centerZ + 4, baseY + 1, 9, true, random);
        generateWall(chunk, centerX - 6, centerZ - 5, baseY + 1, 9, false, random);
        generateWall(chunk, centerX + 4, centerZ - 5, baseY + 1, 9, false, random);

        // Armory
        generateChamber(chunk, centerX - 8, centerZ - 1, baseY, 3, 3, 3, random);

        // Spawn guard villagers
        spawnVillager(chunk, centerX, centerZ, baseY + 1, Villager.Profession.WEAPONSMITH);
        spawnVillager(chunk, centerX - 7, centerZ, baseY + 1, Villager.Profession.ARMORER);

        // Weapon storage
        placeLootContainer(chunk, centerX - 7, centerZ - 1, baseY + 1, random);
        placeLootContainer(chunk, centerX + 1, centerZ + 1, baseY + 1, random);
    }

    /**
     * Generates a chamber with walls, floor, and ceiling
     */
    private void generateChamber(ChunkData chunk, int x, int z, int y, int width, int depth, int height,
            Random random) {
        Material wallMaterial = ANCIENT_MATERIALS[random.nextInt(ANCIENT_MATERIALS.length)];
        Material floorMaterial = ANCIENT_MATERIALS[random.nextInt(ANCIENT_MATERIALS.length)];

        // Clear interior space
        for (int dx = 1; dx < width - 1; dx++) {
            for (int dz = 1; dz < depth - 1; dz++) {
                for (int dy = 1; dy < height; dy++) {
                    chunk.setBlock(x + dx, y + dy, z + dz, Material.AIR);
                }
            }
        }

        // Floor
        for (int dx = 0; dx < width; dx++) {
            for (int dz = 0; dz < depth; dz++) {
                chunk.setBlock(x + dx, y, z + dz, floorMaterial);
            }
        }

        // Walls
        for (int dx = 0; dx < width; dx++) {
            for (int dy = 0; dy <= height; dy++) {
                chunk.setBlock(x + dx, y + dy, z, wallMaterial);
                chunk.setBlock(x + dx, y + dy, z + depth - 1, wallMaterial);
            }
        }
        for (int dz = 0; dz < depth; dz++) {
            for (int dy = 0; dy <= height; dy++) {
                chunk.setBlock(x, y + dy, z + dz, wallMaterial);
                chunk.setBlock(x + width - 1, y + dy, z + dz, wallMaterial);
            }
        }

        // Partially damaged ceiling
        for (int dx = 0; dx < width; dx++) {
            for (int dz = 0; dz < depth; dz++) {
                if (random.nextDouble() < 0.7) { // 70% chance for ceiling block
                    chunk.setBlock(x + dx, y + height, z + dz, wallMaterial);
                }
            }
        }

        // Add some lighting
        if (width > 3 && depth > 3) {
            chunk.setBlock(x + width / 2, y + height - 1, z + depth / 2, Material.GLOWSTONE);
        }
    }

    /**
     * Generates a corridor connecting chambers
     */
    private void generateCorridor(ChunkData chunk, int x, int z, int y, int width, int length, boolean horizontal,
            Random random) {
        Material material = ANCIENT_MATERIALS[random.nextInt(ANCIENT_MATERIALS.length)];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                int blockX = horizontal ? x + i : x + j;
                int blockZ = horizontal ? z + j : z + i;

                // Floor
                chunk.setBlock(blockX, y - 1, blockZ, material);
                // Clear space
                chunk.setBlock(blockX, y, blockZ, Material.AIR);
                chunk.setBlock(blockX, y + 1, blockZ, Material.AIR);
                // Ceiling
                if (random.nextDouble() < 0.8) {
                    chunk.setBlock(blockX, y + 2, blockZ, material);
                }
            }
        }
    }

    /**
     * Places an altar with valuable items
     */
    private void placeAltar(ChunkData chunk, int x, int z, int y, Random random) {
        // Altar base
        chunk.setBlock(x, y, z, Material.CHISELED_STONE_BRICKS);
        chunk.setBlock(x, y + 1, z, Material.ENCHANTING_TABLE);

        // Surrounding candles/torches
        chunk.setBlock(x - 1, y, z - 1, Material.TORCH);
        chunk.setBlock(x + 1, y, z - 1, Material.TORCH);
        chunk.setBlock(x - 1, y, z + 1, Material.TORCH);
        chunk.setBlock(x + 1, y, z + 1, Material.TORCH);

        // Rare block on altar
        if (random.nextDouble() < 0.3) {
            Material rareBlock = RARE_BLOCKS[random.nextInt(RARE_BLOCKS.length)];
            chunk.setBlock(x, y + 2, z, rareBlock);
        }
    }

    /**
     * Places bookshelves in a line
     */
    private void placeBookshelves(ChunkData chunk, int x, int z, int y, int length, Random random) {
        for (int i = 0; i < length; i++) {
            if (random.nextDouble() < 0.8) { // Some may be missing/damaged
                chunk.setBlock(x + i, z, y, Material.BOOKSHELF);
            }
        }
    }

    /**
     * Places a market stall with goods
     */
    private void placeMarketStall(ChunkData chunk, int x, int z, int y, Random random) {
        // Stall counter
        chunk.setBlock(x, y, z, Material.BARREL);
        chunk.setBlock(x + 1, y, z, Material.BARREL);

        // Goods display
        if (random.nextBoolean()) {
            chunk.setBlock(x, y + 1, z, Material.ITEM_FRAME);
        }
        if (random.nextBoolean()) {
            chunk.setBlock(x + 1, y + 1, z, Material.ITEM_FRAME);
        }
    }

    /**
     * Generates a tower structure
     */
    private void generateTower(ChunkData chunk, int x, int z, int y, int size, int height, Random random) {
        Material material = ANCIENT_MATERIALS[random.nextInt(ANCIENT_MATERIALS.length)];

        // Tower walls
        for (int dy = 0; dy < height; dy++) {
            for (int dx = 0; dx < size; dx++) {
                for (int dz = 0; dz < size; dz++) {
                    boolean isWall = dx == 0 || dx == size - 1 || dz == 0 || dz == size - 1;
                    if (isWall) {
                        chunk.setBlock(x + dx, y + dy, z + dz, material);
                    } else if (dy == 0) {
                        chunk.setBlock(x + dx, y + dy, z + dz, material); // Floor
                    } else {
                        chunk.setBlock(x + dx, y + dy, z + dz, Material.AIR);
                    }
                }
            }
        }

        // Add some lighting
        chunk.setBlock(x + size / 2, y + height - 2, z + size / 2, Material.TORCH);
    }

    /**
     * Generates a wall structure
     */
    private void generateWall(ChunkData chunk, int x, int z, int y, int length, boolean horizontal, Random random) {
        Material material = ANCIENT_MATERIALS[random.nextInt(ANCIENT_MATERIALS.length)];

        for (int i = 0; i < length; i++) {
            for (int h = 0; h < 3; h++) {
                int wallX = horizontal ? x + i : x;
                int wallZ = horizontal ? z : z + i;

                if (random.nextDouble() < 0.6) { // Partially ruined walls
                    chunk.setBlock(wallX, y + h, wallZ, material);
                }
            }
        }
    }

    /**
     * Spawns a villager with the specified profession
     * Note: This sets a spawner block that can be processed by server logic
     */
    private void spawnVillager(ChunkData chunk, int x, int z, int y, Villager.Profession profession) {
        // Place a spawner that can be converted to villager by server logic
        chunk.setBlock(x, y, z, Material.SPAWNER);
        // TODO: Add metadata to specify villager profession
    }

    /**
     * Places a loot container with treasure
     */
    private void placeLootContainer(ChunkData chunk, int x, int z, int y, Random random) {
        Material container = LOOT_CONTAINERS[random.nextInt(LOOT_CONTAINERS.length)];
        chunk.setBlock(x, y, z, container);
    }

    /**
     * Selects the type of ruins based on biome and randomness
     */
    private RuinsType selectRuinsType(Random random, String biome) {
        return switch (biome.toLowerCase()) {
            case "desert", "badlands" -> random.nextBoolean() ? RuinsType.TEMPLE : RuinsType.FORTRESS;
            case "jungle", "forest" -> random.nextBoolean() ? RuinsType.TEMPLE : RuinsType.LIBRARY;
            case "plains", "meadow" -> random.nextBoolean() ? RuinsType.MARKETPLACE : RuinsType.LIBRARY;
            case "mountains", "taiga" -> random.nextBoolean() ? RuinsType.FORTRESS : RuinsType.LIBRARY;
            default -> RuinsType.values()[random.nextInt(RuinsType.values().length)];
        };
    }

    /**
     * Hash function for coordinate-based randomness
     */
    private long hashCoords(int x, int z) {
        return x * 341873128712L + z * 132897987541L;
    }

    /**
     * Types of ancient ruins that can be generated
     */
    private enum RuinsType {
        TEMPLE, // Religious/ceremonial structures
        LIBRARY, // Knowledge and books
        MARKETPLACE, // Trading and commerce
        FORTRESS // Military/defensive
    }
}
