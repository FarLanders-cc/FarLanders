package cc.farlanders.generate.spawning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.ChunkGenerator.ChunkData;

/**
 * Enhanced mob spawning system that creates suitable environments for passive
 * mobs
 * and places spawning platforms in various biomes
 */
public class MobSpawningManager {

    private MobSpawningManager() {
        // Prevent instantiation
    }

    private static final Random random = new Random();

    /**
     * Creates suitable spawning environments for passive mobs based on biome
     */
    public static void generateMobSpawningAreas(ChunkData chunk, int chunkX, int chunkZ, String biome) {
        // Generate spawning areas based on biome type
        switch (biome.toLowerCase()) {
            case "plains", "sunflower_plains" -> generatePlainsSpawning(chunk, chunkX, chunkZ);
            case "forest", "birch_forest", "dark_forest" -> generateForestSpawning(chunk, chunkX, chunkZ);
            case "desert" -> generateDesertSpawning(chunk, chunkX, chunkZ);
            case "savanna", "savanna_plateau" -> generateSavannaSpawning(chunk, chunkX, chunkZ);
            case "taiga", "snowy_taiga" -> generateTaigaSpawning(chunk, chunkX, chunkZ);
            case "jungle" -> generateJungleSpawning(chunk, chunkX, chunkZ);
            case "swamp" -> generateSwampSpawning(chunk, chunkX, chunkZ);
            case "mushroom_fields" -> generateMushroomSpawning(chunk, chunkX, chunkZ);
            default -> generateGenericSpawning(chunk, chunkX, chunkZ);
        }
    }

    private static void generatePlainsSpawning(ChunkData chunk, int chunkX, int chunkZ) {
        // Create small farm-like areas and grazing spots - more spaced out
        if (random.nextInt(12) == 0) { // Increased from 4 to 12
            createGrazingArea(chunk, 5 + random.nextInt(6), 5 + random.nextInt(6), Material.GRASS_BLOCK);
        }

        // Occasional small ponds for animals to drink
        if (random.nextInt(18) == 0) { // Increased from 8 to 18
            createSmallPond(chunk, 3 + random.nextInt(10), 3 + random.nextInt(10));
        }
    }

    private static void generateForestSpawning(ChunkData chunk, int chunkX, int chunkZ) {
        // Create forest clearings for wolves, foxes, and occasional farm animals - more
        // spaced
        if (random.nextInt(15) == 0) { // Increased from 6 to 15
            createForestClearing(chunk, 4 + random.nextInt(8), 4 + random.nextInt(8));
        }

        // Berry bushes and hiding spots
        if (random.nextInt(8) == 0) { // Increased from 3 to 8
            createBerryPatch(chunk, 2 + random.nextInt(12), 2 + random.nextInt(12));
        }
    }

    private static void generateDesertSpawning(ChunkData chunk, int chunkX, int chunkZ) {
        // Oases for camels and rare desert animals
        if (random.nextInt(12) == 0) {
            createDesertOasis(chunk, 6 + random.nextInt(4), 6 + random.nextInt(4));
        }

        // Cactus groves
        if (random.nextInt(5) == 0) {
            createCactusGrove(chunk, 3 + random.nextInt(10), 3 + random.nextInt(10));
        }
    }

    private static void generateSavannaSpawning(ChunkData chunk, int chunkX, int chunkZ) {
        // Watering holes for llamas and horses
        if (random.nextInt(6) == 0) {
            createWateringHole(chunk, 4 + random.nextInt(8), 4 + random.nextInt(8));
        }

        // Acacia groves
        if (random.nextInt(4) == 0) {
            createAcaciaGrove(chunk, 5 + random.nextInt(6), 5 + random.nextInt(6));
        }
    }

    private static void generateTaigaSpawning(ChunkData chunk, int chunkX, int chunkZ) {
        // Wolf dens and fox hideouts
        if (random.nextInt(8) == 0) {
            createWolfDen(chunk, 4 + random.nextInt(8), 4 + random.nextInt(8));
        }

        // Berry patches in the snow
        if (random.nextInt(5) == 0) {
            createSnowBerryPatch(chunk, 3 + random.nextInt(10), 3 + random.nextInt(10));
        }
    }

    private static void generateJungleSpawning(ChunkData chunk, int chunkX, int chunkZ) {
        // Parrot perches and monkey platforms
        if (random.nextInt(4) == 0) {
            createJunglePerch(chunk, 6 + random.nextInt(4), 6 + random.nextInt(4));
        }

        // Jungle streams
        if (random.nextInt(6) == 0) {
            createJungleStream(chunk, 2 + random.nextInt(12), 2 + random.nextInt(12));
        }
    }

    private static void generateSwampSpawning(ChunkData chunk, int chunkX, int chunkZ) {
        // Lily pad areas for frogs
        if (random.nextInt(3) == 0) {
            createLilyPadArea(chunk, 4 + random.nextInt(8), 4 + random.nextInt(8));
        }

        // Muddy banks
        if (random.nextInt(4) == 0) {
            createMuddyBank(chunk, 5 + random.nextInt(6), 5 + random.nextInt(6));
        }
    }

    private static void generateMushroomSpawning(ChunkData chunk, int chunkX, int chunkZ) {
        // Mooshroom grazing areas
        if (random.nextInt(3) == 0) {
            createMooshroomArea(chunk, 6 + random.nextInt(4), 6 + random.nextInt(4));
        }

        // Giant mushroom circles
        if (random.nextInt(5) == 0) {
            createMushroomCircle(chunk, 4 + random.nextInt(8), 4 + random.nextInt(8));
        }
    }

    private static void generateGenericSpawning(ChunkData chunk, int chunkX, int chunkZ) {
        // Basic spawning area for any biome
        if (random.nextInt(10) == 0) {
            createGrazingArea(chunk, 4 + random.nextInt(8), 4 + random.nextInt(8), Material.GRASS_BLOCK);
        }
    }

    // Helper methods for creating specific spawning structures

    private static void createGrazingArea(ChunkData chunk, int centerX, int centerZ, Material groundMaterial) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        // Create a circular grazing area
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (x * x + z * z <= 9 && isValidPosition(centerX + x, centerZ + z)) {
                    setGrazingBlock(chunk, centerX + x, centerZ + z, groundMaterial);
                }
            }
        }
    }

    private static void setGrazingBlock(ChunkData chunk, int x, int z, Material groundMaterial) {
        int targetY = findSurfaceY(chunk, x, z);
        if (targetY > 0) {
            chunk.setBlock(x, targetY, z, groundMaterial);

            // Add some grass and flowers
            if (random.nextInt(3) == 0) {
                chunk.setBlock(x, targetY + 1, z, Material.TALL_GRASS);
            } else if (random.nextInt(8) == 0) {
                chunk.setBlock(x, targetY + 1, z,
                        random.nextBoolean() ? Material.DANDELION : Material.POPPY);
            }
        }
    }

    private static void createSmallPond(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        // Create a small circular pond
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x * x + z * z <= 4 && isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, surfaceY - 1, centerZ + z, Material.WATER);
                    chunk.setBlock(centerX + x, surfaceY, centerZ + z, Material.AIR);

                    // Add lily pads occasionally
                    if (random.nextInt(4) == 0) {
                        chunk.setBlock(centerX + x, surfaceY, centerZ + z, Material.LILY_PAD);
                    }
                }
            }
        }
    }

    private static void createForestClearing(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        // Create a forest clearing with soft grass
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                handleForestClearingBlock(chunk, centerX, centerZ, surfaceY, x, z);
            }
        }
    }

    private static void handleForestClearingBlock(ChunkData chunk, int centerX, int centerZ, int surfaceY, int x,
            int z) {
        if (x * x + z * z <= 16 && isValidPosition(centerX + x, centerZ + z)) {
            chunk.setBlock(centerX + x, surfaceY, centerZ + z, Material.GRASS_BLOCK);

            // Add ferns and grass
            if (random.nextInt(2) == 0) {
                chunk.setBlock(centerX + x, surfaceY + 1, centerZ + z, Material.FERN);
            }

            // Occasional fallen logs (using stripped wood)
            if (random.nextInt(15) == 0) {
                chunk.setBlock(centerX + x, surfaceY + 1, centerZ + z, Material.STRIPPED_OAK_LOG);
            }
        }
    }

    private static void createBerryPatch(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        // Create berry bushes for food sources
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (isValidPosition(centerX + x, centerZ + z) && random.nextInt(3) == 0) {
                    chunk.setBlock(centerX + x, surfaceY + 1, centerZ + z, Material.SWEET_BERRY_BUSH);
                }
            }
        }
    }

    private static void createDesertOasis(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        // Central water source
        chunk.setBlock(centerX, surfaceY - 1, centerZ, Material.WATER);
        chunk.setBlock(centerX, surfaceY, centerZ, Material.AIR);

        // Surrounding palm-like trees and grass
        placePalmTreesAndGrass(chunk, centerX, centerZ, surfaceY);
    }

    private static void placePalmTreesAndGrass(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                handlePalmTreeAndGrassBlock(chunk, centerX, centerZ, surfaceY, x, z);
            }
        }
    }

    private static void handlePalmTreeAndGrassBlock(ChunkData chunk, int centerX, int centerZ, int surfaceY, int x,
            int z) {
        int distanceSquared = x * x + z * z;
        if (distanceSquared <= 9 && distanceSquared > 1 && isValidPosition(centerX + x, centerZ + z)) {
            if (random.nextInt(4) == 0) {
                chunk.setBlock(centerX + x, surfaceY, centerZ + z, Material.GRASS_BLOCK);
                if (random.nextInt(3) == 0) {
                    chunk.setBlock(centerX + x, surfaceY + 1, centerZ + z, Material.JUNGLE_SAPLING);
                }
            }
        }
    }

    // Additional helper methods for other spawning areas would continue here...
    // (I'll include a few more key ones)

    private static void createCactusGrove(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        for (int i = 0; i < 3 + random.nextInt(3); i++) {
            int x = centerX + random.nextInt(6) - 3;
            int z = centerZ + random.nextInt(6) - 3;
            if (isValidPosition(x, z)) {
                chunk.setBlock(x, surfaceY, z, Material.SAND);
                chunk.setBlock(x, surfaceY + 1, z, Material.CACTUS);
                if (random.nextBoolean()) {
                    chunk.setBlock(x, surfaceY + 2, z, Material.CACTUS);
                }
            }
        }
    }

    private static void createWateringHole(ChunkData chunk, int centerX, int centerZ) {
        createSmallPond(chunk, centerX, centerZ);

        // Add acacia wood around the edges
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (x * x + z * z > 4 && x * x + z * z <= 9 && isValidPosition(centerX + x, centerZ + z)) {
                    if (random.nextInt(6) == 0) {
                        int surfaceY = findSurfaceY(chunk, centerX + x, centerZ + z);
                        chunk.setBlock(centerX + x, surfaceY + 1, centerZ + z, Material.ACACIA_LOG);
                    }
                }
            }
        }
    }

    private static void createAcaciaGrove(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        for (int i = 0; i < 2 + random.nextInt(3); i++) {
            int x = centerX + random.nextInt(10) - 5;
            int z = centerZ + random.nextInt(10) - 5;
            if (isValidPosition(x, z)) {
                chunk.setBlock(x, surfaceY + 1, z, Material.ACACIA_SAPLING);
            }
        }
    }

    private static void createWolfDen(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        // Create a small cave-like structure
        chunk.setBlock(centerX, surfaceY, centerZ, Material.COBBLESTONE);
        chunk.setBlock(centerX, surfaceY + 1, centerZ, Material.AIR);

        // Add some bones and wolf-friendly terrain
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (isValidPosition(centerX + x, centerZ + z) && random.nextInt(8) == 0) {
                    chunk.setBlock(centerX + x, surfaceY + 1, centerZ + z, Material.BONE_BLOCK);
                }
            }
        }
    }

    private static void createSnowBerryPatch(ChunkData chunk, int centerX, int centerZ) {
        createBerryPatch(chunk, centerX, centerZ);

        // Add snow layers
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (isValidPosition(centerX + x, centerZ + z) && random.nextInt(3) == 0) {
                    int surfaceY = findSurfaceY(chunk, centerX + x, centerZ + z);
                    chunk.setBlock(centerX + x, surfaceY + 1, centerZ + z, Material.SNOW);
                }
            }
        }
    }

    private static void createJunglePerch(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        // Create elevated platforms for jungle animals
        for (int y = 1; y <= 3; y++) {
            chunk.setBlock(centerX, surfaceY + y, centerZ, Material.JUNGLE_LOG);
        }

        // Natural platform using jungle logs only (no artificial planks)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, surfaceY + 4, centerZ + z, Material.JUNGLE_LOG);
                }
            }
        }
    }

    private static void createJungleStream(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        // Create a small flowing stream
        for (int i = -4; i <= 4; i++) {
            if (isValidPosition(centerX + i, centerZ)) {
                chunk.setBlock(centerX + i, surfaceY - 1, centerZ, Material.WATER);
                chunk.setBlock(centerX + i, surfaceY, centerZ, Material.AIR);
            }
        }
    }

    private static void createLilyPadArea(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (x * x + z * z <= 9 && isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, surfaceY - 1, centerZ + z, Material.WATER);
                    if (random.nextInt(2) == 0) {
                        chunk.setBlock(centerX + x, surfaceY, centerZ + z, Material.LILY_PAD);
                    }
                }
            }
        }
    }

    private static void createMuddyBank(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, surfaceY, centerZ + z, getMaterialOrFallback("MUD", Material.DIRT));
                    if (random.nextInt(4) == 0) {
                        chunk.setBlock(centerX + x, surfaceY + 1, centerZ + z, Material.DEAD_BUSH);
                    }
                }
            }
        }
    }

    private static void createMooshroomArea(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                handleMooshroomBlock(chunk, centerX, centerZ, surfaceY, x, z);
            }
        }
    }

    private static void handleMooshroomBlock(ChunkData chunk, int centerX, int centerZ, int surfaceY, int x, int z) {
        if (x * x + z * z <= 16 && isValidPosition(centerX + x, centerZ + z)) {
            chunk.setBlock(centerX + x, surfaceY, centerZ + z, Material.MYCELIUM);

            if (random.nextInt(5) == 0) {
                Material mushroom = random.nextBoolean() ? Material.RED_MUSHROOM : Material.BROWN_MUSHROOM;
                chunk.setBlock(centerX + x, surfaceY + 1, centerZ + z, mushroom);
            }
        }
    }

    private static void createMushroomCircle(ChunkData chunk, int centerX, int centerZ) {
        int surfaceY = findSurfaceY(chunk, centerX, centerZ);
        if (surfaceY <= 0)
            return;

        // Create a fairy ring of mushrooms
        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 4) {
            int x = centerX + (int) (3 * Math.cos(angle));
            int z = centerZ + (int) (3 * Math.sin(angle));
            if (isValidPosition(x, z)) {
                chunk.setBlock(x, surfaceY + 1, z, Material.RED_MUSHROOM);
            }
        }
    }

    // Utility methods

    private static int findSurfaceY(ChunkData chunk, int x, int z) {
        if (!isValidPosition(x, z))
            return 0;

        for (int y = 100; y >= 1; y--) {
            Material material = chunk.getBlockData(x, y, z).getMaterial();
            if (material != Material.AIR && material != Material.WATER) {
                return y;
            }
        }
        return 0;
    }

    private static boolean isValidPosition(int x, int z) {
        return x >= 0 && x < 16 && z >= 0 && z < 16;
    }

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

    /**
     * Safely adds an entity type to the list if it exists in the current Minecraft
     * version
     */
    private static void addEntityIfExists(List<EntityType> mobs, String entityName) {
        try {
            EntityType entityType = EntityType.valueOf(entityName);
            mobs.add(entityType);
        } catch (IllegalArgumentException e) {
            // Entity not available in this Minecraft version, skip
        }
    }

    /**
     * Gets recommended mob types for a given biome
     */
    public static List<EntityType> getRecommendedMobs(String biome) {
        List<EntityType> mobs = new ArrayList<>();

        switch (biome.toLowerCase()) {
            case "plains", "sunflower_plains" -> {
                mobs.add(EntityType.COW);
                mobs.add(EntityType.SHEEP);
                mobs.add(EntityType.HORSE);
                mobs.add(EntityType.PIG);
                mobs.add(EntityType.CHICKEN);
            }
            case "forest", "birch_forest" -> {
                mobs.add(EntityType.WOLF);
                mobs.add(EntityType.FOX);
                mobs.add(EntityType.COW);
                mobs.add(EntityType.CHICKEN);
            }
            case "dark_forest" -> {
                mobs.add(EntityType.WOLF);
                mobs.add(EntityType.FOX);
            }
            case "desert" -> {
                addEntityIfExists(mobs, "CAMEL");
                mobs.add(EntityType.RABBIT);
            }
            case "savanna", "savanna_plateau" -> {
                mobs.add(EntityType.LLAMA);
                mobs.add(EntityType.HORSE);
                mobs.add(EntityType.COW);
            }
            case "taiga", "snowy_taiga" -> {
                mobs.add(EntityType.WOLF);
                mobs.add(EntityType.FOX);
                mobs.add(EntityType.RABBIT);
            }
            case "jungle" -> {
                mobs.add(EntityType.PARROT);
                mobs.add(EntityType.OCELOT);
                addEntityIfExists(mobs, "PANDA");
            }
            case "swamp" -> {
                addEntityIfExists(mobs, "FROG");
                mobs.add(EntityType.SLIME);
            }
            case "mushroom_fields" -> {
                addEntityIfExists(mobs, "MOOSHROOM");
                // Fallback for older versions
                if (mobs.isEmpty()) {
                    addEntityIfExists(mobs, "MUSHROOM_COW");
                }
            }
            case "ocean", "deep_ocean" -> {
                mobs.add(EntityType.SQUID);
                mobs.add(EntityType.DOLPHIN);
                mobs.add(EntityType.TURTLE);
            }
            default -> {
                // Optionally add a generic passive mob for unknown biomes
                mobs.add(EntityType.SHEEP);
            }
        }

        return mobs;
    }
}
