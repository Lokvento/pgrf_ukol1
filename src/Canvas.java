import objectdata.LineSegment;
import objectdata.Point;
import objectdata.Polygon;
import rasterdata.RasterBI;
import rasterops.LineRasterizer;
import rasterops.LineRasterizerNaive;
import rasterops.PolygonRasterizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Minimal class for drawing pixels
 */
public class Canvas {

    private final JPanel panel;
    private final RasterBI image;

    private final LineRasterizer lineRasterizer = new LineRasterizerNaive();

    private final PolygonRasterizer polygonRasterizer = new PolygonRasterizer();
    private final int backgroundColor = 0x000000;

    private Polygon polygon = new Polygon();
    private final List<LineSegment> lineSegments = new ArrayList<>();

    private Point currentMousePoint;
    private Point lineStartPoint;
    private boolean isDragging = false;
    private boolean isLineMode = true;

    public Canvas(final int width, final int height) {
        JFrame frame = new JFrame("UHK 2026 PGRF1 | Ukol1 | C04 ");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        image = new RasterBI(width, height);
        image.clear(backgroundColor);

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                image.present(g);
                g.setColor(Color.WHITE);
                g.drawString("To clear canvas press C",10,10);
                g.drawString("Mode (E):" + (isLineMode? "Line":"Polygon"),150,10);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    polygon = new Polygon();
                    lineSegments.clear();
                    isDragging = false;
                    redraw();
                } else if (e.getKeyCode()== KeyEvent.VK_E) {
                    isLineMode= !isLineMode;
                    isDragging = false;
                    redraw();
                }

            }
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isLineMode) {
                    lineStartPoint = new Point(e.getX(),e.getY());
                }else {
                    if (polygon.getSize() == 0) {
                        polygon = polygon.withPoint(new Point(e.getX(), e.getY()));
                    }
                }
                currentMousePoint = new Point(e.getX(), e.getY());
                isDragging = true;
                redraw();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
               currentMousePoint = new Point(e.getX(), e.getY());
                redraw();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false;
                if(isLineMode) {
                    if (lineStartPoint != null){
                        lineSegments.add(new LineSegment(lineStartPoint,new Point(e.getX(),e.getY())));
                    }
                }else  {
                    polygon = polygon.withPoint(new Point(e.getX(), e.getY()));
                }
                redraw();
            }
        };
        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();

        panel.setFocusable(true);
        panel.grabFocus();

    }

    static void main() {
        SwingUtilities.invokeLater(() -> new Canvas(1000, 1000));
    }

    private void redraw() {
        image.clear(backgroundColor);

        int lineColor = 0xFF0000;
        for (LineSegment lineSegment : lineSegments) {
            lineRasterizer.draw(image,lineSegment.p1().x(),lineSegment.p1().y(),lineSegment.p2().x(),lineSegment.p2().y(), lineColor);
        }
        int polygonColor = 0x00FF00;
        polygonRasterizer.draw(image, polygon, lineRasterizer, polygonColor);
        if (isDragging && currentMousePoint != null && isLineMode && lineStartPoint !=null) {
            lineRasterizer.draw(image,lineStartPoint.x(),lineStartPoint.y(), currentMousePoint.x(), currentMousePoint.y(), lineColor);
        } else if (!isLineMode && polygon.getSize() >0) {
            Point lastPoint = polygon.getPoint(polygon.getSize() - 1);
            lineRasterizer.draw(image, lastPoint.x(), lastPoint.y(), currentMousePoint.x(), currentMousePoint.y(), polygonColor);
        }

        panel.repaint();
    }
}
