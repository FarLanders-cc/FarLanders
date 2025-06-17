package cc.farlanders.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ValuesTest {

    @Test
    void testValues() {
        // Test the values in the config
        assertTrue(Values.values().length > 0, "Values enum should have values defined");
    }

    @Test
    void testValueNames() {
        // Test the names of the values in the config
        for (Values value : Values.values()) {
            assertTrue(!value.name().isEmpty(), "Value name should not be empty");
        }
    }

    @Test
    void testValuesExistence() {
        // Test that all values are defined in the enum
        for (Values value : Values.values()) {
            assertNotNull(value, "Value should not be null: " + value.name());
        }
    }

    // @Test
    // void testValueTypes() {
    // // Test the types of the values in the config
    // for (Values value : Values.values()) {
    // if (value.doubleValue != null) {
    // assertTrue(value.doubleValue >= 0, "Double value should be non-negative");
    // }
    // if (value.floatValue != null) {
    // assertTrue(value.floatValue >= 0, "Float value should be non-negative");
    // }
    // if (value.intValue != null) {
    // assertTrue(value.intValue >= 0, "Integer value should be non-negative");
    // }
    // }
    // }

    @Test
    void testGetAllValues() {
        // Test the getAllValues method
        Values[] allValues = Values.getAllValues();
        assertTrue(allValues.length > 0, "getAllValues should return an array with values");
        for (Values value : allValues) {
            assertNotNull(value, "Value should not be null");
        }
    }
}
