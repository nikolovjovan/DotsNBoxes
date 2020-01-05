package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

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

    Difficulty difficulty;
    int treeDepth;
    Solver solver;

    public AIPlayer(Game game, String name, byte colorValue, String difficulty, int treeDepth) {
        super(game, Type.AI, name, colorValue);
        this.difficulty = Difficulty.fromString(difficulty);
        this.treeDepth = treeDepth;
        if (this.difficulty == null) throw new RuntimeException("AI Player difficulty: " + difficulty + " is invalid!");
        switch (this.difficulty) {
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

    public void computeNextMove() { game.playerDone(); }

    @Override
    public Edge getNextMove() { return solver.getNextMove(); }
}