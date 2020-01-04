package etf.dotsandboxes.nj160040d.logic;

public abstract class Player implements Solver {

    protected String name;
    protected byte colorValue;
    protected int score;

    protected Player(String name, byte colorValue) {
        this.name = name;
        this.colorValue = colorValue;
        this.score = 0;
    }

    public abstract Edge getNextMove();
}