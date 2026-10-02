import rasterdata.RasterBI;
import rasterops.LineRasterizerNaive;
import rasterops.LineRasterizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 * Minimal class for drawing pixels
 */
public class Canvas {

    private final JFrame frame;
    private final JPanel panel;
    private final RasterBI image;

    private int column, row;

    public Canvas(final int width, final int height) {
        frame = new JFrame("UHK 2026 PGRF1 | Martin Konvicka");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        image = new RasterBI(width, height);

        column = width / 2;
        row = height / 2;
        image.setColor(column, row, 0xff00);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                image.present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    row = row + 1;
                    image.setColor(column, row, 0xff00);
                }
                panel.repaint();
            }
        });

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();

        panel.setFocusable(true);
        panel.grabFocus();
    }

    static void main() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Canvas(500, 500);
            }
        });
    }
}
