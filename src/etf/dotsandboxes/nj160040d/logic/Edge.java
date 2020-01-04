package etf.dotsandboxes.nj160040d.logic;

public class Edge {

    byte value;
    int x, y;
    boolean horizontal;

    public Edge() {
        value = 0;
        x = y = -1;
        horizontal = false;
    }

    public Edge(byte value, int x, int y, boolean horizontal) {
        this.value = value;
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
    }

    public void copy(Edge edge) {
        value = edge.value;
        x = edge.x;
        y = edge.y;
        horizontal = edge.horizontal;
    }

    public boolean isValid() { return x >= 0 && y >= 0; }
    public void invalidate() { x = y = -1; }

    public byte getValue() { return value; }
    public void setValue(byte value) { this.value = value; }

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