package etf.dotsandboxes.nj160040d.logic;

public class Edge {

    byte colorValue;
    int x, y;
    boolean horizontal;

    public Edge() {
        colorValue = 0;
        x = y = -1;
        horizontal = false;
    }

    public Edge(byte colorValue, int x, int y, boolean horizontal) {
        this.colorValue = colorValue;
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
    }

    public void copy(Edge edge) {
        colorValue = edge.colorValue;
        x = edge.x;
        y = edge.y;
        horizontal = edge.horizontal;
    }

    public boolean isValid() { return x >= 0 && y >= 0; }
    public void invalidate() { x = y = -1; }

    public byte getColorValue() { return colorValue; }
    public void setColorValue(byte colorValue) { this.colorValue = colorValue; }

    public boolean isHorizontal() { return horizontal; }
    public void setHorizontal(boolean value) { horizontal = value; }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int value) { x = value; }
    public void setY(int value) { y = value; }

    public void setAsTopEdge(int boardX, int boardY) { horizontal = true; x = boardX; y = boardY; }
    public void setAsBottomEdge(int boardX, int boardY) { horizontal = true; x = boardX; y = boardY + 1; }
    public void setAsLeftEdge(int boardX, int boardY) { horizontal = false; x = boardX; y = boardY; }
    public void setAsRightEdge(int boardX, int boardY) { horizontal = false; x = boardX + 1; y = boardY; }
}