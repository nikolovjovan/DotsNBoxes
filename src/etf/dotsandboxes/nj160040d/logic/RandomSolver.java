package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

import java.util.List;

public class RandomSolver implements Solver {

    Game game;
    AIPlayer player;

    public RandomSolver(AIPlayer player) {
        this.player = player;
        this.game = player.game;
    }

    @Override
    public Edge getNextMove() {
        if (game.getBoard().numberOfAvailableMoves == 0) return new Edge();
        List<Edge> availableMoves = game.getBoard().getAvailableMoves();
        for (Edge move : availableMoves) if (game.getBoard().closesBox(move)) return move;
        return availableMoves.get((int) Math.floor(Math.random() * availableMoves.size()));
    }
}