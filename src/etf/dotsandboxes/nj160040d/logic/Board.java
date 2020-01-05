package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.gui.ColorValue;

import java.util.ArrayList;
import java.util.List;

public class Board {

    Game game;
    int width, height, numberOfAvailableMoves;
    byte[][] hEdgeMatrix, vEdgeMatrix, boxMatrix;
    Edge lastEdge;

    public Board(Game game, int width, int height) {
        this.game = game;
        this.width = width;
        this.height = height;
        this.numberOfAvailableMoves = 2 * width * height + width + height;
        this.hEdgeMatrix = new byte[height + 1][width];
        this.vEdgeMatrix = new byte[height][width + 1];
        this.boxMatrix = new byte[height][width];
        this.lastEdge = new Edge();
    }

    public Board(Game game, String gameStateFileName) {
        this.game = game;
        // TODO: Implement method
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getMaxScore() {
        final int maxScore = width * height;
        return maxScore;
    }

    public int getNumberOfAvailableMoves() { return numberOfAvailableMoves; }

    public List<Edge> getAvailableMoves() {
        ArrayList<Edge> availableMoves = new ArrayList<>();
        for (int i = 0; i <= height; ++i)
            for (int j = 0; j < width; ++j)
                if (hEdgeMatrix[i][j] == 0) availableMoves.add(new Edge(j, i, true));
        for (int i = 0; i < height; ++i)
            for (int j = 0; j <= width; ++j)
                if (vEdgeMatrix[i][j] == 0) availableMoves.add(new Edge(j, i, false));
        return availableMoves;
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

    public boolean closesBox(Edge edge) {
        int x = edge.getX(), y = edge.getY();
        if (edge.isHorizontal()) {
            if (y > 0 && isTopEdgeSet(x, y - 1) && isLeftEdgeSet(x, y - 1) && isRightEdgeSet(x, y - 1)) return true;
            if (y < height && isBottomEdgeSet(x, y) && isLeftEdgeSet(x, y) && isRightEdgeSet(x, y)) return true;
        } else {
            if (x > 0 && isLeftEdgeSet(x - 1, y) && isTopEdgeSet(x - 1, y) && isBottomEdgeSet(x - 1, y)) return true;
            if (x < width && isRightEdgeSet(x, y) && isTopEdgeSet(x, y) && isBottomEdgeSet(x, y)) return true;
        }
        return false;
    }

    public boolean playerDrawEdge(Edge edge) {
        if (!edge.isValid()) return false;
        if (numberOfAvailableMoves == 0) return false;
        Player player = game.getCurrentPlayer();
        int x = edge.getX(), y = edge.getY();
        byte colorValue = player.getColorValue();
        int score = player.getScore();
        boolean playsAgain = false;
        if (lastEdge.isValid()) game.getBoard().setEdgeColorValue(lastEdge, ColorValue.BLACK);
        lastEdge.copy(edge);
        numberOfAvailableMoves--;
        if (numberOfAvailableMoves == 0) setEdgeColorValue(edge, ColorValue.BLACK);
        else setEdgeColorValue(edge, ColorValue.getHighlightColor(colorValue));
        if (edge.isHorizontal()) {
            if (y > 0 && isTopEdgeSet(x, y - 1) && isLeftEdgeSet(x, y - 1) && isRightEdgeSet(x, y - 1)) {
                setBoxColorValue(x, y - 1, colorValue);
                score++;
                playsAgain = true;
            }
            if (y < height && isBottomEdgeSet(x, y) && isLeftEdgeSet(x, y) && isRightEdgeSet(x, y)) {
                setBoxColorValue(x, y, colorValue);
                score++;
                playsAgain = true;
            }
        } else {
            if (x > 0 && isLeftEdgeSet(x - 1, y) && isTopEdgeSet(x - 1, y) && isBottomEdgeSet(x - 1, y)) {
                setBoxColorValue(x - 1, y, colorValue);
                score++;
                playsAgain = true;
            }
            if (x < width && isRightEdgeSet(x, y) && isTopEdgeSet(x, y) && isBottomEdgeSet(x, y)) {
                setBoxColorValue(x, y, colorValue);
                score++;
                playsAgain = true;
            }
        }
        player.setScore(score);
        if (numberOfAvailableMoves == 0) game.endGame();
        return playsAgain;
    }
}