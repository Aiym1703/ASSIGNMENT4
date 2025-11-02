package main;
import graph.scc.TarjanSCC;
import graph.topo.TopologicalSortKahn;
import graph.dagsp.DAGShortestPaths;
import utils.Graph;
import utils.GraphLoader;
import utils.Metrics;

import java.io.FileWriter;
import java.util.*;

/**
 * 1) SCC , i used Tarjan
 * 2) Condensation DAG
 * 3) Topological sort with Kahn on condensation DAG
 */

public class Main {
    private static String compPathAsOriginals(List<Integer> compPath, List<List<Integer>> sccs) {
        if (compPath == null || compPath.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < compPath.size(); i++) {
            int comp = compPath.get(i);
            List<Integer> nodes = new ArrayList<>(sccs.get(comp));
            Collections.sort(nodes);
            sb.append("{");
            for (int j = 0; j < nodes.size(); j++) {
                sb.append(nodes.get(j));
                if (j + 1 < nodes.size()) sb.append(" ");
            }
            sb.append("}");
            if (i + 1 < compPath.size()) sb.append("->");
        }
        return sb.toString();
    }

    /** all distances from source component  to a compact CSV */
    private static String serializeAllDistances(int[] dist) {
        StringBuilder allDists = new StringBuilder("[");
        for (int i = 0; i < dist.length; i++) {
            if (i > 0) allDists.append(", ");
            allDists.append(dist[i] == Integer.MAX_VALUE ? "NA" : Integer.toString(dist[i]));
        }
        allDists.append("]");
        return allDists.toString();
    }

    public static void main(String[] args) {
        try {
            List<Graph> graphs = GraphLoader.loadGraphs("data/tasks.json");

            try (FileWriter out = new FileWriter("data/results.csv")) {
                out.write("graph_id,n,m,scc_count,scc_components,topo_order,derived_order,"
                        + "shortest_path,shortest_dist_to_target,all_distances_from_source,"
                        + "critical_path,longest_path_len,"
                        + "dfs_calls,edges_visited_dfs,queue_pushes,queue_pops,relaxations,time_ms,"
                        + "weight_model,source_comp,target_comp,dag_n,dag_m,remarks\n");
                int graphId = 1;
                for (Graph g : graphs) {
                    Metrics metrics = new Metrics();
                    metrics.start();
                    int n = g.getN();
                    List<List<Integer>> adj = g.getAdj();

                    // 1) SCC
                    TarjanSCC tarjan = new TarjanSCC(adj, metrics);
                    List<List<Integer>> sccs = tarjan.getSCCs();

                    StringBuilder sccBuilder = new StringBuilder();
                    for (int i = 0; i < sccs.size(); i++) {
                        List<Integer> comp = new ArrayList<>(sccs.get(i));
                        Collections.sort(comp);
                        sccBuilder.append("(").append(comp.size()).append(") ");
                        for (int v : comp) sccBuilder.append(v).append(" ");
                        if (sccBuilder.charAt(sccBuilder.length() - 1) == ' ')
                            sccBuilder.deleteCharAt(sccBuilder.length() - 1);
                        if (i + 1 < sccs.size()) sccBuilder.append(" ; ");
                    }

                    // 2) Condensation DAG
                    int[] compId = new int[n];
                    for (int i = 0; i < sccs.size(); i++) {
                        for (int node : sccs.get(i)) compId[node] = i;
                    }
                    int C = sccs.size();

                    List<List<Integer>> dag = new ArrayList<>();
                    for (int i = 0; i < C; i++) dag.add(new ArrayList<>());
                    Set<Long> seen = new HashSet<>();

                    List<List<int[]>> wCond = new ArrayList<>();
                    for (int i = 0; i < C; i++) wCond.add(new ArrayList<>());
                    Map<Long, Integer> minW = new HashMap<>();

                    for (int u = 0; u < n; u++) {
                        for (int[] e : g.getWeightedAdj().get(u)) {
                            int v = e[0], w = e[1];
                            int cu = compId[u], cv = compId[v];
                            if (cu == cv) continue;
                            long key = (((long) cu) << 32) | (cv & 0xffffffffL);
                            if (seen.add(key)) dag.get(cu).add(cv);
                            minW.merge(key, w, Math::min);
                        }
                    }
                    for (Map.Entry<Long, Integer> en : minW.entrySet()) {
                        int cu = (int) (en.getKey() >> 32);
                        int cv = (int) (en.getKey() & 0xffffffffL);
                        wCond.get(cu).add(new int[]{cv, en.getValue()});
                    }

                    // 3) topological sorting  on condensation DAG
                    TopologicalSortKahn topo = new TopologicalSortKahn(dag, metrics);
                    List<Integer> topoOrder = topo.getTopologicalOrder();
                    boolean topoValid = topoOrder.size() == C;

                    // derived ordering of original nodes by component topo-order
                    StringBuilder derivedOrder = new StringBuilder();
                    for (int comp : topoOrder) {
                        List<Integer> nodes = new ArrayList<>(sccs.get(comp));
                        Collections.sort(nodes);
                        derivedOrder.append("{");
                        for (int v : nodes) derivedOrder.append(v).append(" ");
                        if (derivedOrder.charAt(derivedOrder.length() - 1) == ' ')
                            derivedOrder.deleteCharAt(derivedOrder.length() - 1);
                        derivedOrder.append("} ");
                    }

                    // 4) shortest and longest paths on condensation DAG
                    int srcComp = compId[g.getSource()];

                    // SSSP
                    DAGShortestPaths sp = new DAGShortestPaths(wCond, srcComp, topoOrder, metrics);
                    int[] dist = sp.getDistances();
                    String allDists = serializeAllDistances(dist);

                    // pick best reachable target for a sample shortest path
                    int bestTarget = -1;
                    int bestDist = Integer.MAX_VALUE;
                    for (int c = 0; c < dist.length; c++) {
                        if (c != srcComp && dist[c] < bestDist) {
                            bestDist = dist[c];
                            bestTarget = c;
                        }
                    }
                    List<Integer> shortestCompPath = sp.reconstructPath(bestTarget);

                    // Longest critical path
                    int[] longDist = DAGShortestPaths.longestPaths(wCond, topoOrder, srcComp);
                    int maxLen = Integer.MIN_VALUE, endComp = -1;
                    for (int c = 0; c < longDist.length; c++) {
                        if (longDist[c] > maxLen) { maxLen = longDist[c]; endComp = c; }
                    }
                    List<Integer> criticalCompPath = DAGShortestPaths.reconstructLongestPath(
                            wCond, topoOrder, srcComp, endComp, longDist);

                    metrics.stop();
                    String remark = topoValid ? "ok" : "cyclic";
                    String weightModel = "edge";
                    int dagN = C;
                    int dagM = seen.size();

                    // 5) CSV writer
                    out.write(graphId + "," + n + "," + g.getEdges().size() + ","
                            + sccs.size() + ",\"" + sccBuilder + "\",\"" + topoOrder + "\",\""
                            + derivedOrder + "\",\"" + compPathAsOriginals(shortestCompPath, sccs) + "\","
                            + (bestTarget == -1 ? "NA" : Integer.toString(bestDist)) + ",\""
                            + allDists + "\",\"" + compPathAsOriginals(criticalCompPath, sccs) + "\","
                            + (endComp == -1 ? "NA" : Integer.toString(maxLen)) + ","
                            + metrics.dfsCalls + "," + metrics.edgesVisited + ","
                            + metrics.queuePushes + "," + metrics.queuePops + ","
                            + metrics.relaxations + ","
                            + String.format(java.util.Locale.US, "%.3f", metrics.getElapsedMillisDouble()) + ","
                            + weightModel + "," + srcComp + ","
                            + (bestTarget == -1 ? "NA" : Integer.toString(bestTarget)) + ","
                            + dagN + "," + dagM + ","
                            + remark + "\n");

                    graphId++;
                }
            }
            System.out.println("All results saved to data/results.csv");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
