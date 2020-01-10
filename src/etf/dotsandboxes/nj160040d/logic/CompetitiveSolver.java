package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

public class CompetitiveSolver implements Solver {

    private Game game;
    private AIPlayer player;

    public CompetitiveSolver(AIPlayer player) {
        this.player = player;
        this.game = player.game;
    }

    @Override
    public Edge getNextMove() {
        if (game.getState().getNumberOfAvailableMoves() == 0) return new Edge();
        // TODO: Remove this placeholder and implement the method
        return game.getState().getAvailableMoves().get(0);
    }
}