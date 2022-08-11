import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

// Collection of algorithms to work with WordNet
// Project mark - 97

public class SAP {
    private final Digraph g;
    
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) throw new IllegalArgumentException();
        // to make immutable copy reverse original graph 2 times
        g = G.reverse().reverse();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v < 0 || w < 0) throw new IllegalArgumentException();
        if (v >= g.V() || w >= g.V()) throw new IllegalArgumentException();
        if (v == w) return 0;
        /* run BFS from first point and fill the distances to it
         * then run BFS from second point, and whenever we encounter point previously visited
         * in 1st BFS, compare the sum of distances from 2 points and update the holder 
         */
        int[] distToV, distToW;
        distToV = new int[g.V()]; distToW = new int[g.V()];
        for (int i = 0; i < g.V(); i++) {
            distToV[i] = -1; distToW[i] = -1;
        }
        
        Queue<Integer> q1, q2;
        q1 = new Queue<Integer>(); q2 = new Queue<Integer>();
        q1.enqueue(v); q2.enqueue(w);
        distToV[v] = 0; distToW[w] = 0;
        
        while (!q1.isEmpty()) {
            int temp = q1.dequeue();
            for (int a : g.adj(temp)) {
                if (distToV[a] == -1) {
                    distToV[a] = distToV[temp] + 1;
                    q1.enqueue(a);
                }
            }
        }
        
        int minL = -1;
        // check if this element is directly connected to 1st element as well
        if (distToV[w] != -1)
            if (distToV[w] < minL || minL == -1)
                minL = distToV[w] + distToW[w];
            
        while (!q2.isEmpty()) {
            int temp = q2.dequeue();
            for (int a : g.adj(temp)) {
                if (distToW[a] == -1) {
                    distToW[a] = distToW[temp] + 1;
                    q2.enqueue(a);
                }
                if (distToV[a] != -1)
                    if (distToV[a] + distToW[a] < minL || minL == -1)
                        minL = distToV[a] + distToW[a];
            }
        }
        
        return minL;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v < 0 || w < 0) throw new IllegalArgumentException();
        if (v >= g.V() || w >= g.V()) throw new IllegalArgumentException();
        if (v == w) return v;
        
        // same as length, but now also hold the common ancestor on the shortest path
        int[] distToV, distToW;
        distToV = new int[g.V()]; distToW = new int[g.V()];
        for (int i = 0; i < g.V(); i++) {
            distToV[i] = -1; distToW[i] = -1;
        }
        
        Queue<Integer> q1, q2;
        q1 = new Queue<Integer>(); q2 = new Queue<Integer>();
        q1.enqueue(v); q2.enqueue(w);
        distToV[v] = 0; distToW[w] = 0;
        
        while (!q1.isEmpty()) {
            int temp = q1.dequeue();
            for (int a : g.adj(temp)) {
                if (distToV[a] == -1) {
                    distToV[a] = distToV[temp] + 1;
                    q1.enqueue(a);
                }
            }
        }
        
        int minL = -1;
        int anc = -1;
        // check if this element is directly connected to 1st element as well
        if (distToV[w] != -1) {
            if (distToV[w] < minL || minL == -1) {
                minL = distToV[w] + distToW[w];
                anc = w;
            }
        }
        
        while (!q2.isEmpty()) {
            int temp = q2.dequeue();
            for (int a : g.adj(temp)) {
                if (distToW[a] == -1) {
                    distToW[a] = distToW[temp] + 1;
                    q2.enqueue(a);
                }
                if (distToV[a] != -1) {
                    if (distToV[a] + distToW[a] < minL || minL == -1) {
                        minL = distToV[a] + distToW[a];
                        anc = a;
                    }
                }
            }
        }
        
        return anc;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();
        /* 
         * First run BFS on all elements of the 1st group with the next tweak:
         *  if, while running BFS, we find an element visited by another value from 1st group, 
         *  we should run BFS further for that node iff the distance
         *  to this element is closer for the current value
         * Then run BFS for all elements of the 2nd group with the next tweak:
         *  if, while running BFS, we find an element that was visited by group 1, sum the distance
         *  to that element for group 1 and current value, and if it is less than current minimum,
         *  then update the minimum. Treat elements of own group same was as in BFS for 1st group
         */
        int[] distToV, distToW;
        distToV = new int[g.V()]; distToW = new int[g.V()];
        
        for (int i = 0; i < g.V(); i++) {
            distToV[i] = -1; distToW[i] = -1;
        }
        
        for (int a : v) {
            if (a < 0 || a >= g.V()) throw new IllegalArgumentException();
            
            Queue<Integer> q1 = new Queue<Integer>();
            q1.enqueue(a);
            distToV[a] = 0;
            
            while (!q1.isEmpty()) {
                int temp = q1.dequeue();
                
                for (int d : g.adj(temp)) {
                    if (distToV[d] == -1 || distToV[temp] + 1 < distToV[d]) {
                        distToV[d] = distToV[temp] + 1;
                        q1.enqueue(d);
                    }
                }
            }            
        }
        
        int minLength = -1;
        for (int b : w) {
            if (b < 0 || b >= g.V()) throw new IllegalArgumentException();
            
            distToW[b] = 0;
            // check if this element is directly connected to 1st group as well
            if (distToV[b] != -1)
                if (distToV[b] < minLength || minLength == -1)
                    minLength = distToV[b] + distToW[b];
                
            Queue<Integer> q2 = new Queue<Integer>();
            q2.enqueue(b);
            
            while (!q2.isEmpty()) {
                int temp = q2.dequeue();
                
                for (int d : g.adj(temp)) {
                    if (distToW[d] == -1 || distToW[temp] + 1 < distToW[d]) {
                        distToW[d] = distToW[temp] + 1;
                        q2.enqueue(d);
                    }
                    if (distToV[d] != -1)
                        if (distToV[d] + distToW[d] < minLength || minLength == -1)
                            minLength = distToV[d] + distToW[d];
                }
            }
        }
        return minLength;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();
        /*
         * Similar to finding shortest length in group of vertices
         * but this time update ancestor whenever minLength is updated
         */ 
        int[] distToV, distToW;
        distToV = new int[g.V()]; distToW = new int[g.V()];
        
        for (int i = 0; i < g.V(); i++) {
            distToV[i] = -1; distToW[i] = -1;
        }
        
        for (int a : v) {
            if (a < 0 || a >= g.V()) throw new IllegalArgumentException();
            
            Queue<Integer> q1 = new Queue<Integer>();
            q1.enqueue(a);
            distToV[a] = 0;
            
            while (!q1.isEmpty()) {
                int temp = q1.dequeue();
                
                for (int d : g.adj(temp)) {
                    if (distToV[d] == -1 || distToV[temp] + 1 < distToV[d]) {
                        distToV[d] = distToV[temp] + 1;
                        q1.enqueue(d);
                    }
                }
            }            
        }
        
        int minLength = -1, anc = -1;
        for (int b : w) {
            if (b < 0 || b >= g.V()) throw new IllegalArgumentException();
            
            distToW[b] = 0;
            // check if this element is directly connected to 1st group as well
            if (distToV[b] != -1) {
                if (distToV[b] < minLength || minLength == -1) {
                    minLength = distToV[b] + distToW[b];
                    anc = b;
                }
            }
            Queue<Integer> q2 = new Queue<Integer>();
            q2.enqueue(b);
            
            
            while (!q2.isEmpty()) {
                int temp = q2.dequeue();
                
                for (int d : g.adj(temp)) {
                    if (distToW[d] == -1 || distToW[temp] + 1 < distToW[d]) {
                        distToW[d] = distToW[temp] + 1;
                        q2.enqueue(d);
                    }
                    if (distToV[d] != -1) {
                        if (distToV[d] + distToW[d] < minLength || minLength == -1) {
                            minLength = distToV[d] + distToW[d];
                            anc = d;
                        }
                    }
                }
            }
        }
        return anc;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
