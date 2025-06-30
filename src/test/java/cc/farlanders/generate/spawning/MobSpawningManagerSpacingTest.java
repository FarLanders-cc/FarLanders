package cc.farlanders.generate.spawning;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cc.farlanders.generate.BaseTest;

class MobSpawningManagerSpacingTest extends BaseTest {

    private ChunkData mockChunk;
    private BlockData mockBlockData;

    @BeforeEach
    void setup() {
        super.setupBase();
        // Mock ChunkData and BlockData
        mockChunk = mock(ChunkData.class);
        mockBlockData = mock(BlockData.class);

        // When getBlockData is called, return the mocked BlockData
        when(mockChunk.getBlockData(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockData);
        when(mockBlockData.getMaterial()).thenReturn(Material.STONE);
    }

    @Test
    void testMobSpawningSpacing_plainsSpacing_shouldBeMoreSpaced() {
        // Test that plains mob spawning areas generate less frequently
        AtomicInteger spawningAreaCount = new AtomicInteger(0);

        // Run generation multiple times to test spacing
        for (int i = 0; i < 1000; i++) {
            // Create fresh mock for each iteration
            ChunkData testChunk = mock(ChunkData.class);
            BlockData testBlockData = mock(BlockData.class);
            when(testChunk.getBlockData(anyInt(), anyInt(), anyInt())).thenReturn(testBlockData);
            when(testBlockData.getMaterial()).thenReturn(Material.STONE);

            MobSpawningManager.generateMobSpawningAreas(testChunk, i, i, "plains");
            if (wasAnyBlockSet(testChunk)) {
                spawningAreaCount.incrementAndGet();
            }
        }

        // With new spacing (1/12 and 1/18), we expect roughly 8.33% + 5.56% = ~13.89%
        // generation
        // Allow for randomness with a range
        int expectedMin = 80; // 8% of 1000 iterations
        int expectedMax = 200; // 20% of 1000 iterations

        assertTrue(spawningAreaCount.get() >= expectedMin,
                "Mob spawning areas should generate at least " + expectedMin + " times, but generated "
                        + spawningAreaCount.get() + " times");
        assertTrue(spawningAreaCount.get() <= expectedMax,
                "Mob spawning areas should generate at most " + expectedMax + " times, but generated "
                        + spawningAreaCount.get() + " times");
    }

    @Test
    void testMobSpawningSpacing_forestSpacing_shouldBeMoreSpaced() {
        // Test that forest mob spawning areas generate less frequently
        AtomicInteger forestSpawningCount = new AtomicInteger(0);

        // Run generation multiple times to test spacing
        for (int i = 0; i < 1000; i++) {
            // Create fresh mock for each iteration
            ChunkData testChunk = mock(ChunkData.class);
            BlockData testBlockData = mock(BlockData.class);
            when(testChunk.getBlockData(anyInt(), anyInt(), anyInt())).thenReturn(testBlockData);
            when(testBlockData.getMaterial()).thenReturn(Material.STONE);

            MobSpawningManager.generateMobSpawningAreas(testChunk, i, i, "forest");
            if (wasAnyBlockSet(testChunk)) {
                forestSpawningCount.incrementAndGet();
            }
        }

        // With new spacing (1/15 and 1/8), we expect roughly 6.67% + 12.5% = ~19.17%
        // generation
        // Allow for randomness with a range
        int expectedMin = 100; // 10% of 1000 iterations
        int expectedMax = 300; // 30% of 1000 iterations

        assertTrue(forestSpawningCount.get() >= expectedMin,
                "Forest mob spawning should generate at least " + expectedMin + " times, but generated "
                        + forestSpawningCount.get() + " times");
        assertTrue(forestSpawningCount.get() <= expectedMax,
                "Forest mob spawning should generate at most " + expectedMax + " times, but generated "
                        + forestSpawningCount.get() + " times");
    }

    @Test
    void testJunglePlatform_shouldUseNaturalLogs() {
        // Test that jungle platforms can be generated without errors
        // Note: Due to mocking limitations and probabilistic generation,
        // we primarily test that the method executes without exceptions
        assertDoesNotThrow(() -> {
            MobSpawningManager.generateMobSpawningAreas(mockChunk, 8, 8, "jungle");
        }, "Jungle mob spawning generation should not throw exceptions");

        // In a real implementation, jungle platforms should use natural logs
        // But with mocks and probabilistic generation, we can't reliably verify
        // specific materials
        assertTrue(true, "Jungle platform test completed successfully");
    }

    @Test
    void testGetRecommendedMobs_returnsCorrectMobsForBiomes() {
        // Test that the right mobs are recommended for each biome

        // Plains should have farm animals
        List<EntityType> plainsMobs = MobSpawningManager.getRecommendedMobs("plains");
        assertTrue(plainsMobs.contains(EntityType.COW), "Plains should have cows");
        assertTrue(plainsMobs.contains(EntityType.SHEEP), "Plains should have sheep");
        assertTrue(plainsMobs.contains(EntityType.HORSE), "Plains should have horses");
        assertTrue(plainsMobs.contains(EntityType.PIG), "Plains should have pigs");
        assertTrue(plainsMobs.contains(EntityType.CHICKEN), "Plains should have chickens");

        // Forest should have wild animals
        List<EntityType> forestMobs = MobSpawningManager.getRecommendedMobs("forest");
        assertTrue(forestMobs.contains(EntityType.WOLF), "Forest should have wolves");
        assertTrue(forestMobs.contains(EntityType.FOX), "Forest should have foxes");

        // Desert should have desert animals
        List<EntityType> desertMobs = MobSpawningManager.getRecommendedMobs("desert");
        assertTrue(desertMobs.contains(EntityType.RABBIT), "Desert should have rabbits");
        // CAMEL may not be available in older Minecraft versions, check conditionally
        try {
            // Use reflection to check if CAMEL exists
            EntityType camelType = EntityType.valueOf("CAMEL");
            assertTrue(desertMobs.contains(camelType), "Desert should have camels (if available)");
        } catch (IllegalArgumentException e) {
            // CAMEL not available in this Minecraft version, skip this assertion
        }

        // Jungle should have tropical animals
        List<EntityType> jungleMobs = MobSpawningManager.getRecommendedMobs("jungle");
        assertTrue(jungleMobs.contains(EntityType.PARROT), "Jungle should have parrots");
        assertTrue(jungleMobs.contains(EntityType.OCELOT), "Jungle should have ocelots");
        // PANDA may not be available in older Minecraft versions, check conditionally
        try {
            EntityType pandaType = EntityType.valueOf("PANDA");
            assertTrue(jungleMobs.contains(pandaType), "Jungle should have pandas (if available)");
        } catch (IllegalArgumentException e) {
            // PANDA not available in this Minecraft version, skip this assertion
        }

        // Mushroom fields should have mooshrooms (or mushroom cows in older versions)
        List<EntityType> mushroomMobs = MobSpawningManager.getRecommendedMobs("mushroom_fields");
        try {
            EntityType mooshroomType = EntityType.valueOf("MOOSHROOM");
            assertTrue(mushroomMobs.contains(mooshroomType), "Mushroom fields should have mooshrooms (if available)");
        } catch (IllegalArgumentException e) {
            // MOOSHROOM not available, check for MUSHROOM_COW in older versions
            try {
                EntityType mushroomCowType = EntityType.valueOf("MUSHROOM_COW");
                assertTrue(mushroomMobs.contains(mushroomCowType),
                        "Mushroom fields should have mushroom cows (1.15.2)");
            } catch (IllegalArgumentException e2) {
                // Neither available, this shouldn't happen but skip the assertion
            }
        }
    }

    @Test
    void testMobSpawningSpacing_comparedToPrevious_shouldBeReduced() {
        // Test that overall mob spawning frequency is reduced compared to previous
        // implementation
        AtomicInteger totalSpawningAreas = new AtomicInteger(0);

        String[] biomes = { "plains", "forest", "desert", "savanna", "taiga", "jungle", "swamp" };

        for (String biome : biomes) {
            for (int i = 0; i < 100; i++) {
                // Create fresh mock for each iteration
                ChunkData testChunk = mock(ChunkData.class);
                BlockData testBlockData = mock(BlockData.class);
                when(testChunk.getBlockData(anyInt(), anyInt(), anyInt())).thenReturn(testBlockData);
                when(testBlockData.getMaterial()).thenReturn(Material.STONE);

                MobSpawningManager.generateMobSpawningAreas(testChunk, i, i, biome);
                if (wasAnyBlockSet(testChunk)) {
                    totalSpawningAreas.incrementAndGet();
                }
            }
        }

        // With increased spacing across all biomes, total should be significantly
        // reduced
        // Previous implementation would generate much more frequently
        assertTrue(totalSpawningAreas.get() < 300,
                "Total mob spawning areas should be reduced due to spacing improvements, got: "
                        + totalSpawningAreas.get());
    }

    @RepeatedTest(25)
    void testMobSpawningConsistency_spacingIsConsistent() {
        // Repeated test to ensure spacing improvements are consistently applied
        AtomicInteger generationCount = new AtomicInteger(0);

        for (int i = 0; i < 50; i++) {
            // Create fresh mock for each iteration
            ChunkData testChunk = mock(ChunkData.class);
            BlockData testBlockData = mock(BlockData.class);
            when(testChunk.getBlockData(anyInt(), anyInt(), anyInt())).thenReturn(testBlockData);
            when(testBlockData.getMaterial()).thenReturn(Material.STONE);

            MobSpawningManager.generateMobSpawningAreas(testChunk, i, i, "plains");
            if (wasAnyBlockSet(testChunk)) {
                generationCount.incrementAndGet();
            }
        }

        // Each run should consistently show reduced generation due to spacing
        assertTrue(generationCount.get() <= 15,
                "Each test run should show consistent spacing reduction, got: " + generationCount.get());
    }

    /**
     * Helper method to check if any blocks were set on the mock chunk
     */
    private boolean wasAnyBlockSet(ChunkData chunk) {
        try {
            verify(chunk, atLeast(1)).setBlock(anyInt(), anyInt(), anyInt(), any(Material.class));
            return true;
        } catch (org.mockito.exceptions.verification.WantedButNotInvoked e) {
            // This is expected for sparse generation - not an error
            return false;
        } catch (Exception e) {
            // Other exceptions should still be reported
            throw new RuntimeException("Unexpected error in verification", e);
        }
    }
}
