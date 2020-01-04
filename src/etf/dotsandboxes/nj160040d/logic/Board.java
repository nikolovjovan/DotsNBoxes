package etf.dotsandboxes.nj160040d.logic;

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

    public byte getBoxValue(int x, int y) { return boxMatrix[y][x]; }
    public void setBoxValue(int x, int y, byte value) { boxMatrix[y][x] = value; }
    public Box getBox(int x, int y) { return new Box(boxMatrix[y][x], x, y); }
    public boolean isBoxSet(int x, int y) { return getBoxValue(x, y) != 0; }

    public byte getBoxValue(Box box) { return getBoxValue(box.getX(), box.getY()); }
    public void setBoxValue(Box box, byte value) { setBoxValue(box.getX(), box.getY(), value); }
    public void setBox(Box box) { setBoxValue(box.getX(), box.getY(), box.getValue()); }
    public boolean isBoxSet(Box box) { return isBoxSet(box.getX(), box.getY()); }

    private byte getEdgeValue(boolean horizontal, int x, int y) {
        return horizontal ? hEdgeMatrix[y][x] : vEdgeMatrix[y][x];
    }
    private void setEdgeValue(boolean horizontal, int x, int y, byte value) {
        if (horizontal) hEdgeMatrix[y][x] = value;
        else vEdgeMatrix[y][x] = value;
        if (value > 0) numberOfAvailableMoves--;
        else numberOfAvailableMoves++;
    }
    public Edge getEdge(boolean horizontal, int x, int y) { return new Edge(getEdgeValue(horizontal, x, y), x, y, horizontal); }
    public boolean isEdgeSet(boolean horizontal, int x, int y) { return getEdgeValue(horizontal, x, y) != 0; }

    public byte getEdgeValue(Edge edge) { return getEdgeValue(edge.isHorizontal(), edge.getX(), edge.getY()); }
    public void setEdgeValue(Edge edge, byte value) { setEdgeValue(edge.isHorizontal(), edge.getX(), edge.getY(), value); }
    public void setEdge(Edge edge) { setEdgeValue(edge.isHorizontal(), edge.getX(), edge.getY(), edge.getValue()); }
    public boolean isEdgeSet(Edge edge) { return isEdgeSet(edge.isHorizontal(), edge.getX(), edge.getY()); }

    public byte getTopEdgeValue(int x, int y) { return getEdgeValue(true, x, y); }
    public void setTopEdgeValue(int x, int y, byte value) { setEdgeValue(true, x, y, value); }
    public Edge getTopEdge(int x, int y) { return getEdge(true, x, y); }
    public boolean isTopEdgeSet(int x, int y) { return isEdgeSet(true, x, y); }

    public byte getBottomEdgeValue(int x, int y) { return getEdgeValue(true, x, y + 1); }
    public void setBottomEdgeValue(int x, int y, byte value) { setEdgeValue(true, x, y + 1, value); }
    public Edge getBottomEdge(int x, int y) { return getEdge(true, x, y + 1); }
    public boolean isBottomEdgeSet(int x, int y) { return isEdgeSet(true, x, y + 1); }

    public byte getLeftEdgeValue(int x, int y) { return getEdgeValue(false, x, y); }
    public void setLeftEdgeValue(int x, int y, byte value) { setEdgeValue(false, x, y, value); }
    public Edge getLeftEdge(int x, int y) { return getEdge(false, x, y); }
    public boolean isLeftEdgeSet(int x, int y) { return isEdgeSet(false, x, y); }

    public byte getRightEdgeValue(int x, int y) { return getEdgeValue(false, x + 1, y); }
    public void setRightEdgeValue(int x, int y, byte value) { setEdgeValue(false, x + 1, y, value); }
    public Edge getRightEdge(int x, int y) { return getEdge(false, x + 1, y); }
    public boolean isRightEdgeSet(int x, int y) { return isEdgeSet(false, x + 1, y); }

    public void updateEdgeValue(boolean horizontal, int x, int y, byte edgeValue, byte boxValue) {
        setEdgeValue(horizontal, x, y, edgeValue);
        if (horizontal) {
            if (y > 0 && isTopEdgeSet(x, y - 1) && isLeftEdgeSet(x, y - 1) && isRightEdgeSet(x, y - 1)) {
                setBoxValue(x, y - 1, boxValue);
            }
            if (y < height && isBottomEdgeSet(x, y) && isLeftEdgeSet(x, y) && isRightEdgeSet(x, y)) {
                setBoxValue(x, y, boxValue);
            }
        } else {
            if (x > 0 && isLeftEdgeSet(x - 1, y) && isTopEdgeSet(x - 1, y) && isBottomEdgeSet(x - 1, y)) {
                setBoxValue(x - 1, y, boxValue);
            }
            if (x < width && isRightEdgeSet(x, y) && isTopEdgeSet(x, y) && isBottomEdgeSet(x, y)) {
                setBoxValue(x, y, boxValue);
            }
        }
    }
    public void updateEdgeValue(Edge edge, byte edgeValue, byte boxValue) {
        updateEdgeValue(edge.isHorizontal(), edge.getX(), edge.getY(), edgeValue, boxValue);
    }

    public List<Edge> getAvailableMoves() {
        // TODO: Implement method
        return null;
    }
}