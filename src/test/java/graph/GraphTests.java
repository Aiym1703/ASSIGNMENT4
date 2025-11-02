package graph;

import graph.dagsp.DAGShortestPaths;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSortKahn;
import org.junit.jupiter.api.Test;
import utils.Metrics;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * graph tests for algorithms, check is everything pasts those tests
 */

public class GraphTests {

    @Test
    void testSingleSCC() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());
        adj.get(0).add(1); adj.get(1).add(2); adj.get(2).add(0);
        TarjanSCC scc = new TarjanSCC(adj, new Metrics());
        assertEquals(1, scc.getSCCs().size());
        assertEquals(3, scc.getSCCs().get(0).size());
    }

    @Test
    void testTopologicalSort() {
        List<List<Integer>> adj = Arrays.asList(
                Arrays.asList(1, 2),
                Arrays.asList(3),
                Arrays.asList(3),
                Collections.emptyList()
        );
        TopologicalSortKahn topo = new TopologicalSortKahn(adj, new Metrics());
        List<Integer> order = topo.getTopologicalOrder();
        assertEquals(4, order.size());
        assertTrue(order.indexOf(0) < order.indexOf(3));
    }

    @Test
    void testShortestPathOnCond() {
        // simple condensation DAG
        List<List<int[]>> wCond = new ArrayList<>();
        for (int i = 0; i < 4; i++) wCond.add(new ArrayList<>());
        wCond.get(0).add(new int[]{1,2});
        wCond.get(0).add(new int[]{2,3});
        wCond.get(1).add(new int[]{3,4});
        wCond.get(2).add(new int[]{3,1});
        List<Integer> topoOrder = Arrays.asList(0,1,2,3);

        DAGShortestPaths sp = new DAGShortestPaths(wCond, 0, topoOrder, new Metrics());
        assertEquals(0, sp.getDistances()[0]);
        assertEquals(3, sp.getDistances()[2]);
        assertEquals(4, sp.getDistances()[3]); // 0->2->3 = 3+1
    }
}
