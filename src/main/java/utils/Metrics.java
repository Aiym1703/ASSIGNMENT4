package utils;
/** Tracks execution metrics and timings across algorithms. */
public class Metrics {
    private long startTime;
    private long endTime;
    public int dfsCalls = 0;
    public int edgesVisited = 0;  // DFS edge traversals (diagnostic)
    public int queuePushes = 0;
    public int queuePops = 0;
    public int relaxations = 0;
    public void start() { startTime = System.nanoTime(); }
    public void stop()  { endTime = System.nanoTime(); }

    public long getElapsedMillis() { return (endTime - startTime) / 1_000_000; }

    /** Precise milliseconds with decimals */
    public double getElapsedMillisDouble() { return (endTime - startTime) / 1_000_000.0; }

    public long getElapsedMicros() { return (endTime - startTime) / 1_000; }
    public void reset() {
        dfsCalls = edgesVisited = queuePushes = queuePops = relaxations = 0;
        startTime = endTime = 0;
    }
}
