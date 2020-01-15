package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.gui.ColorValue;

import java.util.ArrayList;
import java.util.List;

public class State implements Cloneable {

    private Game game;

    private Player player1, player2;
    private int width, height;

    private Player currentPlayer, winner;
    private int score1, score2, maxScore;

    private Edge lastMove;
    private int numberOfAvailableMoves;

    private byte[][] hEdgeMatrix, vEdgeMatrix, boxMatrix;
    private boolean canModifyGame;

    public State(Game game, Player player1, Player player2, int width, int height) {
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        this.width = width;
        this.height = height;

        this.currentPlayer = player1;
        this.winner = null;
        this.score1 = this.score2 = 0;
        this.maxScore = this.width * this.height;

        this.lastMove = new Edge();
        this.numberOfAvailableMoves = 2 * this.width * this.height + this.width + this.height;

        if (width > 0 && height > 0) {
            this.hEdgeMatrix = new byte[height + 1][width];
            this.vEdgeMatrix = new byte[height][width + 1];
            this.boxMatrix = new byte[height][width];
        }
        this.canModifyGame = true;
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

        clone.currentPlayer = currentPlayer;
        clone.winner = winner;
        clone.score1 = score1;
        clone.score2 = score2;

        clone.lastMove = lastMove.clone();
        clone.numberOfAvailableMoves = numberOfAvailableMoves;

        for (int i = 0; i <= height; ++i)
            for (int j = 0; j <= width; ++j) {
                if (j < width) clone.hEdgeMatrix[i][j] = hEdgeMatrix[i][j];
                if (i < height) clone.vEdgeMatrix[i][j] = vEdgeMatrix[i][j];
                if (j < width && i < height) {
                    clone.boxMatrix[i][j] = boxMatrix[i][j];
                }
            }

        clone.canModifyGame = canModifyGame;

        return clone;
    }

    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Player getCurrentPlayer() { return currentPlayer; }
    public Player getWinner() { return winner; }

    public int getPlayer1Score() { return score1; }
    public int getPlayer2Score() { return score2; }

    public int getMaxScore() { return maxScore; }
    public int getCurrentPlayerScore() { return currentPlayer == player1 ? score1 : score2; }

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

    public byte getBoxValue(int x, int y) { return boxMatrix[y][x]; }
    public boolean isBoxSet(int x, int y) { return getBoxValue(x, y) != 0; }

    public byte getEdgeValue(boolean horizontal, int x, int y) { return horizontal ? hEdgeMatrix[y][x] : vEdgeMatrix[y][x]; }
    public boolean isEdgeSet(boolean horizontal, int x, int y) { return getEdgeValue(horizontal, x, y) != 0; }

    public byte getEdgeValue(Edge edge) {
        return edge.isHorizontal() ? hEdgeMatrix[edge.getY()][edge.getX()] : vEdgeMatrix[edge.getY()][edge.getX()];
    }
    public void setEdgeValue(Edge edge, byte value) {
        if (edge.isHorizontal()) hEdgeMatrix[edge.getY()][edge.getX()] = value;
        else vEdgeMatrix[edge.getY()][edge.getX()] = value;
    }
    public boolean isEdgeSet(Edge edge) { return getEdgeValue(edge) != 0; }

    public boolean isTopEdgeSet(int x, int y) { return isEdgeSet(true, x, y); }
    public boolean isBottomEdgeSet(int x, int y) { return isEdgeSet(true, x, y + 1); }
    public boolean isLeftEdgeSet(int x, int y) { return isEdgeSet(false, x, y); }
    public boolean isRightEdgeSet(int x, int y) { return isEdgeSet(false, x + 1, y); }

    public boolean makeMove(Edge move) {
        if (move == null || !move.isValid() || isEdgeSet(move) || numberOfAvailableMoves == 0) return false;
        int x = move.getX(), y = move.getY();
        if (lastMove.isValid()) setEdgeValue(lastMove, ColorValue.BLACK);
        lastMove.copy(move);
        numberOfAvailableMoves--;
        if (numberOfAvailableMoves == 0) setEdgeValue(move, ColorValue.BLACK);
        else setEdgeValue(move, ColorValue.getLastEdgeColor(currentPlayer.getColorValue()));
        int score = currentPlayer == player1 ? score1 : score2;
        boolean playsAgain = false;
        if (move.isHorizontal()) {
            if (y > 0 && isTopEdgeSet(x, y - 1) && isLeftEdgeSet(x, y - 1) && isRightEdgeSet(x, y - 1)) {
                boxMatrix[y - 1][x] = currentPlayer.getColorValue();
                score++;
                playsAgain = true;
            }
            if (y < height && isBottomEdgeSet(x, y) && isLeftEdgeSet(x, y) && isRightEdgeSet(x, y)) {
                boxMatrix[y][x] = currentPlayer.getColorValue();
                score++;
                playsAgain = true;
            }
        } else {
            if (x > 0 && isLeftEdgeSet(x - 1, y) && isTopEdgeSet(x - 1, y) && isBottomEdgeSet(x - 1, y)) {
                boxMatrix[y][x - 1] = currentPlayer.getColorValue();
                score++;
                playsAgain = true;
            }
            if (x < width && isRightEdgeSet(x, y) && isTopEdgeSet(x, y) && isBottomEdgeSet(x, y)) {
                boxMatrix[y][x] = currentPlayer.getColorValue();
                score++;
                playsAgain = true;
            }
        }
        if (currentPlayer == player1) score1 = score;
        else score2 = score;
        if (!playsAgain) currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
        if (score1 + score2 == getMaxScore()) winner = score1 > score2 ? player1 : (score1 < score2 ? player2 : null);
        if (canModifyGame && numberOfAvailableMoves == 0) game.endGame();
        return true;
    }

    public State getNextBoardState(Edge move) {
        State nextState = clone();
        nextState.canModifyGame = false;
        nextState.makeMove(move);
        return nextState;
    }

    public int getPlayerHeuristic(Player player) {
        return player == player1 ? score1 - score2 : score2 - score1;
    }

    public int getEdgeCount(int x, int y) {
        int count = 0;
        if (isTopEdgeSet(x, y)) ++count;
        if (isBottomEdgeSet(x, y)) ++count;
        if (isLeftEdgeSet(x, y)) ++count;
        if (isRightEdgeSet(x, y)) ++count;
        return count;
    }

    public boolean addsNthEdge(Edge move, int n) {
        if (n < 1 || n > 4) return true;
        int x = move.getX(), y = move.getY();
        if (move.isHorizontal()) {
            if (y > 0 && getEdgeCount(x, y - 1) == n - 1) return true;
            return y < height && getEdgeCount(x, y) == n - 1;
        } else {
            if (x > 0 && getEdgeCount(x - 1, y) == n - 1) return true;
            return x < width && getEdgeCount(x, y) == n - 1;
        }
    }

    public int closeAllAvailableBoxes() {
        if (getNumberOfAvailableMoves() == 0) return 0;
        int boxCount = 0;
        List<Edge> availableMoves = getAvailableMoves();
        boolean stateChanged = true;
        while (stateChanged) {
            stateChanged = false;
            for (int i = 0; i < availableMoves.size(); ++i) {
                if (addsNthEdge(availableMoves.get(i), 4)) {
                    makeMove(availableMoves.get(i));
                    boxCount++;
                    availableMoves.remove(i);
                    stateChanged = true;
                }
            }
        }
        return boxCount;
    }
}