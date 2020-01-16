package etf.dotsandboxes.nj160040d.logic;

public class Node implements Comparable<Node> {
    private Edge move;
    private State state;
    private int heuristic;

    public Node(Edge move, State state, int heuristic) {
        this.move = move;
        this.state = state;
        this.heuristic = heuristic;
    }

    public Node(Edge move, int heuristic) {
        this(move, null, heuristic);
    }

    public Edge getMove() { return move; }
    public void setMove(Edge move) { this.move = move; }

    public State getState() { return state; }

    public int getHeuristic() { return heuristic; }
    public void setHeuristic(int heuristic) { this.heuristic = heuristic; }

    @Override
    public int compareTo(Node node) {
        return heuristic - node.heuristic;
    }
}