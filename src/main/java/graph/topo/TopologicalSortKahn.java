package graph.topo;
import utils.Metrics;
import java.util.*;
/**
 * Kahn's algorithm for topological ordering on a DAG
 * Counts queue pushes/pops via Metrics
 */
public class TopologicalSortKahn {
    private final List<List<Integer>> adj;
    private final int[] inDegree;
    private final List<Integer> topoOrder;
    private final Metrics metrics;
    public TopologicalSortKahn(List<List<Integer>> adj, Metrics metrics) {
        this.adj = adj;
        this.metrics = metrics;
        int n = adj.size();
        inDegree = new int[n];
        topoOrder = new ArrayList<>();
        for (List<Integer> nbrs : adj) {
            for (int v : nbrs) inDegree[v]++;
        }
        Queue<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                q.offer(i);
                metrics.queuePushes++;
            }
        }

        while (!q.isEmpty()) {
            int u = q.poll();
            metrics.queuePops++;
            topoOrder.add(u);
            for (int v : adj.get(u)) {
                if (--inDegree[v] == 0) {
                    q.offer(v);
                    metrics.queuePushes++;
                }
            }
        }
    }

    public List<Integer> getTopologicalOrder() { return topoOrder; }
}
