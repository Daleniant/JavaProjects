import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;      // red-black BST

// Main object for a brute force implementation
// Project mark - 97

public class PointSET {
    private SET<Point2D> points;
    
    public PointSET() {
        points = new SET<Point2D>();
    }
    
    public boolean isEmpty() {
        return points.isEmpty(); 
    }
    
    public int size() {
        return points.size();
    }
    
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        points.add(p);
    }
    
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        return points.contains(p); 
    }
    
    public void draw() {
        for (Point2D point : points)
            point.draw();
    }
    
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        // check every single point if it is inside a rectangle
        Queue<Point2D> q = new Queue<Point2D>();
        for (Point2D point : points) {
            if (rect.contains(point))
                q.enqueue(point);
        }
        
        return q;
    }
    
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return null;
        Point2D champ = points.min();
        double dist = p.distanceSquaredTo(champ);
        // check every single point and its distance to a query point
        for (Point2D point : points) {
            if (p.distanceSquaredTo(point) < dist) {
                dist = p.distanceSquaredTo(point);
                champ = point;
            }
        }
        
        return champ;
    }
}
