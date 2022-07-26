import edu.princeton.cs.algs4.StdOut;
import java.util.Comparator;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;

public class FastCollinearPoints {
    private LineSegment[] fullSegments = new LineSegment[0];
    
    /*
     * Take point 'p', treat it as origin
     * For each other point 'q' determine the slope it creates with p
     * Sort remaining points with respect to slope they make with p
     * Check adjacent points if all of them make the same slope with ps
     */
    public FastCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException();
        
        int n = points.length;
        sort(points); // Sort original array and also check if input is correct

        for (int i = 0; i < n-1; i++) {
            // Sort all the remaining points with respect to slope
            sort(points, i+1, points[i]);
            
            int j = i+1, s; // next element, counter of elements on the same line
            double refSlope;
            while (j < n) { // iterate through all remaining elements
                s = 1;
                refSlope = points[i].slopeTo(points[j]); // reference slope with a 1st element on the line
                // go through all adjacent elements as long as they make the same slope(on the same line)
                while (j+s < n && points[i].slopeTo(points[j+s]) == refSlope) {
                    s++;
                }
                
                // if we have a total of 4 or more (count points[i] as well), then create a new line segment
                if (s >= 3) { 
                    // go through previous points to check if we haven't yet created this line segment
                    boolean u = true;
                    for (int k = i-1; k > 0; k--) {
                        // reverse slope since all point before are smaller in value
                        if (points[k].slopeTo(points[i]) == refSlope) {
                            u = false; break;
                        }
                    }
                    // only if we haven't had this line segment - create it
                    // array was sorted by a stable sort, so end points on the array group are guaranteed to be line's end points
                    if (u) addSegment(points[i], points[j+s-1]);
                }
                j += s; // switch to next group of points, not on the current line
            }
            
            // since array was rearranged by slopeOrder sort, sort the remaining points again to put them in order by value
            sort(points, i+1);
        }
    }
    
    // merge sorts by value
    private static void merge(Point[] a, Point[] temp, int lo, int mid, int hi) {
        for (int k = lo; k <= hi; k++)
            temp[k] = a[k];
        
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) a[k] = temp[j++];
            else if (j > hi) a[k] = temp[i++];
            else if (less(temp[j], temp[i])) a[k] = temp[j++];
            else a[k] = temp[i++];
        }
    } 
    
    // merge sorts by slope order
    private static void merge(Point[] a, Point[] temp, int lo, int mid, int hi, Point point) {
        for (int i = lo; i <= hi; i++) {
            temp[i] = a[i];
        }
       
        int i = lo, j = mid+1;
        Comparator<Point> comparator = point.slopeOrder();
        for (int k = lo; k <= hi; k++) {
            if (j > hi) a[k] = temp[i++];
            else if (i > mid) a[k] = temp[j++];
            else if (comparator.compare(temp[j], temp[i]) < 0) a[k] = temp[j++];
            else a[k] = temp[i++];
        }
    }   
    
    // first sort
    private static void sort(Point[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) { // preliminary check for null elements
            if (a[i] == null) throw new IllegalArgumentException();
        }
        
        Point[] temp = new Point[n];
        for (int sz = 1; sz < n; sz *= 2) { // bottom-up merge sort by value
            for (int lo = 0; lo < n - sz; lo += 2*sz) {
                merge(a, temp, lo, lo+sz-1, Math.min(lo+2*sz-1, n-1));
            }
        }
        
        for (int i = 0; i < n-1; i++) { // post-sort check for duplicates (Project specification)
            if (a[i].compareTo(a[i+1]) == 0) throw new IllegalArgumentException();
        }
    }
    
    // same as first sort - by value, but without input checks and with beginning element specified
    private static void sort(Point[] a, int low) {
        int n = a.length;
        Point[] temp = new Point[n];
        for (int sz = 1; sz < n; sz *= 2) {
            for (int lo = low; lo < n - sz; lo += 2*sz) {
                merge(a, temp, lo, lo+sz-1, Math.min(lo+2*sz-1, n-1));
            }
        }
    }
    
    // sorts by slope order
    private static void sort(Point[] a, int low, Point mainPoint) {
        int n = a.length;
        Point[] temp = new Point[n];
        for (int sz = 1; sz < n; sz *= 2) { // bottom up merge sort
            for (int lo = low; lo < n - sz; lo += 2*sz) {
                merge(a, temp, lo, lo+sz-1, Math.min(lo+2*sz-1, n-1), mainPoint);
            }
        }
    }   
    
    private static boolean less(Point v, Point m) {
        return v.compareTo(m) < 0;
    }
    
    private void addSegment(Point p1, Point p2) { 
        resize(fullSegments.length+1); // To properly store all actual line segments - increase by 1
        fullSegments[fullSegments.length-1] = new LineSegment(p1, p2);
    }
    
    private void resize(int n) {
        LineSegment[] temp = new LineSegment[n];
        for (int i = 0; i < n-1; i++) {
            temp[i] = fullSegments[i];
        }
        fullSegments = temp;
    }
       
    public int numberOfSegments() {
        return fullSegments.length;
    }
    public LineSegment[] segments() {
        // for program's stability - return the copy of line segments array
        LineSegment[] copy = new LineSegment[fullSegments.length];
        for (int i = 0; i < fullSegments.length; i++)
            copy[i] = fullSegments[i];
        return copy;
    }
    
    public static void main(String[] args) {
     // read the n points from a file
        int n = StdIn.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = StdIn.readInt();
            int y = StdIn.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}