package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.gui.ColorValue;

import java.util.ArrayList;
import java.util.List;

public class State implements Cloneable {

    private Game game;
    private Player player1, player2;
    private int width, height;

    private byte[][] hEdgeMatrix, vEdgeMatrix, boxMatrix;

    private boolean canModifyGame;
    private int numberOfAvailableMoves, score1, score2, maxScore;
    private Edge lastEdge;
    private Player currentPlayer, winner, loser;

    public State(Game game, Player player1, Player player2, int width, int height) {
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        this.width = width;
        this.height = height;
        this.canModifyGame = true;
        if (width > 0 && height > 0) {
            this.hEdgeMatrix = new byte[height + 1][width];
            this.vEdgeMatrix = new byte[height][width + 1];
            this.boxMatrix = new byte[height][width];
            this.numberOfAvailableMoves = 2 * width * height + width + height;
        }
        this.score1 = this.score2 = 0;
        this.maxScore = this.width * this.height;
        this.lastEdge = new Edge();
        this.currentPlayer = player1;
        this.winner = this.loser = null;
    }

    public State(Game game, Player player1, Player player2, String gameStateFileName) {
        this(game, player1, player2, 0, 0);
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        // TODO: Implement method
    }

    @Override
    protected State clone() {
        State clone = new State(game, player1, player2, width, height);

        for (int i = 0; i <= height; ++i)
            for (int j = 0; j <= width; ++j) {
                if (j < width) clone.hEdgeMatrix[i][j] = hEdgeMatrix[i][j];
                if (i < height) clone.vEdgeMatrix[i][j] = vEdgeMatrix[i][j];
                if (j < width && i < height) {
                    clone.boxMatrix[i][j] = boxMatrix[i][j];
                }
            }

        clone.canModifyGame = canModifyGame;
        clone.numberOfAvailableMoves = numberOfAvailableMoves;
        clone.score1 = score1;
        clone.score2 = score2;
        clone.lastEdge = lastEdge.clone();
        clone.currentPlayer = currentPlayer;
        clone.winner = winner;
        clone.loser = loser;

        return clone;
    }

    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getNumberOfAvailableMoves() { return numberOfAvailableMoves; }

    public int getPlayer1Score() { return score1; }
    public int getPlayer2Score() { return score2; }
    public int getMaxScore() { return maxScore; }

    public int getCurrentPlayerScore() {
        if (currentPlayer == player1) return score1;
        return score2;
    }

    public Player getCurrentPlayer() { return currentPlayer; }
    public Player getWinner() { return winner; }
    public Player getLoser() { return loser; }

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

    public int getEdgeCount(int x, int y) {
        int count = 0;
        if (isTopEdgeSet(x, y)) ++count;
        if (isBottomEdgeSet(x, y)) ++count;
        if (isLeftEdgeSet(x, y)) ++count;
        if (isRightEdgeSet(x, y)) ++count;
        return count;
    }

    public int getBoxCount(int numEdges) {
        int count = 0;
        for (int i = 0; i < height; ++i)
            for (int j = 0; j < width; ++j)
                if (getEdgeCount(j, i) == numEdges) ++count;
        return count;
    }

    public boolean addsNthEdge(Edge move, int n) {
        if (n < 1 || n > 4) return true;
        int x = move.getX(), y = move.getY();
        if (move.isHorizontal()) {
            if (y > 0 && getEdgeCount(x, y - 1) == n - 1) return true;
            if (y < height && getEdgeCount(x, y) == n - 1) return true;
        } else {
            if (x > 0 && getEdgeCount(x - 1, y) == n - 1) return true;
            if (x < width && getEdgeCount(x, y) == n - 1) return true;
        }
        return false;
    }

    public void playerDrawEdge(Edge edge) {
        if (edge == null || !edge.isValid() || numberOfAvailableMoves == 0) return;
        int x = edge.getX(), y = edge.getY();
        if (lastEdge.isValid()) setEdgeColorValue(lastEdge, ColorValue.BLACK);
        lastEdge.copy(edge);
        numberOfAvailableMoves--;
        if (numberOfAvailableMoves == 0) setEdgeColorValue(edge, ColorValue.BLACK);
        else setEdgeColorValue(edge, ColorValue.getLastEdgeColor(currentPlayer.getColorValue()));
        int score = currentPlayer == player1 ? score1 : score2;
        boolean playsAgain = false;
        if (edge.isHorizontal()) {
            if (y > 0 && isTopEdgeSet(x, y - 1) && isLeftEdgeSet(x, y - 1) && isRightEdgeSet(x, y - 1)) {
                setBoxColorValue(x, y - 1, currentPlayer.getColorValue());
                score++;
                playsAgain = true;
            }
            if (y < height && isBottomEdgeSet(x, y) && isLeftEdgeSet(x, y) && isRightEdgeSet(x, y)) {
                setBoxColorValue(x, y, currentPlayer.getColorValue());
                score++;
                playsAgain = true;
            }
        } else {
            if (x > 0 && isLeftEdgeSet(x - 1, y) && isTopEdgeSet(x - 1, y) && isBottomEdgeSet(x - 1, y)) {
                setBoxColorValue(x - 1, y, currentPlayer.getColorValue());
                score++;
                playsAgain = true;
            }
            if (x < width && isRightEdgeSet(x, y) && isTopEdgeSet(x, y) && isBottomEdgeSet(x, y)) {
                setBoxColorValue(x, y, currentPlayer.getColorValue());
                score++;
                playsAgain = true;
            }
        }
        if (currentPlayer == player1) score1 = score;
        else score2 = score;
        if (!playsAgain) currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
        if (score1 + score2 == getMaxScore()) {
            if (score1 > score2) { // player 1 wins
                winner = player1;
                loser = player2;
            } else if (score1 < score2) { // player 2 wins
                winner = player2;
                loser = player1;
            } else { // tie
                winner = loser = null;
            }
        }
        if (canModifyGame && numberOfAvailableMoves == 0) game.endGame();
    }

    public State getNextBoardState(Edge move) {
        State nextState = clone();
        nextState.canModifyGame = false;
        nextState.playerDrawEdge(move);
        return nextState;
    }
}