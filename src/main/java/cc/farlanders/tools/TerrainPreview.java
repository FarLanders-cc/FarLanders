package cc.farlanders.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import cc.farlanders.noise.OpenSimplex2;

/**
 * Simple preview tool that renders a small heightmap for several
 * terrainHeightMultiplier values side-by-side so you can visually
 * compare biome terrain variants without launching Minecraft.
 *
 * Run after compiling with:
 * ./gradlew clean compileJava -x test
 * java -cp build/classes/java/main cc.farlanders.tools.TerrainPreview
 */
public class TerrainPreview {

    private static final Logger logger = Logger.getLogger("FarLanders");

    public static void main(String[] args) throws Exception {
        int panelW = 256;
        int panelH = 256;
        double[] multipliers = new double[] { 2.0, 1.05, 0.5, 0.45, 0.85 };
        int panels = multipliers.length;

        int imgW = panelW * panels;
        int imgH = panelH;

        BufferedImage img = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_RGB);

        // Use a simple local density estimator for preview so we don't need full
        // config/plugin
        SimplePreviewDensity df = new SimplePreviewDensity();

        int maxY = 320;
        double threshold = 0.5;

        renderPanels(img, panelW, panelH, multipliers, df, maxY, threshold);

        File out = new File("build/preview/terrain_preview.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "png", out);

        logger.log(Level.INFO, "Preview generated: {0}", out.getAbsolutePath());
    }

    private static void renderPanels(BufferedImage img, int panelW, int panelH, double[] multipliers,
            SimplePreviewDensity df, int maxY, double threshold) {
        int panels = multipliers.length;
        for (int p = 0; p < panels; p++) {
            renderPanel(img, panelW, panelH, multipliers[p], p, df, maxY, threshold);
        }
    }

    private static void renderPanel(BufferedImage img, int panelW, int panelH, double mult, int panelIndex,
            SimplePreviewDensity df, int maxY, double threshold) {
        int xOff = panelIndex * panelW;

        for (int ix = 0; ix < panelW; ix++) {
            for (int iz = 0; iz < panelH; iz++) {
                int worldX = ix - panelW / 2 + panelIndex * 8; // slight offset per panel
                int worldZ = iz - panelH / 2;

                int surface = computeSurface(df, worldX, worldZ, mult, maxY, threshold);

                // Normalize height to 0..255
                int gray = (int) Math.round(255.0 * surface / Math.max(1, maxY - 1));
                gray = Math.clamp(0, gray, 255);
                int rgb = (gray << 16) | (gray << 8) | gray;
                img.setRGB(xOff + ix, iz, rgb);
            }
        }

        // Draw a label (simple colored bar) at top to indicate multiplier
        drawLabel(img, xOff, panelW);
    }

    private static int computeSurface(SimplePreviewDensity df, int worldX, int worldZ, double mult, int maxY,
            double threshold) {
        int surface = 0;
        for (int y = 0; y < maxY; y++) {
            double d = df.getCombinedDensity(worldX, y, worldZ, mult);
            if (d > threshold) {
                surface = y;
            }
        }
        return surface;
    }

    private static void drawLabel(BufferedImage img, int xOff, int panelW) {
        int labelY = 4;
        int labelH = 12;
        int color = 0xFFFFFF;
        for (int lx = 2; lx < panelW - 2; lx++) {
            for (int ly = labelY; ly < labelY + labelH; ly++) {
                int tx = xOff + lx;
                int ty = ly;
                img.setRGB(tx, ty, color);
            }
        }
    }

    // Local simplified density function for preview purposes only.
    static final class SimplePreviewDensity {
        private static final long SEED = 123456789L;

        double getCombinedDensity(int x, int y, int z, double terrainMultiplier) {
            double scale = 0.005 * (1.0);
            double primary = OpenSimplex2.noise3ImproveXZ(SEED, x * scale, y * scale * 0.3, z * scale);
            double detail = OpenSimplex2.noise3ImproveXZ(SEED + 1, x * scale * 4.0, y * scale, z * scale * 4.0) * 0.3;
            double combined = primary + detail;

            // height factor: higher y reduces density
            double heightFactor = 1.0 - y / 320.0;
            double heightBoost = y < 80 ? 0.3 : 0.0;

            double val = (combined) * heightFactor + (heightFactor - 0.2) + heightBoost;

            val *= Math.clamp(terrainMultiplier, 0.0, Double.POSITIVE_INFINITY);
            return Math.clamp(val, 0.0, 1.0);
        }
    }
}
