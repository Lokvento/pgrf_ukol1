package rasterdata;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class RasterBI implements Raster {

    private final BufferedImage image;

    public RasterBI(final int width, final int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public void setColor(final int c, final int r, final int color) {
        if (isValidAddress(c, r)) {
            image.setRGB(c, r, color);
        }
    }

    @Override
    public Optional<Integer> getColor(final int c, final int r) {
        if (isValidAddress(c, r)) {
            return Optional.of(image.getRGB(c, r));
        }
        return Optional.empty();
    }

    /**
     * Presents this Raster onto a Swing component using the given instance of Graphics.
     * @param g instance of Graphics
     */
    public void present(final Graphics g) {
        if (g != null) {
            g.drawImage(image, 0, 0, null);
        }
    }

    /**
     * Clears values of all pixels in this Raster to the given address
     * @param color clear color
     */
    public void clear(final int color) {
        final Graphics g = image.getGraphics();
        if (g != null) {
            g.setColor(new Color(color));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
