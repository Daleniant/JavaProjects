import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;

public class FlowNetwork {
    private final int V;
    private Bag<FlowEdge>[] adj;
    
    @SuppressWarnings("unchecked")
    public FlowNetwork(int V) {
        this.V = V;
        adj = (Bag<FlowEdge>[]) new Bag[V];
        for (int i = 0; i < V; i++)
            adj[i] = new Bag<FlowEdge>();
    }
    
    @SuppressWarnings("unchecked")
    public FlowNetwork(In in) {
        this.V = in.readInt();
        adj = (Bag<FlowEdge>[]) new Bag[V];
        for (int i = 0; i < V; i++)
            adj[i] = new Bag<FlowEdge>();
        
        int E = in.readInt();
        for (int i = 0; i < E; i++)
            addEdge(new FlowEdge(in.readInt(), in.readInt(), in.readDouble()));
    }
    
    public void addEdge(FlowEdge e) {
        adj[e.from()].add(e);  // add backward edge
        adj[e.to()].add(e);    // add forward edge
    }
    
    public Iterable<FlowEdge> adj(int v){
        return adj[v];
    }
    
    public int V() {
        return V;
    }
}
