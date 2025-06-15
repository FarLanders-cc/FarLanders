package cc.farlanders.generate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class TerrainGenerateTest {

    @Test
    void testNormalTerrainHeight() {
        TerrainGenerate generate = new TerrainGenerate(1337L, 64, 12550820);
        int height = generate.getHeightAt(0, 0);
        assertTrue(height >= 0 && height <= 255, "Height should be within valid range (0-255), but was: " + height);
    }
}