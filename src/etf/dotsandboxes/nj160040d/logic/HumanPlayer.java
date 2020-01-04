package etf.dotsandboxes.nj160040d.logic;

public class HumanPlayer extends Player {

    Edge nextMove;

    public HumanPlayer(String name, byte colorValue) {
        super(name, colorValue);
        this.nextMove = new Edge();
    }

    @Override
    public Edge getNextMove() { return nextMove; }
    public void setNextMove(Edge nextMove) { this.nextMove.copy(nextMove); }
}