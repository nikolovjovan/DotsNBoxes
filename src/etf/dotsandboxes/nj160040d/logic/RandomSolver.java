package etf.dotsandboxes.nj160040d.logic;

import java.util.List;

public class RandomSolver implements Solver {

    private AIPlayer player;

    public RandomSolver(AIPlayer player) {
        this.player = player;
    }

    @Override
    public Edge getNextMove() {
        if (player.game.getState().getNumberOfAvailableMoves() == 0) return new Edge();
        List<Edge> availableMoves = player.game.getState().getAvailableMoves();
        for (Edge move : availableMoves) if (player.game.getState().addsNthEdge(move, 4)) return move;
        return availableMoves.get((int) Math.floor(Math.random() * availableMoves.size()));
    }
}