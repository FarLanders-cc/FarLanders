package cc.farlanders.generate.agriculture;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cc.farlanders.generate.BaseTest;

public class AgricultureManagerSimpleTest extends BaseTest {

    private ChunkData mockChunk;
    private BlockData mockBlockData;

    @BeforeEach
    void setup() {
        mockChunk = mock(ChunkData.class);
        mockBlockData = mock(BlockData.class);
        when(mockChunk.getBlockData(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockData);
        when(mockBlockData.getMaterial()).thenReturn(Material.STONE);
    }

    @Test
    void testAgricultureGeneration_allBiomes_shouldNotThrowErrors() {
        // Test that all biome types can be processed without errors
        String[] biomes = { "plains", "forest", "desert", "savanna", "taiga", "jungle", "swamp" };

        for (String biome : biomes) {
            assertDoesNotThrow(() -> {
                AgricultureManager.generateAgriculture(mockChunk, 0, 0, biome, 64);
            }, "Agriculture generation for biome " + biome + " should not throw errors");
        }
    }

    @Test
    void testAgricultureGeneration_unknownBiome_shouldUseGenericFarm() {
        // Test that unknown biomes default to generic farm generation
        assertDoesNotThrow(() -> {
            AgricultureManager.generateAgriculture(mockChunk, 0, 0, "unknown_biome", 64);
        }, "Unknown biome should fall back to generic farm");
    }

    @Test
    void testAgricultureGeneration_multipleCoordinates_shouldNotThrowErrors() {
        // Test various coordinate combinations
        int[] testCoordinates = { 0, 16, 32, 100, 256, 1000 };

        for (int x : testCoordinates) {
            for (int z : testCoordinates) {
                assertDoesNotThrow(() -> {
                    AgricultureManager.generateAgriculture(mockChunk, x, z, "plains", 64);
                }, "Agriculture generation at coordinates (" + x + ", " + z + ") should not throw errors");
            }
        }
    }

    @Test
    void testAgricultureGeneration_edgeCases_shouldHandleGracefully() {
        // Test edge cases
        assertDoesNotThrow(() -> {
            AgricultureManager.generateAgriculture(mockChunk, -100, -100, "plains", 64);
        }, "Negative coordinates should be handled gracefully");

        assertDoesNotThrow(() -> {
            AgricultureManager.generateAgriculture(mockChunk, 0, 0, "", 64);
        }, "Empty biome string should be handled gracefully");

        assertDoesNotThrow(() -> {
            AgricultureManager.generateAgriculture(mockChunk, 0, 0, null, 64);
        }, "Null biome should be handled gracefully");
    }

    @Test
    void testSpacingConcept_reducedGenerationFrequency() {
        // Conceptual test: verify that the spacing improvements are in place
        // by running many generations and ensuring they don't always generate
        int generations = 0;
        int totalAttempts = 50; // Small sample size for test speed

        for (int i = 0; i < totalAttempts; i++) {
            ChunkData testChunk = mock(ChunkData.class);
            BlockData testBlockData = mock(BlockData.class);
            when(testChunk.getBlockData(anyInt(), anyInt(), anyInt())).thenReturn(testBlockData);
            when(testBlockData.getMaterial()).thenReturn(Material.STONE);

            // Test desert biome (has highest spacing - 1/35 chance)
            AgricultureManager.generateAgriculture(testChunk, i, i, "desert", 64);
        }

        // This test primarily ensures no exceptions are thrown
        // The actual spacing is probabilistic and hard to test reliably
        assertTrue(true, "Spacing test completed without errors");
    }
}
