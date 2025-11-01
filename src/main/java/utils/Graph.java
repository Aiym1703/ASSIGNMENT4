package utils;
import java.util.*;
/** Simple directed weighted graph (adjacency lists + edge list) */
public class Graph {
    private final int n;
    private final int source;
    private final List<Edge> edges;
    private final List<List<Integer>> adj;
    private final List<List<int[]>> weightedAdj;
    public Graph(int n, int source) {
        this.n = n;
        this.source = source;
        this.edges = new ArrayList<>();
        this.adj = new ArrayList<>();
        this.weightedAdj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
            weightedAdj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int w) {
        edges.add(new Edge(u, v, w));
        adj.get(u).add(v);
        weightedAdj.get(u).add(new int[]{v, w});
    }

    public int getN() { return n; }
    public int getSource() { return source; }
    public List<Edge> getEdges() { return edges; }
    public List<List<Integer>> getAdj() { return adj; }
    public List<List<int[]>> getWeightedAdj() { return weightedAdj; }
}
