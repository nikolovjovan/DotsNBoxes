package etf.dotsandboxes.nj160040d.logic;

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

    public AIPlayer(String name, byte colorValue, String difficulty, int treeDepth) {
        super(name, colorValue);
        this.difficulty = Difficulty.fromString(difficulty);
        this.treeDepth = treeDepth;
        switch (this.difficulty) {
            case BEGINNER: this.solver = new RandomSolver(this);
            case ADVANCED: this.solver = new AlphaBetaSolver(this);
            case COMPETITIVE: this.solver = new CompetitiveSolver(this);
        }
    }

    @Override
    public Edge getNextMove() { return solver.getNextMove(); }
}