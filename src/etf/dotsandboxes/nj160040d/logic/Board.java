package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;

import java.util.List;

public class Board {

    int width, height, numberOfAvailableMoves;

    byte[][] hEdgeMatrix, vEdgeMatrix, boxMatrix;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        numberOfAvailableMoves = 2 * width * height + width + height;
        hEdgeMatrix = new byte[height + 1][width];
        vEdgeMatrix = new byte[height][width + 1];
        boxMatrix = new byte[height][width];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getNumberOfAvailableMoves() { return numberOfAvailableMoves; }

    public List<Edge> getAvailableMoves() {
        // TODO: Implement method
        return null;
    }

    public byte getBoxColorValue(int x, int y) { return boxMatrix[y][x]; }
    public void setBoxColorValue(int x, int y, byte value) { boxMatrix[y][x] = value; }
    public Box getBox(int x, int y) { return new Box(boxMatrix[y][x], x, y); }
    public boolean isBoxSet(int x, int y) { return getBoxColorValue(x, y) != 0; }

    public byte getBoxColorValue(Box box) { return getBoxColorValue(box.getX(), box.getY()); }
    public void setBoxColorValue(Box box, byte value) { setBoxColorValue(box.getX(), box.getY(), value); }
    public void setBox(Box box) { setBoxColorValue(box.getX(), box.getY(), box.getColorValue()); }
    public boolean isBoxSet(Box box) { return isBoxSet(box.getX(), box.getY()); }

    private byte getEdgeColorValue(boolean horizontal, int x, int y) {
        return horizontal ? hEdgeMatrix[y][x] : vEdgeMatrix[y][x];
    }
    private void setEdgeColorValue(boolean horizontal, int x, int y, byte value) {
        if (horizontal) hEdgeMatrix[y][x] = value;
        else vEdgeMatrix[y][x] = value;
    }
    public Edge getEdge(boolean horizontal, int x, int y) { return new Edge(getEdgeColorValue(horizontal, x, y), x, y, horizontal); }
    public boolean isEdgeSet(boolean horizontal, int x, int y) { return getEdgeColorValue(horizontal, x, y) != 0; }

    public byte getEdgeColorValue(Edge edge) { return getEdgeColorValue(edge.isHorizontal(), edge.getX(), edge.getY()); }
    public void setEdgeColorValue(Edge edge, byte value) { setEdgeColorValue(edge.isHorizontal(), edge.getX(), edge.getY(), value); }
    public void setEdge(Edge edge) { setEdgeColorValue(edge.isHorizontal(), edge.getX(), edge.getY(), edge.getColorValue()); }
    public boolean isEdgeSet(Edge edge) { return isEdgeSet(edge.isHorizontal(), edge.getX(), edge.getY()); }

    public byte getTopEdgeColorValue(int x, int y) { return getEdgeColorValue(true, x, y); }
    public void setTopEdgeColorValue(int x, int y, byte value) { setEdgeColorValue(true, x, y, value); }
    public Edge getTopEdge(int x, int y) { return getEdge(true, x, y); }
    public boolean isTopEdgeSet(int x, int y) { return isEdgeSet(true, x, y); }

    public byte getBottomEdgeColorValue(int x, int y) { return getEdgeColorValue(true, x, y + 1); }
    public void setBottomEdgeColorValue(int x, int y, byte value) { setEdgeColorValue(true, x, y + 1, value); }
    public Edge getBottomEdge(int x, int y) { return getEdge(true, x, y + 1); }
    public boolean isBottomEdgeSet(int x, int y) { return isEdgeSet(true, x, y + 1); }

    public byte getLeftEdgeColorValue(int x, int y) { return getEdgeColorValue(false, x, y); }
    public void setLeftEdgeColorValue(int x, int y, byte value) { setEdgeColorValue(false, x, y, value); }
    public Edge getLeftEdge(int x, int y) { return getEdge(false, x, y); }
    public boolean isLeftEdgeSet(int x, int y) { return isEdgeSet(false, x, y); }

    public byte getRightEdgeColorValue(int x, int y) { return getEdgeColorValue(false, x + 1, y); }
    public void setRightEdgeColorValue(int x, int y, byte value) { setEdgeColorValue(false, x + 1, y, value); }
    public Edge getRightEdge(int x, int y) { return getEdge(false, x + 1, y); }
    public boolean isRightEdgeSet(int x, int y) { return isEdgeSet(false, x + 1, y); }

    public void playerDrawEdge(boolean horizontal, int x, int y) {
        numberOfAvailableMoves--;
        if (numberOfAvailableMoves == 0) setEdgeColorValue(horizontal, x, y, ColorValue.BLACK);
        else setEdgeColorValue(horizontal, x, y, ColorValue.getLight(Game.getCurrentColorValue()));
        if (horizontal) {
            if (y > 0 && isTopEdgeSet(x, y - 1) && isLeftEdgeSet(x, y - 1) && isRightEdgeSet(x, y - 1)) {
                setBoxColorValue(x, y - 1, Game.getCurrentColorValue());
            }
            if (y < height && isBottomEdgeSet(x, y) && isLeftEdgeSet(x, y) && isRightEdgeSet(x, y)) {
                setBoxColorValue(x, y, Game.getCurrentColorValue());
            }
        } else {
            if (x > 0 && isLeftEdgeSet(x - 1, y) && isTopEdgeSet(x - 1, y) && isBottomEdgeSet(x - 1, y)) {
                setBoxColorValue(x - 1, y, Game.getCurrentColorValue());
            }
            if (x < width && isRightEdgeSet(x, y) && isTopEdgeSet(x, y) && isBottomEdgeSet(x, y)) {
                setBoxColorValue(x, y, Game.getCurrentColorValue());
            }
        }
    }
    public void playerDrawEdge(Edge edge) {
        playerDrawEdge(edge.isHorizontal(), edge.getX(), edge.getY());
    }
}