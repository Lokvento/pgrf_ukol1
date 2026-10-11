package objectdata;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a general polygon that is in the two-dimensional space
 * Points are using Integer coordinates
 */
public class Polygon {
    private final List<Point>points;

    public Polygon(){
        points = List.of();
    }
    private Polygon(List<Point> point){
        this.points = point;
    }
    public Polygon withPoint(final Point point){
        final List<Point> newPoint = new ArrayList<>(points);
        newPoint.add(point);
        return new  Polygon(newPoint);
    }
    public int getSize(){
        return points.size();
    }
    public Point getPoint(int index){
        return points.get(index);
    }

    public List<LineSegment>getLineSegments(){
        final List<LineSegment> lineSegments = new ArrayList<>();
        if (points.size()<2) return lineSegments;
        for (int i = 0; i < points.size(); i++) {
            int nextIndex = (i+1) %points.size();
            lineSegments.add(new LineSegment(points.get(i),points.get(nextIndex)));
        }
        return lineSegments;
    }
}
