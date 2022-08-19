
public class FlowEdge {
    private final int v, w;
    private final double capacity;
    private double  flow;
    
    public FlowEdge(int v, int w, double capacity) {
        this.v = v; this.w = w;
        this.capacity = capacity;
    }
    
    public int from()        { return v; }
    
    public int to()          { return w; }
    
    public double capacity() { return capacity; }
    
    public double flow()     { return flow; }
    
    // another endpoint of the edge
    public int other(int v) {
        if (v == this.v)
            return this.w;
        return this.v;
    }
    // residual capacity: how much we can add until forward edge is full, or how much to take until backward edge is emoty
    public double residualCapacityTo(int v) {
        if (v == this.v)
            return flow;
        return capacity - flow;
    }
    
    public void addResidualFlowTo(int v, double delta) {
        if (v == this.v)
            flow -= delta;
        else
            flow += delta;
    }
}
