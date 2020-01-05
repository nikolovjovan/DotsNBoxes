package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

public class AlphaBetaSolver implements Solver {

    Game game;
    AIPlayer player;

    public AlphaBetaSolver(AIPlayer player) {
        this.player = player;
        this.game = player.game;
    }

    @Override
    public Edge getNextMove() {
        if (game.getBoard().numberOfAvailableMoves == 0) return new Edge();
        // TODO: Remove this placeholder and implement the method
        return game.getBoard().getAvailableMoves().get(0);
    }
}