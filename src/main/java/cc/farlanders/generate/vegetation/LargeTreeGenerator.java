package cc.farlanders.generate.vegetation;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator.ChunkData;

public class LargeTreeGenerator {

    private static final double LARGE_TREE_CHANCE = 0.15; // 15% of normal tree freq

    long hashCoords(int x, int z) {
        return x * 341873128712L + z * 132897987541L;
    }

    public void generateLargeTree(ChunkData chunk, int cx, int y, int cz, String biome) {
        Random random = new Random(hashCoords(cx, cz) ^ 0xBEEFBEEF);

        // Basic size parameters: height 15-25 blocks
        int height = 15 + random.nextInt(11);

        // Select log and leaf types by biome
        Material log = getLogForBiome(biome);
        Material leaves = getLeavesForBiome(biome);

        // Generate trunk: vertical column of logs
        for (int i = 0; i < height; i++) {
            chunk.setBlock(cx, y + i, cz, log);
        }

        // Generate leaves as a sphere near the top of the tree
        int leafRadius = Math.max(3, height / 5);
        int topY = y + height - 1;
        placeLargeTreeLeaves(chunk, cx, topY, cz, leafRadius, leaves);
    }

    private void placeLargeTreeLeaves(ChunkData chunk, int cx, int topY, int cz, int leafRadius, Material leaves) {
        LeafPlacementContext ctx = new LeafPlacementContext(chunk, cx, topY, cz, leafRadius, leaves);
        for (int dx = -leafRadius; dx <= leafRadius; dx++) {
            for (int dz = -leafRadius; dz <= leafRadius; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    placeLeafIfValid(ctx, dx, dy, dz);
                }
            }
        }
    }

    private static class LeafPlacementContext {
        final ChunkData chunk;
        final int cx, topY, cz, leafRadius;
        final Material leaves;

        LeafPlacementContext(ChunkData chunk, int cx, int topY, int cz, int leafRadius, Material leaves) {
            this.chunk = chunk;
            this.cx = cx;
            this.topY = topY;
            this.cz = cz;
            this.leafRadius = leafRadius;
            this.leaves = leaves;
        }
    }

    private void placeLeafIfValid(LeafPlacementContext ctx, int dx, int dy, int dz) {
        int distSq = dx * dx + dz * dz + dy * dy;
        if (distSq <= ctx.leafRadius * ctx.leafRadius) {
            int lx = ctx.cx + dx;
            int ly = ctx.topY + dy;
            int lz = ctx.cz + dz;
            // Avoid placing leaves inside trunk
            if (!(dx == 0 && dz == 0 && dy >= 0)) {
                ctx.chunk.setBlock(lx, ly, lz, ctx.leaves);
            }
        }
    }

    private Material getLogForBiome(String biome) {
        return switch (biome.toLowerCase()) {
            case "taiga" -> Material.SPRUCE_LOG;
            case "jungle" -> Material.JUNGLE_LOG;
            case "savanna" -> Material.ACACIA_LOG;
            case "cherry grove" -> Material.CHERRY_LOG;
            case "swamp" -> Material.OAK_LOG;
            default -> Material.OAK_LOG;
        };
    }

    private Material getLeavesForBiome(String biome) {
        return switch (biome.toLowerCase()) {
            case "taiga" -> Material.SPRUCE_LEAVES;
            case "jungle" -> Material.JUNGLE_LEAVES;
            case "savanna" -> Material.ACACIA_LEAVES;
            case "cherry grove" -> Material.CHERRY_LEAVES;
            case "swamp" -> Material.OAK_LEAVES;
            default -> Material.OAK_LEAVES;
        };
    }

    private void generateTallSpruce(ChunkData chunk, int x, int y, int z, Random random) {
        int height = 16 + random.nextInt(8);
        for (int i = 0; i < height; i++) {
            chunk.setBlock(x, y + i, z, Material.SPRUCE_LOG);
        }
        placeLeafCone(chunk, x, y + height - 4, z, 3, Material.SPRUCE_LEAVES);
    }

    private void generateJungleGiant(ChunkData chunk, int x, int y, int z, Random random) {
        int height = 18 + random.nextInt(6);
        for (int i = 0; i < height; i++) {
            chunk.setBlock(x, y + i, z, Material.JUNGLE_LOG);
        }
        placeLeafSphere(chunk, x, y + height, z, 4, Material.JUNGLE_LEAVES);
    }

    private void generateCherryBlossom(ChunkData chunk, int x, int y, int z, Random random) {
        int height = 10 + random.nextInt(4);
        for (int i = 0; i < height; i++) {
            chunk.setBlock(x, y + i, z, Material.CHERRY_LOG);
        }
        placeLeafSphere(chunk, x, y + height, z, 5, Material.CHERRY_LEAVES);
    }

    private void generateSwampOak(ChunkData chunk, int x, int y, int z, Random random) {
        int height = 8 + random.nextInt(4);
        for (int i = 0; i < height; i++) {
            chunk.setBlock(x, y + i, z, Material.OAK_LOG);
        }
        placeLeafBlob(chunk, x, y + height, z, 3, Material.OAK_LEAVES);
    }

    private void generateMassiveOak(ChunkData chunk, int x, int y, int z, Random random) {
        int height = 12 + random.nextInt(4);
        for (int i = 0; i < height; i++) {
            chunk.setBlock(x, y + i, z, Material.OAK_LOG);
        }
        placeLeafSphere(chunk, x, y + height - 1, z, 4, Material.OAK_LEAVES);
    }

    private void generateGenericGiant(ChunkData chunk, int x, int y, int z, Random random) {
        int height = 10 + random.nextInt(5);
        for (int i = 0; i < height; i++) {
            chunk.setBlock(x, y + i, z, Material.DARK_OAK_LOG);
        }
        placeLeafSphere(chunk, x, y + height, z, 4, Material.DARK_OAK_LEAVES);
    }

    private void placeLeafSphere(ChunkData chunk, int cx, int cy, int cz, int radius, Material leafType) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dy * dy + dz * dz <= radius * radius) {
                        chunk.setBlock(cx + dx, cy + dy, cz + dz, leafType);
                    }
                }
            }
        }
    }

    private void placeLeafCone(ChunkData chunk, int cx, int cy, int cz, int baseRadius, Material leafType) {
        for (int i = 0; i <= baseRadius; i++) {
            int radius = baseRadius - i;
            int layerY = cy + i;
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz <= radius * radius) {
                        chunk.setBlock(cx + dx, layerY, cz + dz, leafType);
                    }
                }
            }
        }
    }

    private void placeLeafBlob(ChunkData chunk, int cx, int cy, int cz, int radius, Material leafType) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                chunk.setBlock(cx + dx, cy, cz + dz, leafType);
            }
        }
    }
}
