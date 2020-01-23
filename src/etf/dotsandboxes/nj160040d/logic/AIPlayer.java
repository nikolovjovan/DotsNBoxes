package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

import java.util.List;

public class AIPlayer extends Player {

    public enum Difficulty {
        BEGINNER, ADVANCED, COMPETITIVE;

        @Override
        public String toString() {
            switch (this) {
                case BEGINNER: return "Beginner";
                case ADVANCED: return "Advanced";
                case COMPETITIVE: return "Competitive";
                default: return "Invalid Difficulty";
            }
        }

        public static Difficulty fromString(String s) {
            switch (s) {
                case "Beginner": return BEGINNER;
                case "Advanced": return ADVANCED;
                case "Competitive": return COMPETITIVE;
                default: return null;
            }
        }
    }

    private int maxDepth;
    private Solver solver;
    private List<Node> heuristics;

    public AIPlayer(Game game, String name, byte colorValue, String difficulty, int maxDepth) {
        super(game, Type.AI, name, colorValue);
        Difficulty difficulty1 = Difficulty.fromString(difficulty);
        this.maxDepth = maxDepth;
        if (difficulty1 == null) throw new RuntimeException("AI Player difficulty: " + difficulty + " is invalid!");
        switch (difficulty1) {
            case BEGINNER: {
                this.solver = new RandomSolver(this);
                break;
            }
            case ADVANCED: {
                this.solver = new AlphaBetaSolver(this);
                break;
            }
            case COMPETITIVE: {
                this.solver = new CompetitiveSolver(this);
                break;
            }
        }
    }

    public int getMaxDepth() { return maxDepth; }

    // TODO: Remove
    public void setMaxDepth(int maxDepth) { this.maxDepth = maxDepth; }

    public List<Node> getHeuristics() { return heuristics; }
    public void setHeuristics(List<Node> heuristics) { this.heuristics = heuristics; }

    public long computeNextMove() {
        long startTime = System.nanoTime();
        nextMove = solver.getNextMove();
        long endTime = System.nanoTime() - startTime;
        game.playerDone();
        return endTime;
    }
}