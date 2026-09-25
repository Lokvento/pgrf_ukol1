import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;


/**
 * Minimal class for drawing pixels
 */
public class Canvas {

    private final JFrame frame;
    private final JPanel panel;
    private final BufferedImage image;

    private int column, row;

    public Canvas(final int width, final int height) {
        frame = new JFrame("UHK 2026 PGRF1 | Martin Konvicka");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // image.setRGB(10, 10, 0b111111110000000000000000);

        // for (int i = 0; i < width; i++) {
        //     image.setRGB(i, i, 0xff0000);
        // }

        // for (int i = width / 4; i < width / 4 * 3; i++) {
        //     image.setRGB(i, height / 4, 0xff00);
        //     image.setRGB(i, height / 4 * 3, 0xff00);
        //     image.setRGB(width /4  , i, 0xff00);
        //     image.setRGB(width / 4 * 3, i, 0xff00);
        // }

        column = width / 2;
        row = height / 2;
        image.setRGB(column, row, 0xff00);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // co se ma stat kdyz se stiskne klavesa
                // HW: pri stisku sipky dolu se posuneme o radek a nastavime hodnotu dalsiho pixelu
                // ...
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
