package cc.farlanders.generate.terrain;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cc.farlanders.generate.terrain.TerrainHandler.BlockContext;

class TerrainHandlerTest {

    private TerrainHandler terrainHandler;
    private ChunkData mockChunk;
    private BlockData mockBlockData;

    @BeforeEach
    void setup() {
        terrainHandler = new TerrainHandler();

        // Mock ChunkData and BlockData
        mockChunk = mock(ChunkData.class);
        mockBlockData = mock(BlockData.class);

        // When getBlockData is called, return the mocked BlockData
        when(mockChunk.getBlockData(anyInt(), anyInt(), anyInt())).thenReturn(mockBlockData);
        // Mock getMaterial() to return STONE for surface blocks (or whatever fits test)
        when(mockBlockData.getMaterial()).thenReturn(Material.STONE);
    }

    @Test
    void testHandleSurfaceOrUndergroundBlock_densityAboveThreshold_setsBlock() {
        // Arrange: Create a BlockContext with density > 0.3 to trigger block setting
        BlockContext context = new BlockContext(
                0, // chunkX
                0, // chunkZ
                100, // worldX
                70, // y (above sea level)
                100, // worldZ
                0.5, // density > 0.3
                "plains" // biome, ignored in this method
        );

        // Act
        terrainHandler.handleSurfaceOrUndergroundBlock(mockChunk, context);

        // Assert: verify chunk.setBlock called with correct params and a Material from
        // getOreOrStone()
        verify(mockChunk, times(1)).setBlock(eq(0), eq(70), eq(0), any(Material.class));
    }

    @Test
    void testHandleSurfaceOrUndergroundBlock_belowSeaLevel_setsWater() {
        // Arrange: density below threshold but y < SEA_LEVEL (64)
        BlockContext context = new BlockContext(
                0, 0, 100, 60, 100, 0.1, "plains");

        // Act
        terrainHandler.handleSurfaceOrUndergroundBlock(mockChunk, context);

        // Assert: verify water block set at position
        verify(mockChunk, times(1)).setBlock(eq(0), eq(60), eq(0), eq(Material.WATER));
    }

    @Test
    void testHandleSurfaceOrUndergroundBlock_lowDensityAboveSeaLevel_setsAir() {
        // Arrange: density below threshold, y >= SEA_LEVEL
        BlockContext context = new BlockContext(
                0, 0, 100, 70, 100, 0.1, "plains");

        // Act
        terrainHandler.handleSurfaceOrUndergroundBlock(mockChunk, context);

        // Assert: verify air block set at position
        verify(mockChunk, times(1)).setBlock(eq(0), eq(70), eq(0), eq(Material.AIR));
    }
}
