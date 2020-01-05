package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

public abstract class Player implements Solver {

    public enum Type { HUMAN, AI }

    protected Game game;
    protected Type type;
    protected String name;
    protected byte colorValue;
    protected int score;

    protected Player(Game game, Type type, String name, byte colorValue) {
        this.game = game;
        this.type = type;
        this.name = name;
        this.colorValue = colorValue;
        this.score = 0;
    }

    public Type getType() { return type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public byte getColorValue() { return colorValue; }
    public void setColorValue(byte colorValue) { this.colorValue = colorValue; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public abstract Edge getNextMove();
}