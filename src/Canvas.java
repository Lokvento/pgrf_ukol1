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

public class Canvas {

    private final JPanel panel;
    private final RasterBI image;

    private final LineRasterizer lineRasterizer = new LineRasterizerNaive();
    private final PolygonRasterizer polygonRasterizer = new PolygonRasterizer();
    private final int backgroundColor = 0x000000;
    private final List<LineSegment> lineSegments = new ArrayList<>();
    private Polygon polygon = new Polygon();

    private Point currentMousePoint;
    private Point lineStartPoint;

    private boolean isDragging = false;
    private boolean hasDragged = false;
    private boolean isLineMode = true;
    private int editPointIndex = -1;

    public Canvas(final int width, final int height) {
        JFrame frame = new JFrame("UHK 2026 PGRF1 | Ukol1 | C04");
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
                g.drawString("To clear canvas press C", 10, 20);
                g.drawString("Mode (E): " + (isLineMode ? "Line" : "Polygon"), 150, 20);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        /**
         * KeyListener manages the global state of the application.
         * Pressing 'C' clears all data structures and the canvas.
         * Pressing 'E' toggles between drawing lines and drawing polygons.
         */
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    polygon = new Polygon();
                    lineSegments.clear();
                    isDragging = false;
                    hasDragged = false;
                    editPointIndex = -1;
                    lineStartPoint = null;
                    redraw();
                } else if (e.getKeyCode() == KeyEvent.VK_E) {
                    isLineMode = !isLineMode;
                    isDragging = false;
                    hasDragged = false;
                    editPointIndex = -1;
                    lineStartPoint = null;
                    redraw();
                }
            }
        });

        /**
         * MouseAdapter handles all interactions on the canvas.
         * It separates logic for Left Mouse Button (LMB) used for drawing,
         * and Right Mouse Button (RMB) used for editing existing vertices.
         */
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // RMB: Find the closest polygon vertex within a 20-pixel radius to allow editing
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (!isLineMode && polygon.getSize() > 0) {
                        double minDistance = Double.MAX_VALUE;
                        for (int i = 0; i < polygon.getSize(); i++) {
                            Point pt = polygon.getPoint(i);
                            double dist = distance(e.getX(), e.getY(), pt.x(), pt.y());
                            if (dist < minDistance && dist < 20) {
                                minDistance = dist;
                                editPointIndex = i;
                            }
                        }
                    }
                    return;
                }

                // LMB: Initiate a new line segment or add a new vertex to the polygon
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (isLineMode) {
                        if (lineStartPoint == null) {
                            lineStartPoint = new Point(e.getX(), e.getY());
                        } else {
                            lineSegments.add(new LineSegment(lineStartPoint, new Point(e.getX(), e.getY())));
                            lineStartPoint = null;
                        }
                        hasDragged = false;
                    } else {
                        // Setup the first point if the polygon is completely empty
                        if (polygon.getSize() == 0) {
                            polygon = polygon.withPoint(new Point(e.getX(), e.getY()));
                        }
                    }
                }
                currentMousePoint = new Point(e.getX(), e.getY());
                isDragging = true;
                redraw();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // RMB: Update the coordinates of the currently selected polygon vertex dynamically
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (!isLineMode && editPointIndex != -1) {
                        polygon = polygon.updatePoint(editPointIndex, new Point(e.getX(), e.getY()));
                        redraw();
                    }
                    return;
                }

                // LMB: Track the mouse position to draw a dynamic preview (rubber-band) of the line
                if (SwingUtilities.isLeftMouseButton(e)) {
                    hasDragged = true;
                    currentMousePoint = new Point(e.getX(), e.getY());
                    redraw();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                currentMousePoint = new Point(e.getX(), e.getY());
                redraw();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // RMB: Deselect the currently edited vertex
                if (SwingUtilities.isRightMouseButton(e)) {
                    editPointIndex = -1;
                    return;
                }

                // LMB: Finalize the drawn line segment or append the vertex to the polygon
                if (SwingUtilities.isLeftMouseButton(e)) {
                    isDragging = false;
                    if (isLineMode) {
                        if (lineStartPoint != null && hasDragged) {
                            lineSegments.add(new LineSegment(lineStartPoint, new Point(e.getX(), e.getY())));
                            lineStartPoint = null;
                            hasDragged = false;
                        }
                    } else {
                        // BONUS: Insert point into the closest edge if polygon has >= 3 vertices
                        if (polygon.getSize() >= 3) {
                            int edgeIndex = getClosestEdgeIndex(e.getX(), e.getY());
                            polygon = polygon.insertPoint(edgeIndex + 1, new Point(e.getX(), e.getY()));
                        } else {
                            // Standard append for the 2nd and 3rd point
                            polygon = polygon.withPoint(new Point(e.getX(), e.getY()));
                        }
                    }
                    redraw();
                }
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

    /**
     * Clears the canvas and redraws all committed structures (lines and polygons).
     * Also handles the rendering of dynamic preview lines (rubber-banding) based on the current mouse position.
     */
    private void redraw() {
        int lineColor = 0xFF0000;
        int polygonColor = 0x00FF00;
        image.clear(backgroundColor);

        // Draw saved lines and the finalized polygon
        for (LineSegment lineSegment : lineSegments) {
            lineRasterizer.draw(image, lineSegment.p1().x(), lineSegment.p1().y(), lineSegment.p2().x(), lineSegment.p2().y(), lineColor);
        }
        polygonRasterizer.draw(image, polygon, lineRasterizer, polygonColor);

        // Draw rubber-band preview lines based on current mode
        if (isLineMode && lineStartPoint != null) {
            image.setColor(lineStartPoint.x(), lineStartPoint.y(), lineColor);
            if (currentMousePoint != null) {
                lineRasterizer.draw(image, lineStartPoint.x(), lineStartPoint.y(), currentMousePoint.x(), currentMousePoint.y(), lineColor);
            }
        } else if (!isLineMode && polygon.getSize() > 0 && currentMousePoint != null && editPointIndex == -1) {

            // BONUS: Dynamically preview the split of the closest edge
            if (polygon.getSize() >= 3) {
                int edgeIndex = getClosestEdgeIndex(currentMousePoint.x(), currentMousePoint.y());
                Point p1 = polygon.getPoint(edgeIndex);
                Point p2 = polygon.getPoint((edgeIndex + 1) % polygon.getSize());
                lineRasterizer.draw(image, p1.x(), p1.y(), currentMousePoint.x(), currentMousePoint.y(), polygonColor);
                lineRasterizer.draw(image, p2.x(), p2.y(), currentMousePoint.x(), currentMousePoint.y(), polygonColor);
            } else {
                // Standard preview for the first few points
                Point lastPoint = polygon.getPoint(polygon.getSize() - 1);
                lineRasterizer.draw(image, lastPoint.x(), lastPoint.y(), currentMousePoint.x(), currentMousePoint.y(), polygonColor);

                // Draw closing line back to the first point if the polygon has at least 2 points
                if (polygon.getSize() > 1) {
                    Point firstPoint = polygon.getPoint(0);
                    lineRasterizer.draw(image, firstPoint.x(), firstPoint.y(), currentMousePoint.x(), currentMousePoint.y(), polygonColor);
                }
            }
        }

        panel.repaint();
    }

    /**
     * Calculates the standard Euclidean distance between two points in a 2D space.
     */
    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * Iterates through all edges of the polygon to find the one closest to the given (x, y) coordinates.
     * Returns the index of the starting point of the closest edge.
     */
    private int getClosestEdgeIndex(int x, int y) {
        double minDistance = Double.MAX_VALUE;
        int closestIndex = 0;
        for (int i = 0; i < polygon.getSize(); i++) {
            Point p1 = polygon.getPoint(i);
            Point p2 = polygon.getPoint((i + 1) % polygon.getSize());
            double dist = distancePointToSegment(x, y, p1.x(), p1.y(), p2.x(), p2.y());
            if (dist < minDistance) {
                minDistance = dist;
                closestIndex = i;
            }
        }
        return closestIndex;
    }

    /**
     * Calculates the shortest distance from a point (px, py) to a line segment defined by (x1, y1) and (x2, y2).
     * Uses vector projection to find the closest point on the line segment, bounded by [0, 1] to ensure
     * the projection doesn't fall outside the segment's endpoints.
     */
    private double distancePointToSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double l2 = Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
        if (l2 == 0) {
            return distance(px, py, x1, y1);
        }
        double t = Math.max(0, (Math.min(1, ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / l2)));
        double projX = x1 + t * (x2 - x1);
        double projY = y1 + t * (y2 - y1);
        return distance(px, py, projX, projY);
    }
}