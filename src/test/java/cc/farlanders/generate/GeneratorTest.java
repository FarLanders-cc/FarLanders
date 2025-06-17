package cc.farlanders.generate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class GeneratorTest {

    @Test
    void testFarLandsTerrainHeight() {
        FarLandsGenerator generate = new FarLandsGenerator(1337L);
        int height = generate.getHeightAt(10000, 10000);
        assertTrue(height >= 0 && height <= 255, "Height should be within valid range (0-255), but was: " + height);
    }

    @Test
    void testFarLandsTerrainHeightNegative() {
        FarLandsGenerator generate = new FarLandsGenerator(1337L);
        int height = generate.getHeightAt(-10000, -10000);
        assertTrue(height >= 0 && height <= 255, "Height should be within valid range (0-255), but was: " + height);
    }
}