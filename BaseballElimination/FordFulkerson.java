import edu.princeton.cs.algs4.Queue;

public class FordFulkerson {
    private boolean[] marked;
    private FlowEdge[] edgeTo;
    private double value;
    
    public FordFulkerson(FlowNetwork g, int s, int t) {        
        while (hasAugmentedPath(g, s, t)) {
            double bottleNeck = Double.POSITIVE_INFINITY;
            // compute the bottleneck capacity for some augmented path
            for (int v = t; v != s; v = edgeTo[v].other(v))
                bottleNeck = Math.min(bottleNeck, edgeTo[v].residualCapacityTo(v));
            
            for (int v = t; v != s; v = edgeTo[v].other(v))
                edgeTo[v].addResidualFlowTo(v, bottleNeck);
            
            value += bottleNeck;
        }
    }
    
    private boolean hasAugmentedPath(FlowNetwork g, int s, int t) {
        marked = new boolean[g.V()];
        edgeTo = new FlowEdge[g.V()];
        for (int i = 0; i < g.V(); i++) {
            marked[i] = false;
            edgeTo[i] = null;
        }
        marked[s] = true;
        
        Queue<Integer> q = new Queue<Integer>();
        q.enqueue(s);
        while (!q.isEmpty()) {
            int v = q.dequeue();
            
            for (FlowEdge e : g.adj(v)) {
                int w = e.other(v);
                if (e.residualCapacityTo(w) > 0 && !marked[w]) {
                    marked[w] = true;
                    edgeTo[w] = e;
                    q.enqueue(w);
                }
            }
        }
        
        return marked[t];
    }
    
    public double value() {
        return value;
    }
    // in other words - is v reachable from s in residual network
    public boolean inCut(int v) {
        return marked[v];
    }
    
    public static void main(String[] args) {
        
    }
}
