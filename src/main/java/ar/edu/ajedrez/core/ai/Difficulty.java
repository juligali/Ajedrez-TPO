package ar.edu.ajedrez.core.ai;

public enum Difficulty {
    SUPER_EASY(0, 0, 0), EASY(1, 100, 300), MEDIUM(2, 1200, 600),
    HARD(3, 5000, 1200), EXTREME(4, 16000, 2200);
    private final int depth, nodes, milliseconds;
    Difficulty(int depth, int nodes, int milliseconds) { this.depth=depth; this.nodes=nodes; this.milliseconds=milliseconds; }
    public int getDepth() { return depth; }
    public int getNodes() { return nodes; }
    public int getMilliseconds() { return milliseconds; }
}
