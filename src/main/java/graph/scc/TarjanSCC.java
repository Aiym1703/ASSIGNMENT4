package graph.scc;
import utils.Metrics;
import java.util.*;
/**
 * Tarjan's algorithm for Strongly Connected Components
 * Uses a stack, discovery times, and low-link values
 */
public class TarjanSCC {
    private int time;
    private final int[] low, disc;
    private final boolean[] onStack;
    private final Deque<Integer> stack;
    private final List<List<Integer>> sccs;
    private final List<List<Integer>> adj;
    private final Metrics metrics;

    public TarjanSCC(List<List<Integer>> adj, Metrics metrics) {
        this.adj = adj;
        this.metrics = metrics;
        int n = adj.size();
        time = 0;
        low = new int[n];
        disc = new int[n];
        onStack = new boolean[n];
        stack = new ArrayDeque<>();
        sccs = new ArrayList<>();
        Arrays.fill(disc, -1);
        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) dfs(i);
        }
    }
    private void dfs(int u) {
        metrics.dfsCalls++;
        disc[u] = low[u] = time++;
        stack.push(u);
        onStack[u] = true;
        for (int v : adj.get(u)) {
            metrics.edgesVisited++;
            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
        // root of an SCC
        if (low[u] == disc[u]) {
            List<Integer> component = new ArrayList<>();
            int v;
            do {
                v = stack.pop();
                onStack[v] = false;
                component.add(v);
            } while (u != v);
            sccs.add(component);
        }
    }
    public List<List<Integer>> getSCCs() {
        return sccs;
    }
}
