package cc.farlanders.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import cc.farlanders.generate.biomes.api.BiomeRegistry;
import cc.farlanders.generate.biomes.modules.AshenWastesModule;
import cc.farlanders.generate.biomes.modules.CrystalCavernsModule;
import cc.farlanders.generate.biomes.modules.FarLandsModule;
import cc.farlanders.generate.biomes.modules.MysticForestModule;
import cc.farlanders.generate.biomes.modules.SunkenRuinsModule;
import cc.farlanders.generate.config.GenerationConfig;
import cc.farlanders.generate.density.DensityFunction;
import cc.farlanders.noise.OpenSimplex2;

/**
 * Export a single 16x16 chunk using the real DensityFunction and registered
 * BiomeModules. This is headless and does not attempt to use server
 * ChunkData/TerrainHandler; instead it outputs a heightmap PNG and a JSON
 * with per-column surface heights and a simple solid/air occupancy map.
 */
public class FullChunkExporter {

    public static void main(String[] args) throws Exception {
        // Initialize config and register modules
        initializeGenerationConfigForPreview();
        registerBiomeModules();

        // Basic parameters
        final int chunkSize = 16;
        final int chunkX = 0;
        final int chunkZ = 0;

        // Determine output directory: priority => system property -> first CLI arg ->
        // default
        String outProp = System.getProperty("farlanders.export.output");
        final File outDir;
        if (outProp != null && !outProp.isBlank()) {
            outDir = new File(outProp);
        } else if (args != null && args.length > 0 && args[0] != null && !args[0].isBlank()) {
            outDir = new File(args[0]);
        } else {
            outDir = new File("build/preview/full_chunk");
        }
        outDir.mkdirs();

        DensityFunction df = new DensityFunction();

        // Generate heightmap and image
        int[][] heights = generateHeights(df, chunkSize, chunkX, chunkZ, outDir);

        // Produce blocks and top material image
        BufferedImage topImg = new BufferedImage(chunkSize, chunkSize, BufferedImage.TYPE_INT_RGB);
        List<List<List<String>>> blocks = produceBlocksAndTopImage(df, heights, chunkSize, chunkX, chunkZ, topImg);

        // Write outputs
        writeHeightsJson(heights, chunkSize, outDir);
        writeBlocksJson(blocks, chunkSize, outDir);
        ImageIO.write(topImg, "png", new File(outDir, "chunk_top_materials.png"));

        System.out.println("Full chunk preview written to: " + outDir.getAbsolutePath());
    }

    private static void registerBiomeModules() {
        BiomeRegistry.register(new FarLandsModule());
        BiomeRegistry.register(new MysticForestModule());
        BiomeRegistry.register(new CrystalCavernsModule());
        BiomeRegistry.register(new SunkenRuinsModule());
        BiomeRegistry.register(new AshenWastesModule());
    }

    private static int[][] generateHeights(DensityFunction df, int chunkSize, int chunkX, int chunkZ, File outDir)
            throws Exception {
        int maxY = GenerationConfig.getMaxHeight();
        double threshold = GenerationConfig.getTerrainDensityThreshold();
        BufferedImage img = new BufferedImage(chunkSize, chunkSize, BufferedImage.TYPE_INT_RGB);
        int[][] heights = new int[chunkSize][chunkSize];

        for (int x = 0; x < chunkSize; x++) {
            for (int z = 0; z < chunkSize; z++) {
                int worldX = chunkX * chunkSize + x;
                int worldZ = chunkZ * chunkSize + z;

                var moduleOpt = BiomeRegistry.selectByWeight(worldX, worldZ);
                var module = moduleOpt.orElse(null);

                int surface = 0;
                double terrainMultiplier = module != null ? module.terrainHeightMultiplier() : 1.0;
                for (int y = 0; y < maxY; y++) {
                    double d = df.getCombinedDensity(worldX, y, worldZ, false, 0.0, terrainMultiplier);
                    if (d > threshold) {
                        surface = y;
                    }
                }

                if (module != null) {
                    var ctx = new cc.farlanders.generate.biomes.api.BiomeModule.ColumnGenerationContext();
                    ctx.setSurfaceHeight(surface);
                    module.onGenerateColumn(worldX, worldZ, ctx);
                    surface = ctx.getSurfaceHeight();
                }

                heights[x][z] = surface;

                int gray = (int) Math.round(255.0 * surface / (double) Math.max(1, maxY - 1));
                gray = Math.max(0, Math.min(255, gray));
                int rgb = (gray << 16) | (gray << 8) | gray;
                img.setRGB(x, z, rgb);
            }
        }

        ImageIO.write(img, "png", new File(outDir, "chunk_heights.png"));
        return heights;
    }

    private static List<List<List<String>>> produceBlocksAndTopImage(DensityFunction df, int[][] heights, int chunkSize,
            int chunkX, int chunkZ, BufferedImage topImg) {
        int maxYLevel = GenerationConfig.getMaxHeight();
        double sea = GenerationConfig.getSeaLevel();
        List<List<List<String>>> blocks = new ArrayList<>();

        for (int x = 0; x < chunkSize; x++) {
            List<List<String>> col = new ArrayList<>();
            for (int z = 0; z < chunkSize; z++) {
                int worldX = chunkX * chunkSize + x;
                int worldZ = chunkZ * chunkSize + z;
                var moduleOpt = BiomeRegistry.selectByWeight(worldX, worldZ);
                var module = moduleOpt.orElse(null);
                String biome = moduleOpt.map(m -> m.id()).orElse("FarLands");
                int surface = heights[x][z];
                List<String> column = new ArrayList<>(maxYLevel);

                for (int y = 0; y < maxYLevel; y++) {
                    double terrainMultiplier = module != null ? module.terrainHeightMultiplier() : 1.0;
                    double d = df.getCombinedDensity(worldX, y, worldZ, false, 0.0, terrainMultiplier);
                    String matName;
                    if (d > GenerationConfig.getTerrainDensityThreshold()) {
                        if (y <= 1) {
                            matName = "BEDROCK";
                        } else if (y < 16) {
                            matName = (OpenSimplex2.noise3ImproveXZ(42, worldX * 0.08, y * 0.08, worldZ * 0.08) > 0.8)
                                    ? "DIAMOND_ORE"
                                    : "DEEPSLATE";
                        } else if (y < surface - 3) {
                            matName = "STONE";
                        } else if (y == surface) {
                            matName = topBlockForBiome(biome);
                        } else {
                            matName = "DIRT";
                        }
                    } else {
                        matName = (y <= sea) ? "WATER" : "AIR";
                    }
                    column.add(matName);
                }

                String top = column.get(surface);
                topImg.setRGB(x, z, colorForMaterial(top));
                col.add(column);
            }
            blocks.add(col);
        }

        return blocks;
    }

    private static void writeHeightsJson(int[][] heights, int chunkSize, File outDir) throws Exception {
        try (FileWriter fw = new FileWriter(new File(outDir, "chunk_heights.json"))) {
            fw.write("{\n");
            fw.write("  \"chunk\": [\n");
            for (int z = 0; z < chunkSize; z++) {
                fw.write("    [");
                for (int x = 0; x < chunkSize; x++) {
                    fw.write(Integer.toString(heights[x][z]));
                    if (x < chunkSize - 1)
                        fw.write(", ");
                }
                fw.write("]");
                if (z < chunkSize - 1)
                    fw.write(",\n");
            }
            fw.write("\n  ]\n}");
        }
    }

    private static void writeBlocksJson(List<List<List<String>>> blocks, int chunkSize, File outDir) throws Exception {
        try (FileWriter fw = new FileWriter(new File(outDir, "chunk_blocks.json"))) {
            fw.write("{\n  \"blocks\": [\n");
            for (int x = 0; x < chunkSize; x++) {
                fw.write("    [\n");
                List<List<String>> columns = blocks.get(x);
                for (int z = 0; z < chunkSize; z++) {
                    List<String> col = columns.get(z);
                    fw.write("      [");
                    for (int y = 0; y < col.size(); y++) {
                        fw.write('"' + col.get(y) + '"');
                        if (y < col.size() - 1)
                            fw.write(", ");
                    }
                    fw.write("]");
                    if (z < chunkSize - 1)
                        fw.write(",\n");
                }
                fw.write("\n    ]");
                if (x < chunkSize - 1)
                    fw.write(",\n");
            }
            fw.write("\n  ]\n}\n");
        }
    }

    private static String topBlockForBiome(String biome) {
        return switch (biome.toLowerCase()) {
            case "mysticforest" -> "GRASS_BLOCK";
            case "crystalcaverns" -> "STONE";
            case "sunkenruins" -> "SAND";
            case "ashenwastes" -> "SOUL_SOIL";
            default -> "GRASS_BLOCK";
        };
    }

    private static int colorForMaterial(String mat) {
        return switch (mat.toUpperCase()) {
            case "GRASS_BLOCK" -> 0x6BA84B;
            case "DEEPSLATE", "STONE" -> 0x8B8B8B;
            case "DIRT" -> 0x7B5A3C;
            case "SAND" -> 0xE3D29A;
            case "WATER" -> 0x3A7BD5;
            case "BEDROCK" -> 0x222222;
            case "DIAMOND_ORE" -> 0x49E0D6;
            case "SOUL_SOIL" -> 0x6E6E9E;
            default -> 0xCCCCCC;
        };
    }

    private static void initializeGenerationConfigForPreview() throws Exception {
        // Create an empty YamlConfiguration and set it into GenerationConfig.config via
        // reflection
        try {
            Class<?> cfgClass = GenerationConfig.class;
            Field configField = cfgClass.getDeclaredField("config");
            Field initializedField = cfgClass.getDeclaredField("initialized");
            configField.setAccessible(true);
            initializedField.setAccessible(true);

            // Use a minimal FileConfiguration substitute if Bukkit classes available,
            // otherwise leave null
            Object yamlConfig;
            try {
                Class<?> yamlClass = Class.forName("org.bukkit.configuration.file.YamlConfiguration");
                yamlConfig = yamlClass.getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                // bukkit not on runtime classpath; GenerationConfig getters that rely on config
                // may not be used
                yamlConfig = null;
            }
            configField.set(null, yamlConfig);

            // Call private loadCachedValues() reflectively to populate cached fields; if
            // missing, set defaults
            try {
                Method load = cfgClass.getDeclaredMethod("loadCachedValues");
                load.setAccessible(true);
                load.invoke(null);
            } catch (NoSuchMethodException ex) {
                // Fallback: set some common cached fields
                trySetIntField(cfgClass, "seaLevel", 64);
                trySetIntField(cfgClass, "maxHeight", 320);
                trySetDoubleField(cfgClass, "farlandsThreshold", 12550820.0);
                trySetDoubleField(cfgClass, "baseTerrainScale", 0.005);
                trySetDoubleField(cfgClass, "caveScale", 0.015);
                trySetDoubleField(cfgClass, "biomeScale", 0.003);
            }

            initializedField.setBoolean(null, true);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchFieldException
                | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to initialize GenerationConfig for preview: " + e.getMessage(), e);
        }
    }

    private static void trySetIntField(Class<?> cls, String name, int value) {
        try {
            Field f = cls.getDeclaredField(name);
            f.setAccessible(true);
            f.setInt(null, value);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ignored) {
        }
    }

    private static void trySetDoubleField(Class<?> cls, String name, double value) {
        try {
            Field f = cls.getDeclaredField(name);
            f.setAccessible(true);
            f.setDouble(null, value);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ignored) {
        }
    }
}
