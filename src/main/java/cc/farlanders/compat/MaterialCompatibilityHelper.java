package cc.farlanders.compat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Helper class for handling material compatibility across different Minecraft
 * versions
 */
public final class MaterialCompatibilityHelper {

    // Legacy material mappings for pre-1.13 clients
    private static final Map<Material, Material> LEGACY_MATERIAL_MAP = new HashMap<>();

    static {
        // Initialize legacy material mappings
        initializeLegacyMappings();
    }

    private MaterialCompatibilityHelper() {
        // Utility class - prevent instantiation
    }

    /**
     * Initialize the legacy material mappings for pre-1.13 compatibility
     */
    private static void initializeLegacyMappings() {
        // Block mappings (1.13+ -> 1.12.2 and below)
        tryPutMapping("GRASS_BLOCK", "GRASS");
        tryPutMapping("DIRT_PATH", "GRASS_PATH");
        tryPutMapping("COBBLESTONE_STAIRS", "COBBLESTONE_STAIRS");
        tryPutMapping("OAK_PLANKS", "WOOD");
        tryPutMapping("OAK_LOG", "LOG");
        tryPutMapping("STRIPPED_OAK_LOG", "LOG");
        tryPutMapping("OAK_LEAVES", "LEAVES");
        tryPutMapping("SPRUCE_PLANKS", "WOOD");
        tryPutMapping("SPRUCE_LOG", "LOG");
        tryPutMapping("SPRUCE_LEAVES", "LEAVES");
        tryPutMapping("STONE_STAIRS", "COBBLESTONE_STAIRS");
        tryPutMapping("DEEPSLATE", "STONE");
        tryPutMapping("DEEPSLATE_COAL_ORE", "COAL_ORE");
        tryPutMapping("DEEPSLATE_IRON_ORE", "IRON_ORE");
        tryPutMapping("DEEPSLATE_GOLD_ORE", "GOLD_ORE");
        tryPutMapping("DEEPSLATE_DIAMOND_ORE", "DIAMOND_ORE");
        tryPutMapping("DEEPSLATE_REDSTONE_ORE", "REDSTONE_ORE");
        tryPutMapping("DEEPSLATE_COPPER_ORE", "IRON_ORE"); // Copper didn't exist pre-1.17
        tryPutMapping("BLACKSTONE", "COBBLESTONE");
        tryPutMapping("DRIPSTONE_BLOCK", "STONE");
        tryPutMapping("CALCITE", "QUARTZ_BLOCK");
        tryPutMapping("TUFF", "ANDESITE");
        tryPutMapping("AMETHYST_BLOCK", "PURPUR_BLOCK");
    }

    /**
     * Safely add a material mapping, handling cases where materials don't exist
     */
    private static void tryPutMapping(String modernName, String legacyName) {
        try {
            Material modernMaterial = Material.valueOf(modernName);
            Material legacyMaterial = Material.valueOf(legacyName);
            LEGACY_MATERIAL_MAP.put(modernMaterial, legacyMaterial);
        } catch (IllegalArgumentException e) {
            // Material doesn't exist in this version - ignore
        }
    }

    /**
     * Get the appropriate material for a player's client version
     * 
     * @param material The modern material to convert
     * @param player   The player (null = assume modern client)
     * @return Compatible material for the player's client
     */
    public static Material getCompatibleMaterial(Material material, Player player) {
        if (material == null) {
            return Material.STONE; // Safe fallback
        }

        // If no player context or ViaVersion not available, return original
        if (player == null || !VersionCompatibilityManager.isViaVersionAvailable()) {
            return material;
        }

        // Check if player is using legacy client
        if (VersionCompatibilityManager.isLegacyClient(player)) {
            return LEGACY_MATERIAL_MAP.getOrDefault(material, material);
        }

        return material;
    }

    /**
     * Get a safe fallback material that exists in all versions
     * 
     * @param preferredMaterial The preferred material
     * @param fallbackMaterial  The fallback if preferred doesn't exist
     * @return A valid material
     */
    public static Material getSafeMaterial(String preferredMaterial, Material fallbackMaterial) {
        try {
            return Material.valueOf(preferredMaterial);
        } catch (IllegalArgumentException e) {
            return fallbackMaterial != null ? fallbackMaterial : Material.STONE;
        }
    }

    /**
     * Check if a material exists in the current server version
     * 
     * @param materialName The material name to check
     * @return true if the material exists
     */
    public static boolean materialExists(String materialName) {
        try {
            Material.valueOf(materialName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Get legacy-safe ore materials
     * 
     * @param oreName     The ore type (e.g., "DIAMOND", "IRON")
     * @param isDeepslate Whether this is a deepslate variant
     * @param player      The player context (null = assume modern)
     * @return Compatible ore material
     */
    public static Material getLegacySafeOre(String oreName, boolean isDeepslate, Player player) {
        // If a player is provided but ViaVersion isn't available, we can't
        // reliably detect their client; prefer the non-deepslate ore as a
        // safe fallback to keep behaviour deterministic in tests and on
        // servers without ViaVersion.
        if (player != null) {
            if (!VersionCompatibilityManager.isViaVersionAvailable()) {
                return getSafeMaterial(oreName + "_ORE", Material.STONE);
            }

            if (VersionCompatibilityManager.isLegacyClient(player)) {
                // Legacy clients don't have deepslate ores
                return getSafeMaterial(oreName + "_ORE", Material.STONE);
            }
        }

        if (isDeepslate) {
            return getSafeMaterial("DEEPSLATE_" + oreName + "_ORE",
                    getSafeMaterial(oreName + "_ORE", Material.STONE));
        }

        return getSafeMaterial(oreName + "_ORE", Material.STONE);
    }

    /**
     * Get compatible material for terrain generation (no player context needed)
     * 
     * @param materialName The material name to check
     * @return The compatible material or null if not available
     */
    public static Material getCompatibleMaterial(String materialName) {
        try {
            return Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Get compatible material with version checking for terrain generation
     * 
     * @param material The material to check
     * @return The compatible material or null if not available
     */
    public static Material getCompatibleMaterial(Material material) {
        // Add any version-specific logic here if needed
        return material;
    }

    // ...existing code...
}