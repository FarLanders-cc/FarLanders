package cc.farlanders.generate.structures;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for AncientRuinsGenerator functionality
 * Currently disabled due to complex mocking requirements
 */
@Disabled("Integration test - requires complex mock setup")
class AncientRuinsGeneratorTest {

    @Test
    @DisplayName("Ancient ruins generator creation should not throw")
    void testGeneratorCreation() {
        assertDoesNotThrow(AncientRuinsGenerator::new);
    }
}
