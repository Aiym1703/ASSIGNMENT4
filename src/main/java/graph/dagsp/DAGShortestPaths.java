package graph.dagsp;
import utils.Metrics;
import java.util.*;
/**
 * Single-source shortest paths (edge weights) and longest paths  on a DAG,
 * processing vertices in a provided topological order
 */
public class DAGShortestPaths {
    private final int[] dist;
    private final int[] prev;
    public DAGShortestPaths(List<List<int[]>> adj, int source, List<Integer> topoOrder, Metrics metrics) {
        int n = adj.size();
        dist = new int[n];
        prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[source] = 0;
        for (int u : topoOrder) {
            if (dist[u] == Integer.MAX_VALUE) continue;
            for (int[] e : adj.get(u)) {
                int v = e[0], w = e[1];
                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    prev[v] = u;
                    metrics.relaxations++;
                }
            }
        }
    }

    public int[] getDistances() { return dist; }
    public List<Integer> reconstructPath(int target) {
        List<Integer> path = new ArrayList<>();
        if (target == -1 || dist[target] == Integer.MAX_VALUE) return path;
        for (int cur = target; cur != -1; cur = prev[cur]) path.add(cur);
        Collections.reverse(path);
        return path;
    }
    // ---------- Longest (Critical) Path via max-DP ----------
    public static int[] longestPaths(List<List<int[]>> adj, List<Integer> topoOrder, int source) {
        int n = adj.size();
        int[] d = new int[n];
        Arrays.fill(d, Integer.MIN_VALUE);
        d[source] = 0;
        for (int u : topoOrder) {
            if (d[u] == Integer.MIN_VALUE) continue;
            for (int[] e : adj.get(u)) {
                int v = e[0], w = e[1];
                if (d[u] + w > d[v]) d[v] = d[u] + w;
            }
        }
        return d;
    }
    public static List<Integer> reconstructLongestPath(
            List<List<int[]>> adj, List<Integer> topoOrder, int source, int target, int[] dist) {
        List<Integer> path = new ArrayList<>();
        if (target == -1 || dist[target] == Integer.MIN_VALUE) return path;
        int cur = target;
        path.add(cur);
        while (cur != source) {
            boolean stepped = false;
            for (int u = 0; u < adj.size() && !stepped; u++) {
                for (int[] e : adj.get(u)) {
                    if (e[0] == cur && dist[u] != Integer.MIN_VALUE && dist[u] + e[1] == dist[cur]) {
                        cur = u;
                        path.add(u);
                        stepped = true;
                        break;
                    }
                }
            }
            if (!stepped) break;
        }
        Collections.reverse(path);
        return path;
    }
}
