import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;

// Secondary object made to represent a Point
// Project mark - 89

public class Point implements Comparable<Point> {

    private final int x; // x-coordinate of this point
    private final int y; // y-coordinate of this point

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // draws this point to standard draw.
    public void draw() {
        StdDraw.point(x, y);
    }

    // draws the line segment between this point 
    // and the specified point to standard draw.
    public void drawTo(Point that) {
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     */ 
    public double slopeTo(Point that) {
        if (this.compareTo(that) == 0) return Double.NEGATIVE_INFINITY;
        if (this.y == that.y) return +0.0;
        if (this.x == that.x) return Double.POSITIVE_INFINITY;

        return (double) (that.y - this.y) / (that.x - this.x);
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     */
    public int compareTo(Point that) {
        if (this.y < that.y) return -1;
        if (this.y > that.y) return +1;
        if (this.x < that.x) return -1;
        if (this.x > that.x) return +1;
        return 0;
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     */
    public Comparator<Point> slopeOrder() {
        
        return new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
                double slopeDif = slopeTo(p2) - slopeTo(p1);
                if (slopeDif > 0) return -1;
                if (slopeDif < 0) return +1;
                return 0;
            }
        };
    };


    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     */
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public static void main(String[] args) {
        Point p1 = new Point(StdIn.readInt(), StdIn.readInt());
        Point p2 = new Point(StdIn.readInt(), StdIn.readInt());
        
        p1.draw();
        p2.draw();
        p1.drawTo(p2);
        
        System.out.println(p1.toString());
        System.out.println(p2.toString());
        
        System.out.println(p1.compareTo(p2));
        System.out.println(p2.compareTo(p1));
        
        System.out.println(p1.compareTo(p1));
    }
}
