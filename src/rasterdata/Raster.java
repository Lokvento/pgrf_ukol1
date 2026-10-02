package rasterdata;

import java.util.Optional;

/**
 * Represents a two-dimensional regular grid containing pixels.
 * Pixels are represented as a 32-bit numerical integer values.
 */
public interface Raster {

    /**
     * Returns the number of columns in this Raster
     * @return number of columns
     */
    int getWidth();

    /**
     * Returns the number of rows in this Raster
     * @return number of rows
     */
    int getHeight();

    /**
     * Set a pixel with the given pixel address to the given color.
     * @param c column address
     * @param r row address
     * @param color new color
     */
    void setColor(int c, int r, int color);

    /**
     * Return Optional containing the color value at the specified pixel address
     * @param c column address
     * @param r row address
     * @return Optional of pixel value if the provided address is valid; empty Optional otherwise
     */
    Optional<Integer> getColor(int c, int r);

    /**
     * Checks weather the provided pixel address is valid for this Raster
     * @param c column address
     * @param r row address
     * @return true if address is valid; false otherwise
     */
    default boolean isValidAddress(int c, int r) {
        return (c >= 0 && c < getWidth() && r >= 0 && r < getHeight());
    }

}
