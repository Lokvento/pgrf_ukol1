package rasterops;

import rasterdata.Raster;

/**
 * Line rasterization algorithm: Trivial algorithm using the slope-intercept form (y = k * x + q).
 * This approach determines the driving axis (the one with the larger difference between endpoints)
 * and calculates the dependent coordinate using the line equation to ensure no gaps in the drawn line.
 */
public class LineRasterizerNaive implements LineRasterizer {

    @Override
    public void draw(final Raster raster,
                     int c1,
                     int r1,
                     int c2,
                     int r2,
                     int color) {

        int dx = c2 - c1;
        int dy = r2 - r1;

        // Determine the driving axis based on the largest absolute difference
        if (Math.abs(dx) >= Math.abs(dy)) {
            // Horizontal dominance: swap points if necessary to always draw left to right
            if (c1 > c2) {
                int temp = c1;
                c1 = c2;
                c2 = temp;
                temp = r1;
                r1 = r2;
                r2 = temp;
            }
            if (c1 == c2) return;

            // Calculate slope (k) and y-intercept (q) for y = k * x + q
            final double k = (double) (r2 - r1) / (c2 - c1);
            final double q = r1 - k * c1;

            for (int x = c1; x <= c2; x++) {
                final double y = k * x + q;
                raster.setColor(x, (int) Math.round(y), color);
            }
        } else {
            // Vertical dominance: swap points if necessary to always draw top to bottom
            if (r1 > r2) {
                int temp = c1;
                c1 = c2;
                c2 = temp;
                temp = r1;
                r1 = r2;
                r2 = temp;
            }
            if (r1 == r2) return;

            // Calculate inverse slope for x = k * y + q
            final double k = (double) (c2 - c1) / (r2 - r1);
            final double q = c1 - k * r1;

            for (int y = r1; y <= r2; y++) {
                final double x = k * y + q;
                raster.setColor((int) Math.round(x), y, color);
            }
        }
    }
}