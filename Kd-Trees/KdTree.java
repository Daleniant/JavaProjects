import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.RectHV;

// Main object for a 2-d Tree implementation
// Project mark - 97

public class KdTree {
    private Node root;
    private int count;
    
    private class Node {
        Point2D point;
        Node left, right;
        
        public Node(Point2D p) {
            point = p;
            left = null; right = null;
        }
    }
    
    public KdTree() {
        /*
         * Convention: for all points in the even levels(root is level 0) we compare based on x coordinate,
         * meaning point is less than current if it is to the left
         * And for all points in the odd levels we compare by y coordinate, 
         * meaning point is less than current if it is under it
         */
        root = null;
        count = 0;
    }
    
    public boolean isEmpty() {
        return root == null; 
    }
    
    public int size() { 
        return count;
    }
    
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (!contains(p)) {
            root = insert(root, p, 0);
            count++;
        }
    }
    // recursive insert that goes down level by level
    private Node insert(Node cur, Point2D p, int lvl) {
        if (cur == null) return new Node(p);
        
        if (less(p, cur.point, lvl++)) 
            cur.left = insert(cur.left, p, lvl);
        else 
            cur.right = insert(cur.right, p, lvl);
        return cur;
    }
    // checks if 1 points is less than the other, where less means to the left/bottom and greater = right/up
    private boolean less(Point2D p1, Point2D p2, int lvl) {
        if (lvl % 2 == 0)
            return p1.x() < p2.x();
        return p1.y() < p2.y();
    }
    
    // checks if current board has a specified point by simple BST find
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        
        Node temp = root;
        int lvl = 0;
        while (temp != null) {
            if (p.equals(temp.point)) return true;
            if (less(p, temp.point, lvl++)) temp = temp.left;
            else temp = temp.right;
        }
        return false;
    }
    // draw all the points and lines that represent how they divide the board
    public void draw() {
        inorderDraw(root, new Node(new Point2D(0, 0)), 0);
    }
    // recursive inorder traversal across BST with drawing according to specifications
    private void inorderDraw(Node temp, Node prev, int lvl) {
        if (temp == null)
            return;
        
        inorderDraw(temp.left, temp, lvl+1);
        
        StdDraw.setPenColor(StdDraw.BLACK);
        temp.point.draw();
        
        if (lvl % 2 == 0) { // if we're on even level - draw vertical line and bound it by previous line
            StdDraw.setPenColor(StdDraw.RED);
            if (less(prev.point, temp.point, lvl))
                StdDraw.line(temp.point.x(), 0, temp.point.x(), prev.point.y());
            else
                StdDraw.line(temp.point.x(), prev.point.y(), temp.point.x(), 1);
        }
        else {              // if on odd - draw horizontal and bound it by previous line
            StdDraw.setPenColor(StdDraw.BLUE);
            if (less(prev.point, temp.point, lvl))
                StdDraw.line(prev.point.x(), temp.point.y(), 1, temp.point.y());
            else
                StdDraw.line(0, temp.point.y(), prev.point.x(), temp.point.y());
        }
        
        inorderDraw(temp.right, temp, lvl+1);
    }
    // find all the points that are inside a given rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        
        Queue<Point2D> q = new Queue<Point2D>();
        evalRect(root, rect, q, 0);
        return q;
    }
    /*
     *  recursive search for points that are inside a rectangle:
     *  if rectangle contains current point - add it to the queue
     *  If rectangle is on the points hyperplane(vertical or horizontal line depending on nodes depth),
     *  then observe both children, else check the respective halves of the board that remain
     */
    private void evalRect(Node temp, RectHV rect, Queue<Point2D> q, int lvl) {
        if (temp == null)
            return;
        
        if (rect.contains(temp.point))
            q.enqueue(temp.point);
        // depending on location of rectangle - observe relevant halves
        int comp = compare(rect, temp.point, lvl++);
        // if rectangle is strictly to the left/bottom of a node
        if (comp < 0)
            evalRect(temp.left, rect, q, lvl);
        // if strictly right/above
        else if (comp > 0)
            evalRect(temp.right, rect, q, lvl);
        // if on the hyprplane
        else {
            evalRect(temp.left, rect, q, lvl);
            evalRect(temp.right, rect, q, lvl);
        }
    }
    // custom comparison of a rectangle
    private int compare(RectHV rect, Point2D p, int lvl) {
        // comparison with vertical line by x coordinates
        if (lvl % 2 == 0) {
            if (rect.xmax() < p.x())
                return -1;              // directly left
            if (rect.xmin() > p.x())
                return +1;              // directly right
            return 0;                   // on hyperplane
        }// comparison with horizontal by y coordinates
        else {
            if (rect.ymax() < p.y())
                return -1;              // directly below
            if (rect.ymin() > p.y())
                return +1;              // directly above
            return 0;                   // on hyperplane
        }
    }
    // find the nearest point from already existing to a query point
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return null;
        // handle corner cases and call recursive search
        return evalNeighbor(root, p, root.point, 0);
    }
    /*
     * Fining the nearest neighbor:
     * Go down the tree recursively as if you are searching for the query point
     * Once you hit a leaf - check if it is closer to the query point and update current nearest if needed
     * During unwrapping recursion check if current node is closer to query point and update current champ
     * If the hyperplane of a point(horizontal or vertical line depending on entry) intersects hypersphere
     * of query point(circle with radius of distance to current nearest) - then check the other child of a node
     * that is in another direction of finding algorithm
     * 
     * https://en.wikipedia.org/wiki/K-d_tree#:~:text=The%20nearest%20neighbour%20search%20(NN,portions%20of%20the%20search%20space.
     */
    private Point2D evalNeighbor(Node cur, Point2D p, Point2D champ, int lvl) {
        // in case we followed a null-child
        if (cur == null) return champ;
        // handling the leaf
        if (cur.left == null && cur.right == null) {
            if (p.distanceSquaredTo(cur.point) < p.distanceSquaredTo(champ))
                return cur.point;
            return champ;
        }
        // going down the left child
        if (less(p, cur.point, lvl)) {
            // go down the search
            champ = evalNeighbor(cur.left, p, champ, lvl+1);
            // update if current node is closer
            if (p.distanceSquaredTo(cur.point) < p.distanceSquaredTo(champ))
                champ = cur.point;
            // if satisfies condition to check the remaining half
            if (shouldCheck(cur.point, p, lvl, p.distanceSquaredTo(champ)))
                champ = evalNeighbor(cur.right, p, champ, lvl+1);
        }
        // same as for the left child, but for the right one
        else {
            champ = evalNeighbor(cur.right, p, champ, lvl+1);
            
            if (p.distanceSquaredTo(cur.point) < p.distanceSquaredTo(champ))
                champ = cur.point;
        
            if (shouldCheck(cur.point, p, lvl, p.distanceSquaredTo(champ)))
                    champ = evalNeighbor(cur.left, p, champ, lvl+1);
        }
        
        return champ;
    }
    // compares distances between a query point and a hyperplane with radius of hypersphere
    private boolean shouldCheck(Point2D cur, Point2D p, int lvl, double dist) {
        double temp = p.y() - cur.y();
        if (lvl % 2 == 0) 
            temp = p.x() - cur.x();
        temp *= temp;
        return temp < dist;        
    }
}
