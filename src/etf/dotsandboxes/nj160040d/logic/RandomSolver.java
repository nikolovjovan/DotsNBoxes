package etf.dotsandboxes.nj160040d.logic;

public class RandomSolver implements Solver {

    AIPlayer player;

    public RandomSolver(AIPlayer player) {
        this.player = player;
    }

    @Override
    public Edge getNextMove() {
        return null;
    }
}