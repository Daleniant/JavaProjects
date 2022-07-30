import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdDraw;

// Main object that solves a problem through brute force. Input points to this object
// Project mark - 89

public class BruteCollinearPoints {
    private LineSegment[] fullSegments = new LineSegment[0];
    
    public BruteCollinearPoints(Point[] points) {
        // finds all line segments containing exactly 4 points through brute force
        if (points == null) throw new IllegalArgumentException();
        
        sort(points); // sort points with a stable merge sort
        int n = points.length;
        for (int i = 0; i < n-3; i++) { // take first point         
            for (int j = i+1; j < n-2; j++) { // take second
                for (int k = j+1; k < n-1; k++) { // if third lies on the same line - take it
                    if (points[i].slopeTo(points[j]) == points[i].slopeTo(points[k])) {
                        for (int h = k+1; h < n; h++) { // if fourth on the same line, add a new line segment
                            if (points[i].slopeTo(points[j]) == points[i].slopeTo(points[h])) {
                                // Since points are sorted, these 2 are guaranteed to be the actual end points of a segment
                                addSegment(points[i], points[h]);
                            }  
                        }
                    }
                }
            }
        }
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
    
    private static boolean less(Point p1, Point p2) {
        return p1.compareTo(p2) < 0;
    }
     
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
     
     private static void sort(Point[] a) {
         int n = a.length;
         for (int i = 0; i < n; i++) { // preliminary check for null elements
             if (a[i] == null) throw new IllegalArgumentException();
         }
         
         Point[] temp = new Point[n];
         for (int sz = 1; sz < n; sz *= 2) { // bottom up merge sort
             for (int lo = 0; lo < n - sz; lo += 2*sz) {
                 merge(a, temp, lo, lo+sz-1, Math.min(lo+2*sz-1, n-1));
             }
         }
         
         for (int i = 0; i < n-1; i++) { // post-sort check for duplicates (Project specification)
             if (a[i].compareTo(a[i+1]) == 0) throw new IllegalArgumentException();
         }
     }
   
     public int numberOfSegments() {
         // returns the number of line segments
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
         BruteCollinearPoints collinear = new BruteCollinearPoints(points);
         for (LineSegment segment : collinear.segments()) {
             StdOut.println(segment);
             segment.draw();
         }
         StdDraw.show();
     }
}
