package rasterops;

import rasterdata.Raster;

/**
 * Represents an algorithm for drawing straight lines onto a {@link rasterdata.Raster} instance
 */
public interface LineRasterizer {

    /**
     * Draws a straight line defined by the given pixel addresses.
     * @param raster Raster to draw onto
     * @param c1 column address defining the start of the line segment
     * @param r1 row address defining the start of the line segment
     * @param c2 column address defining the end of the line segment
     * @param r2 row address defining the end of the line segment
     * @param color color to use for drawing
     */
    void draw(Raster raster, int c1, int r1, int c2, int r2, int color);
}
