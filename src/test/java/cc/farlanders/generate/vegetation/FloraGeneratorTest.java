package cc.farlanders.generate.vegetation;

import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class FloraGeneratorTest {

    private FloraGenerator floraGenerator;
    private ChunkData chunkData;

    @BeforeEach
    void setUp() {
        floraGenerator = new FloraGenerator();
        chunkData = mock(ChunkData.class);
    }

    @Test
    void testPlaceTree_Oak() {
        floraGenerator.placeTree(chunkData, 8, 64, 8, TreeType.TREE, 6);
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.OAK_LOG));
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.OAK_LEAVES));
    }

    @Test
    void testPlaceTree_Birch() {
        floraGenerator.placeTree(chunkData, 8, 64, 8, TreeType.BIRCH, 7);
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.BIRCH_LOG));
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.BIRCH_LEAVES));
    }

    @Test
    void testPlaceTree_Spruce() {
        floraGenerator.placeTree(chunkData, 8, 64, 8, TreeType.REDWOOD, 8);
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.SPRUCE_LOG));
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.SPRUCE_LEAVES));
    }

    @Test
    void testPlaceTree_Acacia() {
        floraGenerator.placeTree(chunkData, 8, 64, 8, TreeType.ACACIA, 5);
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.ACACIA_LOG));
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.ACACIA_LEAVES));
    }

    // @Test
    // void testPlaceTree_Cherry() {
    // floraGenerator.placeTree(chunkData, 8, 64, 8, TreeType.CHERRY, 6);
    // verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(),
    // eq(Material.CHERRY_LOG));
    // verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(),
    // eq(Material.CHERRY_LEAVES));
    // }

    @Test
    void testPlaceTree_Jungle() {
        floraGenerator.placeTree(chunkData, 8, 64, 8, TreeType.JUNGLE, 9);
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.JUNGLE_LOG));
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.JUNGLE_LEAVES));
    }

    @Test
    void testPlaceTree_Swamp() {
        floraGenerator.placeTree(chunkData, 8, 64, 8, TreeType.SWAMP, 5);
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.OAK_LOG));
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.OAK_LEAVES));
    }

    @Test
    void testPlaceTree_BrownMushroom() {
        floraGenerator.placeTree(chunkData, 8, 64, 8, TreeType.BROWN_MUSHROOM, 4);
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.MUSHROOM_STEM));
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.BROWN_MUSHROOM_BLOCK));
    }

    @Test
    void testPlaceTree_RedMushroom() {
        floraGenerator.placeTree(chunkData, 8, 64, 8, TreeType.RED_MUSHROOM, 4);
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.MUSHROOM_STEM));
        verify(chunkData, atLeastOnce()).setBlock(anyInt(), anyInt(), anyInt(), eq(Material.RED_MUSHROOM_BLOCK));
    }
}