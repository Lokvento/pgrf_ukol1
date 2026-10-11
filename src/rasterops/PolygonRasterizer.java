package rasterops;

import objectdata.LineSegment;
import objectdata.Polygon;
import rasterdata.Raster;

public class PolygonRasterizer {
    /**
     * Draws a two-dimensional Polygon using LineRasterizer as base for drawing
     * @param raster Raster to draw image
     * @param polygon Polygon to draw
     * @param lineRasterizer Algorithm for drawing lines
     * @param color color of the polygon
     */
    public void draw(final Raster raster, final Polygon polygon, final LineRasterizer lineRasterizer, final int color){
        for (LineSegment lineSegment : polygon.getLineSegments()) {
            lineRasterizer.draw(raster,lineSegment.p1().x(), lineSegment.p1().y(),lineSegment.p2().x(),lineSegment.p2().y(),color);
        }
    }
}
