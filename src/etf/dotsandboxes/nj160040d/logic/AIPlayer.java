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

        public Difficulty fromString(String s) {
            switch (s) {
                case "Beginner": return BEGINNER;
                case "Advanced": return ADVANCED;
                case "Competitive": return COMPETITIVE;
                default: return null;
            }
        }
    }

    Difficulty difficulty;
    //Solver solver;

    public AIPlayer(Difficulty difficulty) {
        this.difficulty = difficulty;
        /*
        switch (difficulty) {
            case BEGINNER: this.solver = new RandomSolver();
            case ADVANCED: this.solver = new AlphaBetaSolver();
            case COMPETITIVE: this.solver = new CompetitiveSolver();
        }
        */
    }
}