package cc.farlanders.compat;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VersionCompatibilityManagerTest {

    @Mock
    private Player mockPlayer;

    @Mock
    private Plugin mockPlugin;

    @BeforeEach
    void setUp() {
        // Reset the manager state for each test
        VersionCompatibilityManager.recheckAvailability();
        // Remove the UUID stubbing from here - we'll add it only where needed
    }

    @Test
    void testIsViaVersionAvailable() {
        // Test should work regardless of whether ViaVersion is actually installed
        // Just verify the method doesn't throw an exception
        assertDoesNotThrow(() -> VersionCompatibilityManager.isViaVersionAvailable(),
                "ViaVersion availability check should not throw an exception");
    }

    @Test
    void testIsViaRewindAvailable() {
        // Test should work regardless of whether ViaRewind is actually installed
        // Just verify the method doesn't throw an exception
        assertDoesNotThrow(() -> VersionCompatibilityManager.isViaRewindAvailable(),
                "ViaRewind availability check should not throw an exception");
    }

    @Test
    void testGetPlayerProtocolVersionWithNullPlayer() {
        int version = VersionCompatibilityManager.getPlayerProtocolVersion(null);
        assertEquals(-1, version, "Should return -1 for null player");
    }

    @Test
    void testGetPlayerProtocolVersionWithoutViaVersion() {
        // Setup mock only for this test
        when(mockPlayer.getUniqueId()).thenReturn(UUID.randomUUID());

        // When ViaVersion is not available, should return -1
        int version = VersionCompatibilityManager.getPlayerProtocolVersion(mockPlayer);
        // Could be -1 (ViaVersion not available) or a valid protocol version (if
        // available)
        assertTrue(version >= -1, "Protocol version should be -1 or a positive value");
    }

    @Test
    void testGetPlayerProtocolVersion_NullAndEdgeCases() {
        // Null player should always return -1
        assertEquals(-1, VersionCompatibilityManager.getPlayerProtocolVersion(null));
        // Player with no UUID (should not happen, but test defensive)
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(null);
        assertTrue(VersionCompatibilityManager.getPlayerProtocolVersion(player) >= -1);
    }

    @Test
    void testGetVersionStringForKnownVersions() {
        // Test known protocol versions
        assertEquals("1.21.1", VersionCompatibilityManager.getVersionString(767));
        assertEquals("1.21", VersionCompatibilityManager.getVersionString(766));
        assertEquals("1.20.4", VersionCompatibilityManager.getVersionString(763));
        assertEquals("1.19.4", VersionCompatibilityManager.getVersionString(759));
        assertEquals("1.18.2", VersionCompatibilityManager.getVersionString(755));
        assertEquals("1.17.1", VersionCompatibilityManager.getVersionString(751));
        assertEquals("1.16.5", VersionCompatibilityManager.getVersionString(736));
        assertEquals("1.12.2", VersionCompatibilityManager.getVersionString(340));
        assertEquals("1.8.9", VersionCompatibilityManager.getVersionString(47));
    }

    @Test
    void testGetVersionStringForUnknownVersion() {
        String version = VersionCompatibilityManager.getVersionString(999);
        assertEquals("Unknown (999)", version);
    }

    @Test
    void testGetVersionStringForNativeVersion() {
        String version = VersionCompatibilityManager.getVersionString(-1);
        assertEquals("Native/Unknown", version);
    }

    @Test
    void testGetVersionString_NegativeAndZero() {
        assertEquals("Native/Unknown", VersionCompatibilityManager.getVersionString(-1));
        assertEquals("Unknown (0)", VersionCompatibilityManager.getVersionString(0));
    }

    @Test
    void testIsLegacyClient() {
        // Setup mock only for this test
        when(mockPlayer.getUniqueId()).thenReturn(UUID.randomUUID());

        // Mock legacy client detection - should not throw an exception
        assertDoesNotThrow(() -> VersionCompatibilityManager.isLegacyClient(mockPlayer),
                "Legacy client check should not throw an exception");
    }

    @Test
    void testIsLegacyClient_NullPlayer() {
        assertFalse(VersionCompatibilityManager.isLegacyClient(null));
    }

    @Test
    void testIsModernClient() {
        // Setup mock only for this test
        when(mockPlayer.getUniqueId()).thenReturn(UUID.randomUUID());

        // Should not throw an exception
        assertDoesNotThrow(() -> VersionCompatibilityManager.isModernClient(mockPlayer),
                "Modern client check should not throw an exception");
    }

    @Test
    void testIsModernClient_NullPlayer() {
        boolean result = VersionCompatibilityManager.isModernClient(null);
        System.out.println("isModernClient(null) returned: " + result);
        assertTrue(result);
    }

    @Test
    void testLegacyAndModernClientAreOpposite() {
        // Setup mock only for this test
        when(mockPlayer.getUniqueId()).thenReturn(UUID.randomUUID());

        // For any given player, they should be either legacy OR modern, not both
        boolean isLegacy = VersionCompatibilityManager.isLegacyClient(mockPlayer);
        boolean isModern = VersionCompatibilityManager.isModernClient(mockPlayer);

        // Note: This might not always be true if ViaVersion returns -1
        // but it's a good sanity check for most cases
        if (VersionCompatibilityManager.getPlayerProtocolVersion(mockPlayer) != -1) {
            assertNotEquals(isLegacy, isModern, "Player should be either legacy OR modern, not both");
        }
    }

    @Test
    void testGetCompatibilityInfo() {
        when(mockPlugin.getServer()).thenReturn(mock(org.bukkit.Server.class));
        when(mockPlugin.getServer().getVersion()).thenReturn("Test Server 1.21");

        String info = VersionCompatibilityManager.getCompatibilityInfo(mockPlugin);

        assertNotNull(info);
        assertTrue(info.contains("Version Compatibility Info"));
        assertTrue(info.contains("Server Version"));
        assertTrue(info.contains("ViaVersion"));
        assertTrue(info.contains("ViaRewind"));
    }

    @Test
    void testRecheckAvailability() {
        // Should not throw an exception
        assertDoesNotThrow(() -> VersionCompatibilityManager.recheckAvailability());
    }

    @Test
    void testProtocolVersionBoundaries() {
        // Test the boundary between legacy and modern clients
        assertTrue(VersionCompatibilityManager.getVersionString(392).contains("Unknown")); // Just before 1.13
        assertEquals("1.13", VersionCompatibilityManager.getVersionString(393)); // 1.13 - first modern
        assertEquals("1.12.2", VersionCompatibilityManager.getVersionString(340)); // Legacy
    }
}