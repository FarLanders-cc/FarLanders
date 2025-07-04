package cc.farlanders.generate.terrain;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator.ChunkData;

import cc.farlanders.compat.MaterialCompatibilityHelper;
import cc.farlanders.generate.config.GenerationConfig;
import cc.farlanders.noise.OpenSimplex2;

public class TerrainHandler {

    private static final String DESERT = "desert";
    private static final String BADLANDS = "badlands";
    private static final String MUSHROOM_FIELDS = "mushroom_fields";
    private static final String SNOWY_PLAINS = "snowy_plains";

    /**
     * Safely gets a material if it exists in the current Minecraft version,
     * otherwise returns a fallback material. Enhanced with ViaVersion
     * compatibility.
     */
    private static Material getMaterialOrFallback(String materialName, Material fallback) {
        return MaterialCompatibilityHelper.getCompatibleMaterial(materialName) != null
                ? MaterialCompatibilityHelper.getCompatibleMaterial(materialName)
                : fallback;
    }

    public static class BlockContext {
        public final int cx;
        public final int cz;
        public final int worldX;
        public final int y;
        public final int worldZ;
        public final double density;
        public final String biome;
        public final double chaosIntensity;
        public final boolean inTunnel;
        public final boolean hasOvergrowth;

        public BlockContext(int cx, int cz, int worldX, int y, int worldZ, double density, String biome) {
            this.cx = cx;
            this.cz = cz;
            this.worldX = worldX;
            this.y = y;
            this.worldZ = worldZ;
            this.density = density;
            this.biome = biome;

            // Calculate chaotic properties
            this.chaosIntensity = calculateChaosIntensity(worldX, y, worldZ);
            this.inTunnel = isInTunnelSystem(worldX, y, worldZ);
            this.hasOvergrowth = shouldHaveOvergrowth(worldX, y, worldZ);
        }

        private double calculateChaosIntensity(int x, int y, int z) {
            // Create tornado-like spiral distortion
            double distance = Math.sqrt(x * x + z * z);
            double spiralEffect = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getTornadoSeed(),
                    x * GenerationConfig.getDistortionSpiralScale(),
                    y * GenerationConfig.getDistortionYScale(),
                    z * GenerationConfig.getDistortionSpiralScale()) * GenerationConfig.getTornadoSpiralFactor();

            // Add chaotic distortion that gets stronger with distance
            double chaos = OpenSimplex2.noise3_Fallback(GenerationConfig.getDistortionSeed(),
                    (x + spiralEffect * distance) * GenerationConfig.getDistortionCoordScale(),
                    y * GenerationConfig.getDistortionYScale(),
                    (z + spiralEffect * distance) * GenerationConfig.getDistortionCoordScale());

            return Math.abs(chaos) * GenerationConfig.getChaosIntensity();
        }

        private boolean isInTunnelSystem(int x, int y, int z) {
            // Create interconnected tunnel networks
            double tunnelNoise1 = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getTunnelSeed(),
                    x * GenerationConfig.getTunnelScalePrimary(),
                    y * (GenerationConfig.getTunnelScalePrimary() * 0.75),
                    z * GenerationConfig.getTunnelScalePrimary());
            double tunnelNoise2 = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getTunnelSeed() + 1000,
                    x * GenerationConfig.getTunnelScaleSecondary(),
                    y * (GenerationConfig.getTunnelScaleSecondary() * 0.71),
                    z * GenerationConfig.getTunnelScaleSecondary());
            double tunnelNoise3 = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getTunnelSeed() + 2000,
                    x * GenerationConfig.getTunnelScaleTertiary(),
                    y * (GenerationConfig.getTunnelScaleTertiary() * 1.5),
                    z * GenerationConfig.getTunnelScaleTertiary());

            // Combine tunnel noises for complex tunnel networks
            double combinedTunnel = (tunnelNoise1 + tunnelNoise2 * 0.7 + tunnelNoise3 * 1.2) / 2.9;

            return Math.abs(combinedTunnel) < GenerationConfig.getTunnelThreshold();
        }

        private boolean shouldHaveOvergrowth(int x, int y, int z) {
            double overgrowthNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOvergrowthSeed(),
                    x * GenerationConfig.getOvergrowthScale(),
                    y * GenerationConfig.getOvergrowthYScale(),
                    z * GenerationConfig.getOvergrowthScale());
            return overgrowthNoise > GenerationConfig.getOvergrowthDensityThreshold();
        }
    }

    public void handleSkyIslandBlock(ChunkData chunk, BlockContext context) {
        // Sky islands are affected by chaos but maintain their floating nature
        if (context.inTunnel) {
            // Even sky islands can have tunnels through them
            chunk.setBlock(context.cx, context.y, context.cz, Material.AIR);
            return;
        }

        if (context.density > 0.5 - (context.chaosIntensity * 0.2)) {
            Material mat = getChaoticSkyIslandMaterial(context.worldX, context.y, context.worldZ, context.biome,
                    context);
            chunk.setBlock(context.cx, context.y, context.cz, mat);
        } else {
            chunk.setBlock(context.cx, context.y, context.cz, Material.AIR);
        }
    }

    public void handleSurfaceOrUndergroundBlock(ChunkData chunk, BlockContext context) {
        // Handle tunnel systems first
        if (context.inTunnel) {
            handleTunnelBlock(chunk, context);
            return;
        }

        // Apply chaotic distortion to density threshold
        double chaoticThreshold = 0.3 + (context.chaosIntensity * 0.3);

        if (context.density > chaoticThreshold && isSolidEnvironment(context)) {
            Material blockType = getChaoticSurfaceMaterial(context.worldX, context.y, context.worldZ, context.biome,
                    context);
            chunk.setBlock(context.cx, context.y, context.cz, blockType);
        } else if (context.y < GenerationConfig.getSeaLevel()) {
            chunk.setBlock(context.cx, context.y, context.cz, Material.WATER);
        } else {
            chunk.setBlock(context.cx, context.y, context.cz, Material.AIR);
        }
    }

    public void handleSolidBlock(ChunkData chunk, BlockContext context) {
        // Even solid blocks can be affected by tunnels
        if (context.inTunnel) {
            handleTunnelBlock(chunk, context);
            return;
        }

        Material blockType = getChaoticSurfaceMaterial(context.worldX, context.y, context.worldZ, context.biome,
                context);
        chunk.setBlock(context.cx, context.y, context.cz, blockType);
    }

    public void handleAirBlock(ChunkData chunk, BlockContext context) {
        // Add overgrowth in air spaces near solid blocks
        if (context.hasOvergrowth && isNearSolid(chunk, context.cx, context.y, context.cz)) {
            Material overgrowthMaterial = getOvergrowthMaterial(context.worldX, context.y, context.worldZ,
                    context.biome);
            chunk.setBlock(context.cx, context.y, context.cz, overgrowthMaterial);
        } else {
            chunk.setBlock(context.cx, context.y, context.cz, Material.AIR);
        }
    }

    private void handleTunnelBlock(ChunkData chunk, BlockContext context) {
        // Tunnels can have different contents based on depth and chaos
        if (context.y < GenerationConfig.getTunnelDeepFluidThreshold()) {
            // Deep tunnels might have lava or water
            double fluidNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getTunnelSeed() + 5000,
                    context.worldX * 0.05, context.y * 0.03, context.worldZ * 0.05);
            if (fluidNoise > GenerationConfig.getTunnelLavaChance()) {
                chunk.setBlock(context.cx, context.y, context.cz, Material.LAVA);
            } else if (fluidNoise > GenerationConfig.getTunnelWaterChance()) {
                chunk.setBlock(context.cx, context.y, context.cz, Material.WATER);
            } else {
                chunk.setBlock(context.cx, context.y, context.cz, Material.AIR);
            }
        } else if (context.hasOvergrowth) {
            // Surface tunnels might have overgrowth
            Material overgrowthMaterial = getOvergrowthMaterial(context.worldX, context.y, context.worldZ,
                    context.biome);
            chunk.setBlock(context.cx, context.y, context.cz, overgrowthMaterial);
        } else {
            chunk.setBlock(context.cx, context.y, context.cz, Material.AIR);
        }
    }

    private boolean isNearSolid(ChunkData chunk, int cx, int y, int cz) {
        // Check if there's a solid block nearby (within chunk bounds)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    int checkX = cx + dx;
                    int checkY = y + dy;
                    int checkZ = cz + dz;

                    if (checkX >= 0 && checkX < 16 && checkY >= 0 && checkY < 320 &&
                            checkZ >= 0 && checkZ < 16) {
                        if (!chunk.getBlockData(checkX, checkY, checkZ).getMaterial().isAir()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Material getOvergrowthMaterial(int x, int y, int z, String biome) {
        double overgrowthType = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOvergrowthSeed() + 1000,
                x * GenerationConfig.getOvergrowthTypeScale(),
                y * (GenerationConfig.getOvergrowthTypeScale() * 0.5),
                z * GenerationConfig.getOvergrowthTypeScale());

        // Biome-specific overgrowth
        return switch (biome.toLowerCase()) {
            case "jungle", "bamboo_jungle" -> {
                if (overgrowthType > 0.6)
                    yield getMaterialOrFallback("JUNGLE_LEAVES", Material.OAK_LEAVES);
                if (overgrowthType > 0.3)
                    yield getMaterialOrFallback("VINE", Material.OAK_LEAVES);
                yield getMaterialOrFallback("MOSS_BLOCK", Material.GRASS_BLOCK);
            }
            case "forest", "dark_forest" -> {
                if (overgrowthType > 0.5)
                    yield Material.OAK_LEAVES;
                if (overgrowthType > 0.2)
                    yield getMaterialOrFallback("AZALEA_LEAVES", Material.OAK_LEAVES);
                yield getMaterialOrFallback("MOSS_CARPET", Material.FERN);
            }
            case "swamp", "mangrove_swamp" -> {
                if (overgrowthType > 0.4)
                    yield getMaterialOrFallback("MANGROVE_LEAVES", Material.OAK_LEAVES);
                if (overgrowthType > 0.1)
                    yield getMaterialOrFallback("HANGING_ROOTS", Material.VINE);
                yield getMaterialOrFallback("MUD", Material.DIRT);
            }
            case MUSHROOM_FIELDS -> {
                if (overgrowthType > 0.5)
                    yield Material.RED_MUSHROOM_BLOCK;
                if (overgrowthType > 0.2)
                    yield Material.BROWN_MUSHROOM_BLOCK;
                yield Material.MYCELIUM;
            }
            default -> {
                if (overgrowthType > 0.6)
                    yield Material.FERN;
                if (overgrowthType > 0.3)
                    yield getMaterialOrFallback("FLOWERING_AZALEA_LEAVES", Material.OAK_LEAVES);
                yield getMaterialOrFallback("MOSS_BLOCK", Material.GRASS_BLOCK);
            }
        };
    }

    private Material getChaoticSkyIslandMaterial(int x, int y, int z, String biome, BlockContext context) {
        // Sky islands have chaos-affected rare materials
        double noise = noise3D(x, y, z);
        double chaosBonus = context.chaosIntensity * 0.3;
        double rareOreNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 1000, x * 0.03, y * 0.03,
                z * 0.03) + chaosBonus;

        // Chaos increases rare ore chances significantly
        if (rareOreNoise > 0.85 - chaosBonus) {
            return getMaterialOrFallback("NETHERITE_BLOCK", Material.DIAMOND_BLOCK);
        }
        if (rareOreNoise > 0.8 - chaosBonus) {
            return getMaterialOrFallback("ANCIENT_DEBRIS", Material.DIAMOND_ORE);
        }
        if (rareOreNoise > 0.75 - chaosBonus) {
            return Material.DIAMOND_ORE;
        }
        if (rareOreNoise > 0.7 - chaosBonus) {
            return Material.EMERALD_ORE;
        }
        if (rareOreNoise > 0.65 - chaosBonus) {
            return Material.LAPIS_ORE;
        }
        if (rareOreNoise > 0.6 - chaosBonus) {
            return Material.GOLD_ORE;
        }

        // Chaotic sky island base materials
        if (y > 260) {
            if (noise > 0.6)
                return getMaterialOrFallback("CRYING_OBSIDIAN", Material.OBSIDIAN);
            if (noise > 0.4)
                return getMaterialOrFallback("WARPED_NYLIUM", Material.END_STONE);
            return getMaterialOrFallback("DRIPSTONE_BLOCK", Material.COBBLESTONE);
        } else if (y > 240) {
            if (noise > 0.5)
                return getMaterialOrFallback("TWISTED_DEEPSLATE", Material.DEEPSLATE);
            return getMaterialOrFallback("TUFF", Material.STONE);
        } else {
            if (noise > 0.7)
                return getMaterialOrFallback("CRYING_OBSIDIAN", Material.OBSIDIAN);
            return getMaterialOrFallback("BLACKSTONE", Material.COBBLESTONE);
        }
    }

    private Material getChaoticSurfaceMaterial(int x, int y, int z, String biome, BlockContext context) {
        // Apply chaos to material selection
        double chaosOffset = context.chaosIntensity * 0.5;
        double adjustedY = y + (chaosOffset * GenerationConfig.getDistortionYOffsetMultiplier()); // Chaos can shift
                                                                                                  // material layers

        if (adjustedY < 5)
            return Material.BEDROCK;
        if (adjustedY < 15)
            return getChaoticDeepMaterial(x, (int) adjustedY, z, context);
        if (adjustedY < 40)
            return getChaoticOreOrStone(x, (int) adjustedY, z, context);
        if (adjustedY < 60)
            return getChaoticTransitionMaterial(x, (int) adjustedY, z, biome, context);

        // Surface materials with chaos
        if (y == getSurfaceLevel(x, z, biome)) {
            return getChaoticBiomeTopBlock(biome, context);
        }
        if (y < getSurfaceLevel(x, z, biome) + 3) {
            return getChaoticBiomeSubsurfaceBlock(biome, context);
        }

        return getChaoticStoneVariant(x, y, z, biome, context);
    }

    private Material getChaoticDeepMaterial(int x, int y, int z, BlockContext context) {
        double noise = noise3D(x, y, z);
        double chaosBonus = context.chaosIntensity * GenerationConfig.getChaosDeepOreBonus() * 0.2; // Reduced chaos ore
                                                                                                    // bonus

        // Use separate noise for each ore type to prevent clustering
        double diamondNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 1001, x * 0.08, y * 0.08,
                z * 0.08);
        double goldNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 2002, x * 0.09, y * 0.09,
                z * 0.09);
        double ironNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 3003, x * 0.1, y * 0.1,
                z * 0.1);
        double copperNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 4004, x * 0.11, y * 0.11,
                z * 0.11);
        double coalNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 5005, x * 0.12, y * 0.12,
                z * 0.12);
        double redstoneNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 6006, x * 0.13, y * 0.13,
                z * 0.13);

        // Distributed ore generation - much smaller veins
        if (diamondNoise > GenerationConfig.getDiamondOreThreshold() - chaosBonus && y < 16)
            return getMaterialOrFallback("DEEPSLATE_DIAMOND_ORE", Material.DIAMOND_ORE);
        if (goldNoise > GenerationConfig.getGoldOreThreshold() - chaosBonus && y < 32)
            return getMaterialOrFallback("DEEPSLATE_GOLD_ORE", Material.GOLD_ORE);
        if (ironNoise > GenerationConfig.getIronOreThreshold() - chaosBonus)
            return getMaterialOrFallback("DEEPSLATE_IRON_ORE", Material.IRON_ORE);
        if (copperNoise > GenerationConfig.getCopperOreThreshold() - chaosBonus)
            return getMaterialOrFallback("DEEPSLATE_COPPER_ORE", Material.STONE);
        if (coalNoise > GenerationConfig.getCoalOreThreshold() - chaosBonus)
            return getMaterialOrFallback("DEEPSLATE_COAL_ORE", Material.COAL_ORE);
        if (redstoneNoise > GenerationConfig.getRedstoneOreThreshold() - chaosBonus)
            return getMaterialOrFallback("DEEPSLATE_REDSTONE_ORE", Material.REDSTONE_ORE);

        // Add building materials in deep areas
        double buildingNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 7007, x * 0.06, y * 0.06,
                z * 0.06);
        if (buildingNoise > 0.7) {
            return getMaterialOrFallback("CLAY", Material.DIRT);
        }
        if (buildingNoise > 0.65) {
            return getMaterialOrFallback("GRAVEL", Material.COBBLESTONE);
        }

        // Chaos can create rare deep materials
        if (context.chaosIntensity > 1.0 && noise > 0.85) {
            return getMaterialOrFallback("SCULK", Material.DEEPSLATE);
        }

        Material deepStoneVariant = getDeepStoneVariantClump(x, y, z);
        if (deepStoneVariant != null)
            return deepStoneVariant;

        return getMaterialOrFallback("DEEPSLATE", Material.STONE);
    }

    private Material getChaoticOreOrStone(int x, int y, int z, BlockContext context) {
        double chaosBonus = context.chaosIntensity * GenerationConfig.getChaosTransitionOreBonus() * 0.3; // Reduced
                                                                                                          // chaos bonus

        // Use separate noise for each ore type to prevent clustering
        double ironNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 3003, x * 0.15, y * 0.15,
                z * 0.15);
        double copperNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 4004, x * 0.16, y * 0.16,
                z * 0.16);
        double coalNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 5005, x * 0.17, y * 0.17,
                z * 0.17);
        double redstoneNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 6006, x * 0.18, y * 0.18,
                z * 0.18);

        // Distributed ore generation - smaller veins
        if (ironNoise > GenerationConfig.getIronOreThreshold() - chaosBonus)
            return Material.IRON_ORE;
        if (copperNoise > GenerationConfig.getCopperOreThreshold() - chaosBonus)
            return getMaterialOrFallback("COPPER_ORE", Material.STONE);
        if (coalNoise > GenerationConfig.getCoalOreThreshold() - chaosBonus)
            return Material.COAL_ORE;
        if (redstoneNoise > GenerationConfig.getRedstoneOreThreshold() - chaosBonus)
            return Material.REDSTONE_ORE;

        // Add building materials (sand for glass-making)
        double buildingNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 7007, x * 0.12, y * 0.12,
                z * 0.12);
        if (buildingNoise > 0.75) {
            return Material.SAND; // Essential for glass crafting
        }
        if (buildingNoise > 0.7) {
            return getMaterialOrFallback("GRAVEL", Material.COBBLESTONE);
        }

        // Chaos creates material mixing
        if (context.chaosIntensity > GenerationConfig.getMaterialMixingThreshold()) {
            double mixNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getDistortionSeed(), x * 0.1, y * 0.1,
                    z * 0.1);
            if (mixNoise > 0.6)
                return getMaterialOrFallback("TUFF", Material.ANDESITE);
            if (mixNoise > 0.3)
                return getMaterialOrFallback("CALCITE", Material.DIORITE);
        }

        return Material.STONE;
    }

    private Material getChaoticTransitionMaterial(int x, int y, int z, String biome, BlockContext context) {
        double chaosBonus = context.chaosIntensity * GenerationConfig.getChaosTransitionOreBonus() * 0.25; // Reduced
                                                                                                           // chaos
                                                                                                           // bonus

        // Use separate noise for transition ores
        double ironNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 3003, x * 0.18, y * 0.18,
                z * 0.18);
        double copperNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 4004, x * 0.19, y * 0.19,
                z * 0.19);
        double coalNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 5005, x * 0.2, y * 0.2,
                z * 0.2);

        // Distributed ore generation
        if (ironNoise > GenerationConfig.getIronOreThreshold() - chaosBonus)
            return Material.IRON_ORE;
        if (copperNoise > GenerationConfig.getCopperOreThreshold() - chaosBonus)
            return getMaterialOrFallback("COPPER_ORE", Material.STONE);
        if (coalNoise > GenerationConfig.getCoalOreThreshold() - chaosBonus)
            return Material.COAL_ORE;

        // Add more building materials
        double buildingNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 8008, x * 0.14, y * 0.14,
                z * 0.14);
        if (buildingNoise > 0.8) {
            return getMaterialOrFallback("CLAY", Material.DIRT);
        }
        if (buildingNoise > 0.75) {
            return Material.SAND;
        }
        if (buildingNoise > 0.7) {
            return getMaterialOrFallback("GRAVEL", Material.COBBLESTONE);
        }

        // Chaos mixes biome materials
        if (context.chaosIntensity > GenerationConfig.getBiomeMixingThreshold()) {
            Material chaoticMaterial = getChaoticBiomeMix(x, y, z, biome);
            if (chaoticMaterial != null)
                return chaoticMaterial;
        }

        return switch (biome.toLowerCase()) {
            case DESERT, BADLANDS -> Material.SANDSTONE;
            case "mountains", "stony_peaks" -> Material.ANDESITE;
            case "dripstone_caves" -> Material.COBBLESTONE;
            default -> Material.STONE;
        };
    }

    private Material getChaoticBiomeMix(int x, int y, int z, String biome) {
        double mixNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getDistortionSeed() + 3000, x * 0.08, y * 0.05,
                z * 0.08);

        // Chaos mixes materials from different biomes
        if (mixNoise > 0.7) {
            return switch (biome.toLowerCase()) {
                case DESERT -> getMaterialOrFallback("RED_SANDSTONE", Material.SANDSTONE);
                case "forest" -> getMaterialOrFallback("MOSSY_COBBLESTONE", Material.COBBLESTONE);
                case "swamp" -> getMaterialOrFallback("MUD", Material.DIRT);
                case "mountains" -> getMaterialOrFallback("GRANITE", Material.STONE);
                default -> null;
            };
        }
        return null;
    }

    private Material getChaoticBiomeTopBlock(String biome, BlockContext context) {
        Material baseMaterial = getBiomeTopBlock(biome);

        // Chaos can corrupt surface blocks
        if (context.chaosIntensity > GenerationConfig.getSurfaceCorruptionThreshold()) {
            double corruptionNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getDistortionSeed() + 4000,
                    context.worldX * GenerationConfig.getCorruptionScale(),
                    context.y * (GenerationConfig.getCorruptionScale() * 0.5),
                    context.worldZ * GenerationConfig.getCorruptionScale());
            if (corruptionNoise > 0.6) {
                return switch (biome.toLowerCase()) {
                    case "plains", "forest" -> getMaterialOrFallback("PODZOL", Material.DIRT);
                    case DESERT -> getMaterialOrFallback("RED_SAND", Material.SAND);
                    case "swamp" -> getMaterialOrFallback("MUD", Material.DIRT);
                    default -> getMaterialOrFallback("COARSE_DIRT", baseMaterial);
                };
            }
        }

        return baseMaterial;
    }

    private Material getChaoticBiomeSubsurfaceBlock(String biome, BlockContext context) {
        Material baseMaterial = getBiomeSubsurfaceBlock(biome);

        // Chaos can create material layers
        if (context.chaosIntensity > 0.8) {
            double layerNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getDistortionSeed() + 5000,
                    context.worldX * 0.15, context.y * 0.1, context.worldZ * 0.15);
            if (layerNoise > 0.5) {
                return getMaterialOrFallback("PACKED_MUD", baseMaterial);
            }
        }

        return baseMaterial;
    }

    private Material getChaoticStoneVariant(int x, int y, int z, String biome, BlockContext context) {
        // Chaos creates wild stone variations
        if (context.chaosIntensity > GenerationConfig.getStoneVariantThreshold()) {
            double variantNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getDistortionSeed() + 6000,
                    x * GenerationConfig.getVariantScale(),
                    y * (GenerationConfig.getVariantScale() * 0.67),
                    z * GenerationConfig.getVariantScale());
            if (variantNoise > 0.7)
                return getMaterialOrFallback("TUFF", Material.STONE);
            if (variantNoise > 0.4)
                return Material.ANDESITE;
            if (variantNoise > 0.1)
                return Material.DIORITE;
            if (variantNoise > -0.2)
                return Material.GRANITE;
        }

        return Material.STONE;
    }

    private boolean isSolidEnvironment(BlockContext context) {
        double[] neighbors = new double[] {
                noise3D(context.worldX + 1, context.y, context.worldZ),
                noise3D(context.worldX - 1, context.y, context.worldZ),
                noise3D(context.worldX, context.y + 1, context.worldZ),
                noise3D(context.worldX, context.y - 1, context.worldZ),
                noise3D(context.worldX, context.y, context.worldZ + 1),
                noise3D(context.worldX, context.y, context.worldZ - 1)
        };

        for (double n : neighbors) {
            if (n <= 0.3)
                return false;
        }

        return true;
    }

    private Material getSkyIslandMaterial(int x, int y, int z, String biome) {
        // Sky islands have rare and valuable materials
        double noise = noise3D(x, y, z);
        double rareOreNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getOreSeed() + 1000, x * 0.03, y * 0.03,
                z * 0.03);

        // Sky islands contain much rarer and more valuable ores
        if (rareOreNoise > 0.9) {
            return getMaterialOrFallback("NETHERITE_BLOCK", Material.DIAMOND_BLOCK);
        }
        if (rareOreNoise > 0.85) {
            return getMaterialOrFallback("ANCIENT_DEBRIS", Material.DIAMOND_ORE);
        }
        if (rareOreNoise > 0.8) {
            return Material.DIAMOND_ORE; // Rare
        }
        if (rareOreNoise > 0.75) {
            return Material.EMERALD_ORE; // Rare
        }
        if (rareOreNoise > 0.7) {
            return Material.LAPIS_ORE; // Uncommon
        }
        if (rareOreNoise > 0.65) {
            return Material.GOLD_ORE; // Uncommon
        }

        // Regular sky island materials based on height
        if (y > 260) {
            // Top of sky islands - more ethereal materials
            if (noise > 0.6)
                return Material.END_STONE;
            if (noise > 0.4)
                return getMaterialOrFallback("CALCITE", Material.STONE);
            return getMaterialOrFallback("DRIPSTONE_BLOCK", Material.COBBLESTONE);
        } else if (y > 240) {
            // Middle of sky islands - mixed materials
            if (noise > 0.5)
                return getMaterialOrFallback("DEEPSLATE", Material.ANDESITE);
            return Material.STONE;
        } else {
            // Base of sky islands - solid foundation
            if (noise > 0.7)
                return Material.OBSIDIAN;
            return getMaterialOrFallback("BLACKSTONE", Material.COBBLESTONE);
        }
    }

    private Material getSurfaceMaterial(int x, int y, int z, String biome) {
        if (y < 5)
            return Material.BEDROCK;
        if (y < 15)
            return getDeepMaterial(x, y, z);
        if (y < 40)
            return getOreOrStone(x, y, z);
        if (y < 60)
            return getTransitionMaterial(x, y, z, biome);
        if (y == getSurfaceLevel(x, z, biome))
            return getBiomeTopBlock(biome);
        if (y < getSurfaceLevel(x, z, biome) + 3)
            return getBiomeSubsurfaceBlock(biome);
        return Material.STONE;
    }

    private Material getDeepMaterial(int x, int y, int z) {
        double noise = noise3D(x, y, z);
        if (noise > 0.85)
            return getMaterialOrFallback("DEEPSLATE_DIAMOND_ORE", Material.DIAMOND_ORE);
        if (noise > 0.8)
            return getMaterialOrFallback("DEEPSLATE_GOLD_ORE", Material.GOLD_ORE);
        if (noise > 0.75)
            return getMaterialOrFallback("DEEPSLATE_IRON_ORE", Material.IRON_ORE);
        if (noise > 0.7)
            return getMaterialOrFallback("DEEPSLATE_COPPER_ORE", Material.STONE);
        if (noise > 0.6)
            return getMaterialOrFallback("DEEPSLATE_COAL_ORE", Material.COAL_ORE);
        if (noise > 0.5)
            return getMaterialOrFallback("DEEPSLATE_REDSTONE_ORE", Material.REDSTONE_ORE);

        // Add deep stone variety in clumps
        Material deepStoneVariant = getDeepStoneVariantClump(x, y, z);
        if (deepStoneVariant != null) {
            return deepStoneVariant;
        }

        return getMaterialOrFallback("DEEPSLATE", Material.STONE);
    }

    private Material getTransitionMaterial(int x, int y, int z, String biome) {
        double noise = noise3D(x, y, z);
        if (noise > 0.8)
            return Material.IRON_ORE;
        if (noise > 0.75)
            return getMaterialOrFallback("COPPER_ORE", Material.STONE);
        if (noise > 0.7)
            return Material.COAL_ORE;

        // Generate stone variety in clumps
        Material stoneVariant = getStoneVariantClump(x, y, z, biome);
        if (stoneVariant != null) {
            return stoneVariant;
        }

        // Biome-specific stone variants
        return switch (biome.toLowerCase()) {
            case DESERT, BADLANDS -> Material.SANDSTONE;
            case "mountains", "stony_peaks" -> Material.ANDESITE;
            case "dripstone_caves" -> Material.COBBLESTONE; // Use cobblestone instead
            default -> Material.STONE;
        };
    }

    private int getSurfaceLevel(int x, int z, String biome) {
        // Vary surface level slightly based on biome and position
        double surfaceNoise = noise3D(x, GenerationConfig.getSeaLevel(), z) * 3;
        int baseLevel = GenerationConfig.getSeaLevel();

        return switch (biome.toLowerCase()) {
            case "mountains", "jagged_peaks", "stony_peaks" -> baseLevel + 20 + (int) surfaceNoise;
            case "hills" -> baseLevel + 10 + (int) surfaceNoise;
            case "ocean", "deep_ocean" -> baseLevel - 20;
            default -> baseLevel + (int) surfaceNoise;
        };
    }

    private Material getBiomeSubsurfaceBlock(String biome) {
        return switch (biome.toLowerCase()) {
            case DESERT -> Material.SAND;
            case MUSHROOM_FIELDS -> Material.MYCELIUM;
            case SNOWY_PLAINS, "ice_spikes" -> Material.SNOW_BLOCK;
            case BADLANDS -> Material.RED_SAND;
            case "soul_sand_valley" -> Material.SOUL_SAND;
            default -> Material.DIRT;
        };
    }

    private Material getBiomeTopBlock(String biome) {
        return switch (biome.toLowerCase()) {
            case DESERT -> Material.SAND;
            case MUSHROOM_FIELDS -> Material.MYCELIUM;
            case SNOWY_PLAINS, "ice_spikes" -> Material.SNOW_BLOCK;
            case BADLANDS -> Material.RED_SAND;
            case "taiga" -> Material.PODZOL;
            case "jungle" -> Material.GRASS_BLOCK;
            default -> Material.GRASS_BLOCK;
        };
    }

    private Material getOreOrStone(int x, int y, int z) {
        double noise = noise3D(x, y, z);
        if (y < 0) {
            return getDeepslateOre(noise);
        } else {
            return getStoneOre(noise);
        }
    }

    private Material getDeepslateOre(double noise) {
        if (noise > 0.8)
            return getMaterialOrFallback("DEEPSLATE_IRON_ORE", Material.IRON_ORE);
        if (noise > 0.7)
            return getMaterialOrFallback("DEEPSLATE_GOLD_ORE", Material.GOLD_ORE);
        if (noise > 0.6)
            return getMaterialOrFallback("DEEPSLATE_COPPER_ORE", Material.STONE);
        if (noise > 0.5)
            return getMaterialOrFallback("DEEPSLATE_COAL_ORE", Material.COAL_ORE);
        return getMaterialOrFallback("DEEPSLATE", Material.STONE);
    }

    private Material getStoneOre(double noise) {
        if (noise > 0.8)
            return Material.IRON_ORE;
        if (noise > 0.7)
            return getMaterialOrFallback("COPPER_ORE", Material.STONE);
        if (noise > 0.6)
            return Material.COAL_ORE;
        return Material.STONE;
    }

    private double noise3D(int x, int y, int z) {
        return OpenSimplex2.noise3_ImproveXY(GenerationConfig.getTerrainSeed(), x * 0.05, y * 0.05, z * 0.05);
    }

    /**
     * Generates stone variants in clumps for more geological variety
     */
    private Material getStoneVariantClump(int x, int y, int z, String biome) {
        // Use larger scale noise to create clumps rather than scattered variants
        double clumpNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getTerrainSeed() + 2000, x * 0.008,
                y * 0.008, z * 0.008);
        double varietyNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getTerrainSeed() + 3000, x * 0.012,
                y * 0.012, z * 0.012);

        // Define stone variant zones based on noise values
        if (clumpNoise > 0.4) {
            // Granite clumps
            if (varietyNoise > 0.3)
                return Material.GRANITE;
            if (varietyNoise > 0.0)
                return Material.POLISHED_GRANITE;
            return Material.GRANITE;
        } else if (clumpNoise > 0.1) {
            // Diorite clumps
            if (varietyNoise > 0.3)
                return Material.DIORITE;
            if (varietyNoise > 0.0)
                return Material.POLISHED_DIORITE;
            return Material.DIORITE;
        } else if (clumpNoise > -0.2) {
            // Andesite clumps
            if (varietyNoise > 0.3)
                return Material.ANDESITE;
            if (varietyNoise > 0.0)
                return Material.POLISHED_ANDESITE;
            return Material.ANDESITE;
        } else if (clumpNoise > -0.5) {
            // Stone variant clumps (calcite and tuff)
            if (varietyNoise > 0.2)
                return getMaterialOrFallback("CALCITE", Material.STONE);
            if (varietyNoise > -0.2)
                return getMaterialOrFallback("TUFF", Material.COBBLESTONE);
            return getMaterialOrFallback("CALCITE", Material.STONE);
        } else if (clumpNoise > -0.7) {
            // Stone variant clumps (deepslate variants)
            if (varietyNoise > 0.1)
                return getMaterialOrFallback("DEEPSLATE", Material.COBBLESTONE);
            if (varietyNoise > -0.3)
                return getMaterialOrFallback("COBBLED_DEEPSLATE", Material.ANDESITE);
            return getMaterialOrFallback("DEEPSLATE", Material.COBBLESTONE);
        }

        // Biome-specific rare stone clumps
        if (clumpNoise < -0.7) {
            return switch (biome.toLowerCase()) {
                case "desert", "badlands" -> varietyNoise > 0.0 ? Material.RED_SANDSTONE : Material.SANDSTONE;
                case "swamp", "mangrove_swamp" ->
                    varietyNoise > 0.0 ? getMaterialOrFallback("MUD", Material.DIRT) : Material.COARSE_DIRT;
                case "jungle" -> varietyNoise > 0.0 ? Material.MOSSY_COBBLESTONE : Material.MOSSY_STONE_BRICKS;
                case "taiga", "snowy_taiga" ->
                    varietyNoise > 0.0 ? Material.PACKED_ICE : getMaterialOrFallback("BLUE_ICE", Material.ICE);
                case "mountains", "stony_peaks" ->
                    varietyNoise > 0.0 ? getMaterialOrFallback("DEEPSLATE", Material.COBBLESTONE) : Material.STONE;
                default -> Material.COBBLESTONE;
            };
        }

        return null; // Return null to use default stone
    }

    /**
     * Generates deep stone variants in clumps for underground variety
     */
    private Material getDeepStoneVariantClump(int x, int y, int z) {
        // Use different noise for deep stone variants (use 1.15.2 compatible materials)
        double deepClumpNoise = OpenSimplex2.noise3_ImproveXY(GenerationConfig.getTerrainSeed() + 4000, x * 0.01,
                y * 0.01, z * 0.01);

        if (deepClumpNoise > 0.6) {
            return Material.COBBLESTONE;
        } else if (deepClumpNoise > 0.3) {
            return Material.STONE_BRICKS;
        } else if (deepClumpNoise > 0.0) {
            return Material.POLISHED_ANDESITE;
        } else if (deepClumpNoise > -0.3) {
            return Material.ANDESITE;
        } else if (deepClumpNoise > -0.6) {
            return Material.CHISELED_STONE_BRICKS;
        }

        return null; // Use default stone
    }

    /**
     * Places water at the specified position
     */
    public void placeWater(ChunkData chunk, int cx, int y, int cz) {
        chunk.setBlock(cx, y, cz, Material.WATER);
    }
}
