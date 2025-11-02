package graph.scc;
import org.junit.jupiter.api.Test;
import utils.Metrics;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * unit tests for Tarjanscc algorithm, 6 tests implemented
 */
public class TarjanSCCTest {

    private List<List<Integer>> buildAdj(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);
        return adj;
    }

    /** for small deterministic DAG */
    @Test
    public void testSimpleDAG_NoCycles() {
        Metrics metrics = new Metrics();
        List<List<Integer>> adj = buildAdj(4, new int[][]{
                {0, 1}, {1, 2}, {2, 3}
        });
        TarjanSCC tarjan = new TarjanSCC(adj, metrics);
        List<List<Integer>> sccs = tarjan.getSCCs();

        assertEquals(4, sccs.size(), "Each node should be its own SCC in a DAG");
    }

    /** simple cycle */
    @Test
    public void testSingleCycle() {
        Metrics metrics = new Metrics();
        List<List<Integer>> adj = buildAdj(3, new int[][]{
                {0, 1}, {1, 2}, {2, 0}
        });
        TarjanSCC tarjan = new TarjanSCC(adj, metrics);
        List<List<Integer>> sccs = tarjan.getSCCs();

        // Expect one SCC of size 3
        assertEquals(1, sccs.size());
        assertEquals(3, sccs.get(0).size());
    }

    /** two components with cycles */
    @Test
    public void testDisconnectedGraph() {
        Metrics metrics = new Metrics();
        List<List<Integer>> adj = buildAdj(6, new int[][]{
                {0, 1}, {1, 0}, // cycle A
                {2, 3}, {3, 2}, // cycle B
                {4, 5}          // single edge
        });
        TarjanSCC tarjan = new TarjanSCC(adj, metrics);
        List<List<Integer>> sccs = tarjan.getSCCs();

        assertEquals(4, sccs.size(), "Should detect two 2-cycles and two singletons");
    }

    /** edge cas no edges */
    @Test
    public void testSingleNodeGraph() {
        Metrics metrics = new Metrics();
        List<List<Integer>> adj = buildAdj(1, new int[][]{});
        TarjanSCC tarjan = new TarjanSCC(adj, metrics);
        List<List<Integer>> sccs = tarjan.getSCCs();

        assertEquals(1, sccs.size(), "Single vertex forms one SCC");
        assertEquals(List.of(0), sccs.get(0));
    }

    /** edge case */
    @Test
    public void testEmptyGraph() {
        Metrics metrics = new Metrics();
        List<List<Integer>> adj = new ArrayList<>();
        TarjanSCC tarjan = new TarjanSCC(adj, metrics);
        List<List<Integer>> sccs = tarjan.getSCCs();

        assertTrue(sccs.isEmpty(), "Empty graph should yield no SCCs");
    }

    /** partial cycles and chain */
    @Test
    public void testMixedGraph() {
        Metrics metrics = new Metrics();
        List<List<Integer>> adj = buildAdj(6, new int[][]{
                {0, 1}, {1, 0},
                {1, 2}, {2, 3},
                {4, 5}
        });
        TarjanSCC tarjan = new TarjanSCC(adj, metrics);
        List<List<Integer>> sccs = tarjan.getSCCs();

        assertTrue(sccs.size() >= 4);
    }
}
