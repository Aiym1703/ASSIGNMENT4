package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Loads directed weighted graphs from data/tasks.json
 */
public class GraphLoader {
    public static List<Graph> loadGraphs(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(path));
        ArrayNode graphs = (ArrayNode) root.get("graphs");

        List<Graph> allGraphs = new ArrayList<>();

        for (JsonNode gNode : graphs) {
            int n = gNode.get("n").asInt();
            int src = gNode.get("source").asInt();
            ArrayNode edges = (ArrayNode) gNode.get("edges");

            Graph g = new Graph(n, src);
            for (JsonNode e : edges) {
                int u = e.get("u").asInt();
                int v = e.get("v").asInt();
                int w = e.get("w").asInt();
                g.addEdge(u, v, w);
            }
            allGraphs.add(g);
        }
        return allGraphs;
    }
}
