package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

public class HumanPlayer extends Player {

    Edge nextMove;

    public HumanPlayer(Game game, String name, byte colorValue) {
        super(game, Type.HUMAN, name, colorValue);
        this.nextMove = new Edge();
    }

    @Override
    public Edge getNextMove() { return nextMove; }
    public void setNextMove(Edge nextMove) { this.nextMove.copy(nextMove); }
}