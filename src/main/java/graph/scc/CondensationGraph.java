package graph.scc;
import java.util.*;
/**
 * represents the condensation graph (DAG) built after SCC compression
 * each SCC is represented as a single vertex, and edges connect components
 */
public class CondensationGraph {
    private final List<List<Integer>> dag;
    private final int[] compId;
    public CondensationGraph(List<List<Integer>> adj, List<List<Integer>> sccs) {
        int n = adj.size();
        compId = new int[n];

        // mapping each vertex to its SCC component
        for (int i = 0; i < sccs.size(); i++) {
            for (int node : sccs.get(i)) {
                compId[node] = i;
            }
        }

        // initializes DAG adjacency list
        dag = new ArrayList<>();
        for (int i = 0; i < sccs.size(); i++) {
            dag.add(new ArrayList<>());
        }

        // adding edges between scc
        Set<String> added = new HashSet<>();
        for (int u = 0; u < n; u++) {
            for (int v : adj.get(u)) {
                int cu = compId[u];
                int cv = compId[v];
                if (cu != cv && added.add(cu + "-" + cv)) {
                    dag.get(cu).add(cv);
                }
            }
        }
    }

    public List<List<Integer>> getDAG() {
        return dag;
    }

    public int[] getCompId() {
        return compId;
    }

    public void print() {
        System.out.println("Condensation Graph (DAG of SCCs):");
        for (int i = 0; i < dag.size(); i++) {
            System.out.println("Component " + i + " -> " + dag.get(i));
        }
    }
}
