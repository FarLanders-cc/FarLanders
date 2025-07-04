package cc.farlanders.compat;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Manages version compatibility features and ViaVersion integration.
 * ViaVersion is an optional dependency - this class gracefully handles
 * cases where ViaVersion is not installed.
 */
public class VersionCompatibilityManager {

    private static boolean viaVersionAvailable = false;
    private static boolean viaRewindAvailable = false;
    private static Object viaAPI = null;
    private static Method getPlayerVersionMethod = null;
    private static final Logger logger = Logger.getLogger("FarLanders");

    // Static initialization block to check for ViaVersion availability
    static {
        checkViaVersionAvailability();
    }

    /**
     * Check if ViaVersion and ViaRewind are available at runtime
     */
    private static void checkViaVersionAvailability() {
        try {
            // Check for ViaVersion API
            Class<?> viaClass = Class.forName("com.viaversion.viaversion.api.Via");
            Method getAPIMethod = viaClass.getMethod("getAPI");
            viaAPI = getAPIMethod.invoke(null);

            // Get the getPlayerVersion method
            getPlayerVersionMethod = viaAPI.getClass().getMethod("getPlayerVersion", java.util.UUID.class);

            viaVersionAvailable = true;
            logger.info("ViaVersion detected - multi-version support enabled");

        } catch (ClassNotFoundException e) {
            viaVersionAvailable = false;
            logger.info("ViaVersion not found - using native version support only");
        } catch (Exception e) {
            viaVersionAvailable = false;
            logger.warning("Failed to initialize ViaVersion API: " + e.getMessage());
        }

        try {
            // Check for ViaRewind
            Class.forName("com.viaversion.viarewind.ViaRewind");
            viaRewindAvailable = true;
            logger.info("ViaRewind detected - legacy client support enabled");
        } catch (ClassNotFoundException e) {
            viaRewindAvailable = false;
        }
    }

    /**
     * Check if ViaVersion is available on the server
     * 
     * @return true if ViaVersion is installed and functional
     */
    public static boolean isViaVersionAvailable() {
        return viaVersionAvailable;
    }

    /**
     * Check if ViaRewind is available on the server
     * 
     * @return true if ViaRewind is installed
     */
    public static boolean isViaRewindAvailable() {
        return viaRewindAvailable;
    }

    /**
     * Get the protocol version for a specific player
     * 
     * @param player The player to check
     * @return Protocol version number, or -1 if unavailable
     */
    public static int getPlayerProtocolVersion(Player player) {
        if (!viaVersionAvailable || viaAPI == null || getPlayerVersionMethod == null) {
            return -1; // ViaVersion not available
        }

        try {
            Object result = getPlayerVersionMethod.invoke(viaAPI, player.getUniqueId());
            return (Integer) result;
        } catch (Exception e) {
            logger.warning("Failed to get protocol version for player " + player.getName() + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Get a human-readable version string for a protocol version
     * 
     * @param protocolVersion The protocol version number
     * @return Version string (e.g., "1.20.4") or "Unknown"
     */
    public static String getVersionString(int protocolVersion) {
        if (protocolVersion == -1) {
            return "Native/Unknown";
        }

        // Protocol version mappings for major versions
        return switch (protocolVersion) {
            case 767 -> "1.21.1";
            case 766 -> "1.21";
            case 765 -> "1.20.6";
            case 764 -> "1.20.5";
            case 763 -> "1.20.4";
            case 762 -> "1.20.2";
            case 760 -> "1.20.1";
            case 759 -> "1.19.4";
            case 758 -> "1.19.3";
            case 757 -> "1.19.1";
            case 756 -> "1.19";
            case 755 -> "1.18.2";
            case 754 -> "1.18.1";
            case 753 -> "1.18";
            case 751 -> "1.17.1";
            case 750 -> "1.17";
            case 736 -> "1.16.5";
            case 735 -> "1.16.4";
            case 578 -> "1.15.2";
            case 498 -> "1.14.4";
            case 477 -> "1.14";
            case 404 -> "1.13.2";
            case 393 -> "1.13";
            case 340 -> "1.12.2";
            case 338 -> "1.12.1";
            case 335 -> "1.12";
            case 316 -> "1.11.2";
            case 315 -> "1.11";
            case 210 -> "1.10.2";
            case 47 -> "1.8.9";
            default -> "Unknown (" + protocolVersion + ")";
        };
    }

    /**
     * Check if a player is using a legacy client (pre-1.13)
     * 
     * @param player The player to check
     * @return true if using legacy client, false otherwise
     */
    public static boolean isLegacyClient(Player player) {
        int version = getPlayerProtocolVersion(player);
        return version > 0 && version < 393; // 393 is 1.13
    }

    /**
     * Check if a player is using a modern client (1.13+)
     * 
     * @param player The player to check
     * @return true if using modern client
     */
    public static boolean isModernClient(Player player) {
        int version = getPlayerProtocolVersion(player);
        return version == -1 || version >= 393; // -1 means native/unknown, assume modern
    }

    /**
     * Get version compatibility information for the server
     * 
     * @param plugin The plugin instance for getting plugin managers
     * @return Formatted compatibility info string
     */
    public static String getCompatibilityInfo(Plugin plugin) {
        StringBuilder info = new StringBuilder();
        info.append("§6=== Version Compatibility Info ===\n");
        info.append("§7Server Version: §e").append(plugin.getServer().getVersion()).append("\n");
        info.append("§7ViaVersion: ").append(viaVersionAvailable ? "§aInstalled" : "§cNot Found").append("\n");
        info.append("§7ViaRewind: ").append(viaRewindAvailable ? "§aInstalled" : "§cNot Found").append("\n");

        if (viaVersionAvailable) {
            info.append("§7Multi-version support: §aEnabled\n");
            info.append("§7Supported clients: §e1.8.x - 1.21.x\n");
        } else {
            info.append("§7Multi-version support: §cDisabled\n");
            info.append("§7Supported clients: §eNative server version only\n");
        }

        return info.toString();
    }

    /**
     * Force re-check ViaVersion availability (useful for plugin reloads)
     */
    public static void recheckAvailability() {
        viaVersionAvailable = false;
        viaRewindAvailable = false;
        viaAPI = null;
        getPlayerVersionMethod = null;
        checkViaVersionAvailability();
    }
}