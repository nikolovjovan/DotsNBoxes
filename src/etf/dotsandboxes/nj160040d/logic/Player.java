package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

public abstract class Player implements Solver {

    public enum Type { HUMAN, AI }

    protected Game game;
    protected Type type;
    protected String name;
    protected byte colorValue;
    protected Edge nextMove;

    protected Player(Game game, Type type, String name, byte colorValue) {
        this.game = game;
        this.type = type;
        this.name = name;
        this.colorValue = colorValue;
        this.nextMove = Edge.INVALID;
    }

    public Type getType() { return type; }
    public String getName() { return name; }
    public byte getColorValue() { return colorValue; }
    public Edge getNextMove() { return nextMove; }
}