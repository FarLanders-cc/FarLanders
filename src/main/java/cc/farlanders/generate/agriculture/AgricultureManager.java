package cc.farlanders.generate.agriculture;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator.ChunkData;

/**
 * Enhanced agriculture system that creates farm-like structures and crop areas
 * throughout the FarLands world
 */
public class AgricultureManager {

    private static final Random random = new Random();

    /**
     * Generates agricultural structures based on biome type
     */
    public static void generateAgriculture(ChunkData chunk, int chunkX, int chunkZ, String biome, int surfaceY) {
        switch (biome.toLowerCase()) {
            case "plains", "sunflower_plains" -> generatePlainsFarms(chunk, chunkX, chunkZ, surfaceY);
            case "forest", "birch_forest" -> generateForestGardens(chunk, chunkX, chunkZ, surfaceY);
            case "desert" -> generateDesertFarms(chunk, chunkX, chunkZ, surfaceY);
            case "savanna", "savanna_plateau" -> generateSavannaFarms(chunk, chunkX, chunkZ, surfaceY);
            case "taiga", "snowy_taiga" -> generateTaigaGreenhouses(chunk, chunkX, chunkZ, surfaceY);
            case "jungle" -> generateJunglePlantations(chunk, chunkX, chunkZ, surfaceY);
            case "swamp" -> generateSwampCrops(chunk, chunkX, chunkZ, surfaceY);
            default -> generateGenericFarm(chunk, chunkX, chunkZ, surfaceY);
        }
    }

    private static void generatePlainsFarms(ChunkData chunk, int chunkX, int chunkZ, int surfaceY) {
        // Large wheat and vegetable fields with scarecrows
        if (random.nextInt(6) == 0) {
            createWheatField(chunk, 3 + random.nextInt(10), 3 + random.nextInt(10), surfaceY);
        }

        if (random.nextInt(8) == 0) {
            createVegetableGarden(chunk, 5 + random.nextInt(6), 5 + random.nextInt(6), surfaceY);
        }

        if (random.nextInt(12) == 0) {
            createScarecrow(chunk, 8 + random.nextInt(8), 8 + random.nextInt(8), surfaceY);
        }
    }

    private static void generateForestGardens(ChunkData chunk, int chunkX, int chunkZ, int surfaceY) {
        // Small clearings with berry bushes and mushroom farms
        if (random.nextInt(8) == 0) {
            createBerryFarm(chunk, 4 + random.nextInt(8), 4 + random.nextInt(8), surfaceY);
        }

        if (random.nextInt(6) == 0) {
            createMushroomFarm(chunk, 6 + random.nextInt(4), 6 + random.nextInt(4), surfaceY);
        }

        if (random.nextInt(10) == 0) {
            createTreeOrchard(chunk, 2 + random.nextInt(12), 2 + random.nextInt(12), surfaceY);
        }
    }

    private static void generateDesertFarms(ChunkData chunk, int chunkX, int chunkZ, int surfaceY) {
        // Oasis farms with irrigation systems
        if (random.nextInt(15) == 0) {
            createOasisFarm(chunk, 6 + random.nextInt(4), 6 + random.nextInt(4), surfaceY);
        }

        if (random.nextInt(8) == 0) {
            createCactusFarm(chunk, 4 + random.nextInt(8), 4 + random.nextInt(8), surfaceY);
        }
    }

    private static void generateSavannaFarms(ChunkData chunk, int chunkX, int chunkZ, int surfaceY) {
        // Acacia tree farms and grassland agriculture
        if (random.nextInt(10) == 0) {
            createAcaciaFarm(chunk, 5 + random.nextInt(6), 5 + random.nextInt(6), surfaceY);
        }

        if (random.nextInt(7) == 0) {
            createGrasslandFarm(chunk, 4 + random.nextInt(8), 4 + random.nextInt(8), surfaceY);
        }
    }

    private static void generateTaigaGreenhouses(ChunkData chunk, int chunkX, int chunkZ, int surfaceY) {
        // Protected growing areas for cold climates
        if (random.nextInt(12) == 0) {
            createGreenhouse(chunk, 5 + random.nextInt(6), 5 + random.nextInt(6), surfaceY);
        }

        if (random.nextInt(8) == 0) {
            createColdFrameGarden(chunk, 3 + random.nextInt(10), 3 + random.nextInt(10), surfaceY);
        }
    }

    private static void generateJunglePlantations(ChunkData chunk, int chunkX, int chunkZ, int surfaceY) {
        // Tropical crop plantations
        if (random.nextInt(6) == 0) {
            createCocoaPlantation(chunk, 4 + random.nextInt(8), 4 + random.nextInt(8), surfaceY);
        }

        if (random.nextInt(8) == 0) {
            createBambooFarm(chunk, 6 + random.nextInt(4), 6 + random.nextInt(4), surfaceY);
        }

        if (random.nextInt(10) == 0) {
            createMelonPatch(chunk, 3 + random.nextInt(10), 3 + random.nextInt(10), surfaceY);
        }
    }

    private static void generateSwampCrops(ChunkData chunk, int chunkX, int chunkZ, int surfaceY) {
        // Water-based agriculture
        if (random.nextInt(8) == 0) {
            createRicePaddy(chunk, 5 + random.nextInt(6), 5 + random.nextInt(6), surfaceY);
        }

        if (random.nextInt(6) == 0) {
            createKelpFarm(chunk, 4 + random.nextInt(8), 4 + random.nextInt(8), surfaceY);
        }
    }

    private static void generateGenericFarm(ChunkData chunk, int chunkX, int chunkZ, int surfaceY) {
        if (random.nextInt(12) == 0) {
            createWheatField(chunk, 4 + random.nextInt(8), 4 + random.nextInt(8), surfaceY);
        }
    }

    // Farm creation methods

    private static void createWheatField(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Create a 5x5 wheat field with water irrigation
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    // Central water source
                    if (x == 0 && z == 0) {
                        chunk.setBlock(centerX + x, targetY - 1, centerZ + z, Material.WATER);
                        chunk.setBlock(centerX + x, targetY, centerZ + z, Material.AIR);
                    } else {
                        // Farmland with crops
                        chunk.setBlock(centerX + x, targetY, centerZ + z, Material.FARMLAND);

                        Material crop = switch (random.nextInt(4)) {
                            case 0 -> Material.WHEAT;
                            case 1 -> Material.CARROTS;
                            case 2 -> Material.POTATOES;
                            default -> Material.BEETROOTS;
                        };
                        chunk.setBlock(centerX + x, targetY + 1, centerZ + z, crop);
                    }
                }
            }
        }

        // Add fence posts around the field
        createFarmFencing(chunk, centerX, centerZ, targetY, 3);
    }

    private static void createVegetableGarden(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Mixed vegetable garden with paths
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    // Create paths
                    if (x == 0 || z == 0) {
                        chunk.setBlock(centerX + x, targetY, centerZ + z, Material.DIRT_PATH);
                    } else {
                        // Various crops
                        chunk.setBlock(centerX + x, targetY, centerZ + z, Material.FARMLAND);

                        Material crop = switch (random.nextInt(6)) {
                            case 0 -> Material.PUMPKIN_STEM;
                            case 1 -> Material.MELON_STEM;
                            case 2 -> Material.CARROTS;
                            case 3 -> Material.POTATOES;
                            case 4 -> Material.BEETROOTS;
                            default -> Material.WHEAT;
                        };
                        chunk.setBlock(centerX + x, targetY + 1, centerZ + z, crop);
                    }
                }
            }
        }
    }

    private static void createScarecrow(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0 || !isValidPosition(centerX, centerZ))
            return;

        // Scarecrow post
        chunk.setBlock(centerX, targetY + 1, centerZ, Material.OAK_FENCE);
        chunk.setBlock(centerX, targetY + 2, centerZ, Material.OAK_FENCE);

        // Arms
        chunk.setBlock(centerX - 1, targetY + 2, centerZ, Material.OAK_FENCE);
        chunk.setBlock(centerX + 1, targetY + 2, centerZ, Material.OAK_FENCE);

        // Head
        chunk.setBlock(centerX, targetY + 3, centerZ, Material.PUMPKIN);

        // Hat
        chunk.setBlock(centerX, targetY + 4, centerZ, Material.BLACK_CARPET);
    }

    private static void createBerryFarm(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Rows of berry bushes
        for (int x = -3; x <= 3; x++) {
            for (int z = -2; z <= 2; z += 2) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY, centerZ + z, Material.GRASS_BLOCK);
                    chunk.setBlock(centerX + x, targetY + 1, centerZ + z, Material.SWEET_BERRY_BUSH);
                }
            }
        }

        // Add compost area
        if (isValidPosition(centerX + 4, centerZ)) {
            chunk.setBlock(centerX + 4, targetY + 1, centerZ, Material.COMPOSTER);
        }
    }

    private static void createMushroomFarm(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Dark growing area for mushrooms
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY, centerZ + z, Material.PODZOL);

                    if (random.nextInt(3) == 0) {
                        Material mushroom = random.nextBoolean() ? Material.RED_MUSHROOM : Material.BROWN_MUSHROOM;
                        chunk.setBlock(centerX + x, targetY + 1, centerZ + z, mushroom);
                    }
                }
            }
        }

        // Create shade structure
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if ((Math.abs(x) == 3 || Math.abs(z) == 3) && isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY + 3, centerZ + z, Material.DARK_OAK_LEAVES);
                }
            }
        }
    }

    private static void createTreeOrchard(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Plant fruit trees in a grid pattern
        for (int x = -4; x <= 4; x += 2) {
            for (int z = -4; z <= 4; z += 2) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY, centerZ + z, Material.GRASS_BLOCK);

                    Material sapling = switch (random.nextInt(4)) {
                        case 0 -> Material.OAK_SAPLING;
                        case 1 -> Material.OAK_SAPLING;
                        case 2 -> Material.BIRCH_SAPLING;
                        default -> Material.CHERRY_SAPLING;
                    };
                    chunk.setBlock(centerX + x, targetY + 1, centerZ + z, sapling);
                }
            }
        }
    }

    private static void createOasisFarm(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Central water source
        chunk.setBlock(centerX, targetY - 1, centerZ, Material.WATER);
        chunk.setBlock(centerX, targetY, centerZ, Material.AIR);

        // Surrounding irrigation channels and crops
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (isValidPosition(centerX + x, centerZ + z) && !(x == 0 && z == 0)) {
                    if (Math.abs(x) == 1 || Math.abs(z) == 1) {
                        // Irrigation channels
                        chunk.setBlock(centerX + x, targetY - 1, centerZ + z, Material.WATER);
                        chunk.setBlock(centerX + x, targetY, centerZ + z, Material.AIR);
                    } else {
                        // Farmland
                        chunk.setBlock(centerX + x, targetY, centerZ + z, Material.FARMLAND);
                        chunk.setBlock(centerX + x, targetY + 1, centerZ + z, Material.WHEAT);
                    }
                }
            }
        }

        // Add palm trees around the oasis
        for (int i = 0; i < 3; i++) {
            int x = centerX + random.nextInt(8) - 4;
            int z = centerZ + random.nextInt(8) - 4;
            if (isValidPosition(x, z) && Math.abs(x - centerX) > 2 && Math.abs(z - centerZ) > 2) {
                chunk.setBlock(x, targetY + 1, z, Material.JUNGLE_SAPLING);
            }
        }
    }

    private static void createCactusFarm(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Organized cactus plantation
        for (int x = -3; x <= 3; x += 2) {
            for (int z = -3; z <= 3; z += 2) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY, centerZ + z, Material.SAND);
                    chunk.setBlock(centerX + x, targetY + 1, centerZ + z, Material.CACTUS);
                    if (random.nextBoolean()) {
                        chunk.setBlock(centerX + x, targetY + 2, centerZ + z, Material.CACTUS);
                    }
                }
            }
        }

        // Storage chest for harvested cactus
        if (isValidPosition(centerX + 4, centerZ)) {
            chunk.setBlock(centerX + 4, targetY + 1, centerZ, Material.CHEST);
        }
    }

    private static void createAcaciaFarm(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Acacia tree plantation
        for (int x = -3; x <= 3; x += 2) {
            for (int z = -3; z <= 3; z += 2) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY, centerZ + z, Material.GRASS_BLOCK);
                    chunk.setBlock(centerX + x, targetY + 1, centerZ + z, Material.ACACIA_SAPLING);
                }
            }
        }
    }

    private static void createGrasslandFarm(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Managed grassland for livestock
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY, centerZ + z, Material.GRASS_BLOCK);

                    if (random.nextInt(4) == 0) {
                        chunk.setBlock(centerX + x, targetY + 1, centerZ + z, Material.TALL_GRASS);
                    }
                }
            }
        }

        // Hay bales for storage
        for (int i = 0; i < 2; i++) {
            int x = centerX + random.nextInt(6) - 3;
            int z = centerZ + random.nextInt(6) - 3;
            if (isValidPosition(x, z)) {
                chunk.setBlock(x, targetY + 1, z, Material.HAY_BLOCK);
            }
        }
    }

    private static void createGreenhouse(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Glass structure for cold climate growing
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    // Glass walls
                    if (Math.abs(x) == 3 || Math.abs(z) == 3) {
                        chunk.setBlock(centerX + x, targetY + 1, centerZ + z, Material.GLASS);
                        chunk.setBlock(centerX + x, targetY + 2, centerZ + z, Material.GLASS);
                    }
                    // Glass roof
                    if (Math.abs(x) <= 3 && Math.abs(z) <= 3) {
                        chunk.setBlock(centerX + x, targetY + 3, centerZ + z, Material.GLASS);
                    }
                }
            }
        }

        // Interior crops
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY, centerZ + z, Material.FARMLAND);
                    chunk.setBlock(centerX + x, targetY + 1, centerZ + z, Material.WHEAT);
                }
            }
        }
    }

    private static void createColdFrameGarden(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Low protection for cold weather crops
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY, centerZ + z, Material.FARMLAND);

                    Material crop = switch (random.nextInt(3)) {
                        case 0 -> Material.CARROTS;
                        case 1 -> Material.POTATOES;
                        default -> Material.BEETROOTS;
                    };
                    chunk.setBlock(centerX + x, targetY + 1, centerZ + z, crop);
                }
            }
        }

        // Glass cover
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if ((Math.abs(x) == 3 || Math.abs(z) == 3) && isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY + 2, centerZ + z, Material.GLASS_PANE);
                }
            }
        }
    }

    private static void createCocoaPlantation(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Jungle trees with cocoa pods
        for (int x = -3; x <= 3; x += 2) {
            for (int z = -3; z <= 3; z += 2) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    // Plant jungle trees
                    chunk.setBlock(centerX + x, targetY + 1, centerZ + z, Material.JUNGLE_LOG);
                    chunk.setBlock(centerX + x, targetY + 2, centerZ + z, Material.JUNGLE_LOG);
                    chunk.setBlock(centerX + x, targetY + 3, centerZ + z, Material.JUNGLE_LOG);

                    // Add cocoa pods on the side
                    if (random.nextBoolean() && isValidPosition(centerX + x + 1, centerZ + z)) {
                        chunk.setBlock(centerX + x + 1, targetY + 2, centerZ + z, Material.COCOA);
                    }
                }
            }
        }
    }

    private static void createBambooFarm(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Bamboo grove
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (isValidPosition(centerX + x, centerZ + z) && random.nextInt(2) == 0) {
                    chunk.setBlock(centerX + x, targetY, centerZ + z, Material.GRASS_BLOCK);
                    chunk.setBlock(centerX + x, targetY + 1, centerZ + z, Material.BAMBOO);
                    chunk.setBlock(centerX + x, targetY + 2, centerZ + z, Material.BAMBOO);
                    if (random.nextBoolean()) {
                        chunk.setBlock(centerX + x, targetY + 3, centerZ + z, Material.BAMBOO);
                    }
                }
            }
        }
    }

    private static void createMelonPatch(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Melon and pumpkin vines
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY, centerZ + z, Material.FARMLAND);

                    if (random.nextInt(3) == 0) {
                        Material crop = random.nextBoolean() ? Material.MELON_STEM : Material.PUMPKIN_STEM;
                        chunk.setBlock(centerX + x, targetY + 1, centerZ + z, crop);
                    }

                    // Occasional fully grown fruits
                    if (random.nextInt(8) == 0) {
                        Material fruit = random.nextBoolean() ? Material.MELON : Material.PUMPKIN;
                        chunk.setBlock(centerX + x, targetY + 1, centerZ + z, fruit);
                    }
                }
            }
        }
    }

    private static void createRicePaddy(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Flooded rice fields
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY - 1, centerZ + z, Material.WATER);
                    chunk.setBlock(centerX + x, targetY, centerZ + z, Material.AIR);
                    chunk.setBlock(centerX + x, targetY + 1, centerZ + z, Material.WHEAT); // Represents rice
                }
            }
        }

        // Raised walkways
        for (int x = -4; x <= 4; x += 4) {
            if (isValidPosition(centerX + x, centerZ)) {
                chunk.setBlock(centerX + x, targetY, centerZ, Material.DIRT_PATH);
            }
        }
    }

    private static void createKelpFarm(ChunkData chunk, int centerX, int centerZ, int surfaceY) {
        int targetY = findSafeY(chunk, centerX, centerZ, surfaceY);
        if (targetY <= 0)
            return;

        // Underwater kelp cultivation
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY - 2, centerZ + z, Material.WATER);
                    chunk.setBlock(centerX + x, targetY - 1, centerZ + z, Material.WATER);
                    chunk.setBlock(centerX + x, targetY, centerZ + z, Material.WATER);

                    // Plant kelp
                    if (random.nextInt(2) == 0) {
                        chunk.setBlock(centerX + x, targetY - 2, centerZ + z, Material.KELP_PLANT);
                        chunk.setBlock(centerX + x, targetY - 1, centerZ + z, Material.KELP_PLANT);
                        chunk.setBlock(centerX + x, targetY, centerZ + z, Material.KELP);
                    }
                }
            }
        }
    }

    // Utility methods

    private static void createFarmFencing(ChunkData chunk, int centerX, int centerZ, int targetY, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if ((Math.abs(x) == radius || Math.abs(z) == radius) && isValidPosition(centerX + x, centerZ + z)) {
                    chunk.setBlock(centerX + x, targetY + 1, centerZ + z, Material.OAK_FENCE);
                }
            }
        }

        // Gate
        if (isValidPosition(centerX, centerZ + radius)) {
            chunk.setBlock(centerX, targetY + 1, centerZ + radius, Material.OAK_FENCE_GATE);
        }
    }

    private static int findSafeY(ChunkData chunk, int x, int z, int startY) {
        if (!isValidPosition(x, z))
            return 0;

        // Find a suitable surface level near the suggested starting point
        for (int y = startY; y >= 1; y--) {
            Material material = chunk.getBlockData(x, y, z).getMaterial();
            if (material != Material.AIR && material != Material.WATER) {
                return y;
            }
        }
        return startY; // fallback
    }

    private static boolean isValidPosition(int x, int z) {
        return x >= 0 && x < 16 && z >= 0 && z < 16;
    }
}
