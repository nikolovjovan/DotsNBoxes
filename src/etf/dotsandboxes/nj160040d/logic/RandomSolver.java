package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

import java.util.List;

public class RandomSolver implements Solver {

    private Game game;
    private AIPlayer player;

    public RandomSolver(AIPlayer player) {
        this.player = player;
        this.game = player.game;
    }

    @Override
    public Edge getNextMove() {
        if (game.getState().getNumberOfAvailableMoves() == 0) return new Edge();
        List<Edge> availableMoves = game.getState().getAvailableMoves();
        for (Edge move : availableMoves) if (game.getState().addsNthEdge(move, 4)) return move;
        return availableMoves.get((int) Math.floor(Math.random() * availableMoves.size()));
    }
}