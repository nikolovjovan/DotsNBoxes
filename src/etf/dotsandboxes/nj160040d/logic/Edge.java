package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.gui.ColorValue;

public class Edge implements Cloneable {

    public static final Edge INVALID = new Edge();

    private byte value;
    private int x, y;
    private boolean horizontal;

    public Edge(byte value, int x, int y, boolean horizontal) {
        this.value = value;
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
    }

    public Edge(int x, int y, boolean horizontal) {
        this(ColorValue.TRANSPARENT, x, y, horizontal);
    }

    public Edge() {
        this((byte) 0, -1, -1, false);
    }

    @Override
    public Edge clone() {
        return new Edge(value, x, y, horizontal);
    }

    public Edge copy(Edge edge) {
        value = edge.value;
        x = edge.x;
        y = edge.y;
        horizontal = edge.horizontal;
        return this;
    }

    public boolean isValid() { return x >= 0 && y >= 0; }
    public void invalidate() { x = y = -1; }

    public byte getValue() { return value; }
    public void setValue(byte value) { this.value = value; }

    public boolean isHorizontal() { return horizontal; }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setAsTopEdge(int boardX, int boardY) { horizontal = true; x = boardX; y = boardY; }
    public void setAsBottomEdge(int boardX, int boardY) { horizontal = true; x = boardX; y = boardY + 1; }
    public void setAsLeftEdge(int boardX, int boardY) { horizontal = false; x = boardX; y = boardY; }
    public void setAsRightEdge(int boardX, int boardY) { horizontal = false; x = boardX + 1; y = boardY; }


}