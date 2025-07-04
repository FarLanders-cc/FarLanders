package cc.farlanders.compat;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MaterialCompatibilityHelperTest {
    @Mock
    Player mockPlayer;

    @Test
    void testGetCompatibleMaterial_NullMaterial() {
        assertEquals(Material.STONE, MaterialCompatibilityHelper.getCompatibleMaterial(null, null));
    }

    @Test
    void testGetCompatibleMaterial_NoPlayer() {
        assertEquals(Material.DIAMOND_ORE,
                MaterialCompatibilityHelper.getCompatibleMaterial(Material.DIAMOND_ORE, null));
    }

    @Test
    void testGetCompatibleMaterial_LegacyClient() {
        // Cannot mock static methods without special runner; only test mapping logic
        Material deepslateDiamond = null;
        try {
            deepslateDiamond = Material.valueOf("DEEPSLATE_DIAMOND_ORE");
        } catch (IllegalArgumentException ignored) {
            // Intentionally ignored: material may not exist in this version
        }
        if (deepslateDiamond != null) {
            // This test only verifies that the mapping exists in the helper
            // Actual legacy client logic is tested in integration
            Material mapped = MaterialCompatibilityHelper.getCompatibleMaterial(deepslateDiamond, null);
            // Should be either deepslateDiamond or diamond_ore depending on mapping
            assertTrue(mapped == deepslateDiamond || mapped == Material.DIAMOND_ORE);
        }
    }

    @Test
    void testGetSafeMaterial_ValidAndInvalid() {
        assertEquals(Material.DIAMOND_ORE, MaterialCompatibilityHelper.getSafeMaterial("DIAMOND_ORE", Material.STONE));
        assertEquals(Material.STONE, MaterialCompatibilityHelper.getSafeMaterial("NOT_A_MATERIAL", Material.STONE));
        assertEquals(Material.STONE, MaterialCompatibilityHelper.getSafeMaterial("NOT_A_MATERIAL", null));
    }

    @Test
    void testMaterialExists() {
        assertTrue(MaterialCompatibilityHelper.materialExists("STONE"));
        assertFalse(MaterialCompatibilityHelper.materialExists("NOT_A_MATERIAL"));
    }

    @Test
    void testGetLegacySafeOre_LegacyAndModern() {
        // Cannot mock static methods; only test fallback logic
        assertEquals(Material.DIAMOND_ORE, MaterialCompatibilityHelper.getLegacySafeOre("DIAMOND", true, mockPlayer));
        assertEquals(Material.DIAMOND_ORE, MaterialCompatibilityHelper.getLegacySafeOre("DIAMOND", false, mockPlayer));
        Material deepslateDiamond = null;
        try {
            deepslateDiamond = Material.valueOf("DEEPSLATE_DIAMOND_ORE");
        } catch (IllegalArgumentException ignored) {
            // Intentionally ignored: material may not exist in this version
        }
        if (deepslateDiamond != null) {
            assertEquals(deepslateDiamond, MaterialCompatibilityHelper.getLegacySafeOre("DIAMOND", true, null));
        } else {
            assertEquals(Material.DIAMOND_ORE, MaterialCompatibilityHelper.getLegacySafeOre("DIAMOND", true, null));
        }
        assertEquals(Material.DIAMOND_ORE, MaterialCompatibilityHelper.getLegacySafeOre("DIAMOND", false, null));
    }

    @Test
    void testGetCompatibleMaterial_StringAndMaterial() {
        assertEquals(Material.DIAMOND_ORE, MaterialCompatibilityHelper.getCompatibleMaterial("DIAMOND_ORE"));
        assertNull(MaterialCompatibilityHelper.getCompatibleMaterial("NOT_A_MATERIAL"));
        assertEquals(Material.DIAMOND_ORE, MaterialCompatibilityHelper.getCompatibleMaterial(Material.DIAMOND_ORE));
    }

    @Test
    void testGetCompatibleMaterial_UnknownMaterial() {
        // Should return null for unknown string
        assertNull(MaterialCompatibilityHelper.getCompatibleMaterial("TOTALLY_FAKE_BLOCK"));
    }

    @Test
    void testGetSafeMaterial_NullFallback() {
        assertEquals(Material.STONE, MaterialCompatibilityHelper.getSafeMaterial("NOT_A_MATERIAL", null));
    }

    @Test
    void testMaterialExists_CaseSensitivity() {
        assertFalse(MaterialCompatibilityHelper.materialExists("stone")); // Should be case-sensitive
    }
}
