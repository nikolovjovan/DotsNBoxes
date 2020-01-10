package etf.dotsandboxes.nj160040d.logic;

public class Pair implements Comparable<Pair> {
    private Edge move;
    private int heuristic;

    public Pair(Edge move, int heuristic) {
        this.move = move;
        this.heuristic = heuristic;
    }

    public Edge getMove() { return move; }
    public void setMove(Edge move) { this.move = move; }

    public int getHeuristic() { return heuristic; }
    public void setHeuristic(int heuristic) { this.heuristic = heuristic; }

    @Override
    public int compareTo(Pair pair) {
        return heuristic - pair.heuristic;
    }
}