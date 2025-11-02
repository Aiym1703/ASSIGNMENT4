# Assignment 4 – Smart City Scheduling
### Pangerey Aiym
### SE-2422

# Main report is Assignment_4_Aiym
Used Excel to create the tables and lectures to understand the topic.
## Algorithms Implemented:
- SCC: Tarjan’s algorithm 
- Condensation Graph: components compressed into a DAG. Parallel edges collapsed by min weight.
- Topological Sort: Kahn’s algorithm.
- Shortest Paths on DAG: dynamic programming over topological order from a source component.
- Critical Path :max-DP over topological order
<img width="313" height="187" alt="image" src="https://github.com/user-attachments/assets/4ca7fd31-180f-462f-8be0-4d211e2f379c" />

- Graphs 1–3: Small to medium graphs, some containing small cycles or tri-components. 
- Graphs 4–6: Fully acyclic DAGs with moderate density. 
- Graphs 7–9: Larger DAGs up to 40 nodes and 46 edges, demonstrating scalability of the algorithms. 
- Iplemented graphs by using EXCEL. All datasets used edge-weighted models to evaluate shortest and longest paths across components



### Dataset Performance Summary

| Dataset   | Vertices | Edges | SCC Count | SCC Time (ms) | Topo Push | Topo Pop | Shortest (ms) | Longest (ms) |
|------------|-----------|--------|------------|----------------|------------|-----------|----------------|----------------|
| **graph_1** | 8  | 9  | 6  | 0.102 | 6 | 6 | 0.091 | 0.102 |
| **graph_2** | 9  | 9  | 9  | 0.091 | 9 | 9 | 0.094 | 0.103 |
| **graph_3** | 10 | 10 | 8  | 0.071 | 8 | 8 | 0.071 | 0.078 |
| **graph_4** | 12 | 14 | 12 | 0.094 | 12 | 12 | 0.094 | 0.098 |
| **graph_5** | 16 | 21 | 16 | 0.127 | 16 | 16 | 0.127 | 0.131 |
| **graph_6** | 18 | 21 | 18 | 0.754 | 18 | 18 | 0.162 | 0.173 |
| **graph_7** | 20 | 29 | 20 | 0.173 | 20 | 20 | 0.173 | 0.231 |
| **graph_8** | 25 | 36 | 25 | 0.226 | 25 | 25 | 0.226 | 0.231 |
| **graph_9** | 40 | 46 | 40 | 0.353 | 40 | 40 | 0.353 | 0.367 |

**Observation:**  
Smaller graphs 1–4 executed almost instantly <0.1 ms, while larger ones 7–9 showed linear growth with size O(V + E).  
All algorithms maintained stable performance, even with increased vertices and edges.


###  Detailed Analysis

#### Tarjan’s SCC
- **Bottleneck:** Recursive DFS stack depth and edge scans on cyclic subgraphs.  
- **Structure effect:** In denser graphs Graphs 5–6, back-edges form multi-node SCCs. In sparse DAGs Graphs 2, 4, 8–9, scc_count=n.  
- **Evidence:** Graph 5: dfs_calls = 44 and Graph 6: dfs_calls = 45, while Graph 2 only has dfs_calls = 20.  

#### Kahn’s Topological Sort
- **Bottleneck:** Queue operations scale with in-degree after SCC compression.  
- **Structure effect:** Many small SCCs lead to near-linear queue traffic; higher dag_m increases pushes/pops.  
- **Evidence:** Graph 5: queue_pushes = 21 vs Graph 2:  queue_pushes = 9, both validated as _ok_.  

#### DAG Shortest Paths
- **Bottleneck:** Number of relaxations dag_m.  
- **Structure effect:** Sparse condensation graphs 8–9 have fewer relaxations and very fast completion < 0.37 ms.  
- **Evidence:** Graph 5: relaxations = 18, time_ms = 0.131 vs Graph 6: relaxations = 19, time_ms = 0.765.  

#### Critical Path (Longest Path)
- **Method:** Computed using max-DP over topo_order from source_comp.  
- **Observation:** Paths are reported as groups of original nodes per component. Lengths align closely with the aggregated edge weights and reveal the task chain with the maximum delay.  



### Algorithm Performance Summary/Theory vs Practice

| Algorithm                  | Theoretical Complexity | Observed Performance | Purpose                                               |
|-----------------------------|------------------------|----------------------|-------------------------------------------------------|
| **Tarjan SCC**              | O(V + E)               | 0.07 – 0.12 ms       | Detect strongly connected components and build DAG     |
| **Kahn Topological Sort**   | O(V + E)               | 0.01 – 0.09 ms       | Generate valid task order after SCC compression        |
| **DAG Shortest Path**       | O(V + E)               | 0.07 – 0.23 ms       | Compute minimal dependency cost for Smart City tasks   |
| **DAG Longest Path**        | O(V + E)               | 0.10 – 0.37 ms       | Identify critical path (maximum delay chain)           |

**Summary:**  
All algorithms run in linear time with respect to the number of vertices and edges.  
Even the largest graph 40 vertices, 46 edges completed under 0.4 ms, proving the implementation is scalable and efficient.



# How to Run

From your IDE run Main
