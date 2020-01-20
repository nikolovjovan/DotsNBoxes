package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.gui.ColorValue;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class State {

    public static int parsedWidth, parsedHeight;
    public static List<Edge> parsedMoves;

    private Game game;

    private Player player1, player2;
    private int width, height;

    private Player currentPlayer, winner;
    private int score1, score2, maxScore;

    private Edge lastMove;
    private int availableMovesCount;
    private int[] boxEdgeCount;

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
        this.availableMovesCount = 2 * this.width * this.height + this.width + this.height;
        this.boxEdgeCount = new int[5];
        this.boxEdgeCount[0] = this.width * this.height;
        for (int i = 1; i < 5; ++i) this.boxEdgeCount[i] = 0;

        if (width > 0 && height > 0) {
            this.hEdgeMatrix = new byte[height + 1][width];
            this.vEdgeMatrix = new byte[height][width + 1];
            this.boxMatrix = new byte[height][width];
        }
        this.canModifyGame = true;
    }

    public State getClone() {
        State clone = new State(game, player1, player2, width, height);

        clone.currentPlayer = currentPlayer;
        clone.winner = winner;
        clone.score1 = score1;
        clone.score2 = score2;

        clone.lastMove = lastMove.getClone();
        clone.availableMovesCount = availableMovesCount;

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

    public static boolean tryParseGameStateFromFile(String gameStateFilePath) {
        try (Scanner sc = new Scanner(new File(gameStateFilePath))) {
            parsedWidth = sc.nextInt();
            parsedHeight = sc.nextInt();
            sc.nextLine();
            if (parsedWidth > 0 && parsedHeight > 0) {
//                System.out.println("Board size: " + parsedWidth + "x" + parsedHeight);
                parsedMoves = new ArrayList<>();
                while (sc.hasNextLine()) {
                    String move = sc.nextLine();
                    if (move.isEmpty()) continue;
//                    System.out.println("Read move: '" + move + "'");
                    Edge parsedMove = Edge.parseEdgeFromString(move);
                    if (parsedMove == null) {
                        System.err.println("Error! Failed to parse move: '" + move + "'.");
                        return false;
                    }
//                    System.out.println("Parsed move: " + parsedMove);
                    parsedMoves.add(parsedMove);
                }
                return true;
            } else {
                System.err.println("Error! Invalid board size: " + parsedWidth + "x" + parsedHeight + ".");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error! Invalid game state file path: '" + gameStateFilePath + "'.");
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    public static boolean tryExportGameStateToFile(State state, String gameStateFilePath) {
        if (state == null) return false;
        try (PrintWriter writer = new PrintWriter(gameStateFilePath, "UTF-8")) {
            writer.println(state.getWidth() + " " + state.getHeight());
            for (Edge move : state.game.getMoves()) {
//                System.out.println("Move: " + move + " generated string: " + Edge.generateStringFromEdge(move));
                writer.println(Edge.generateStringFromEdge(move));
            }
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("Error! Invalid game state file path: '" + gameStateFilePath + "'.");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(System.err);
        }
        return false;
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

    public int getAvailableMovesCount() { return availableMovesCount; }

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

    public int getEdgeCount(int x, int y) {
        int count = 0;
        if (isTopEdgeSet(x, y)) ++count;
        if (isBottomEdgeSet(x, y)) ++count;
        if (isLeftEdgeSet(x, y)) ++count;
        if (isRightEdgeSet(x, y)) ++count;
        return count;
    }

    public int getBoxCount(int edgeCount) { return edgeCount < 0 || edgeCount > 4 ? -1 : boxEdgeCount[edgeCount]; }

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

    public boolean makeMove(Edge move) {
        if (move == null || !move.isValid() || isEdgeSet(move) || availableMovesCount == 0) return false;
        int x = move.getX(), y = move.getY();
        if (lastMove.isValid()) setEdgeValue(lastMove, ColorValue.BLACK);
        lastMove.copy(move);
        availableMovesCount--;
        if (availableMovesCount == 0) setEdgeValue(move, ColorValue.BLACK);
        else setEdgeValue(move, ColorValue.getLastEdgeColor(currentPlayer.getColorValue()));
        int score = currentPlayer == player1 ? score1 : score2;
        boolean playsAgain = false;
        if (move.isHorizontal()) {
            if (y > 0) {
                int edgeCount = getEdgeCount(x, y - 1);
                boxEdgeCount[edgeCount - 1]--;
                boxEdgeCount[edgeCount]++;
                if (edgeCount == 4) {
                    boxMatrix[y - 1][x] = currentPlayer.getColorValue();
                    score++;
                    playsAgain = true;
                }
            }
        } else {
            if (x > 0) {
                int edgeCount = getEdgeCount(x - 1, y);
                boxEdgeCount[edgeCount - 1]--;
                boxEdgeCount[edgeCount]++;
                if (edgeCount == 4) {
                    boxMatrix[y][x - 1] = currentPlayer.getColorValue();
                    score++;
                    playsAgain = true;
                }
            }
        }
        if (move.isHorizontal() && y < height || !move.isHorizontal() && x < width) {
            int edgeCount = getEdgeCount(x, y);
            boxEdgeCount[edgeCount - 1]--;
            boxEdgeCount[edgeCount]++;
            if (edgeCount == 4) {
                boxMatrix[y][x] = currentPlayer.getColorValue();
                score++;
                playsAgain = true;
            }
        }
        if (currentPlayer == player1) score1 = score;
        else score2 = score;
        if (!playsAgain) currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
        if (score1 + score2 == getMaxScore()) winner = score1 > score2 ? player1 : (score1 < score2 ? player2 : null);
        if (canModifyGame && availableMovesCount == 0) game.endGame();
        return true;
    }

    public State getNextBoardState(Edge move) {
        State nextState = getClone();
        nextState.canModifyGame = false;
        nextState.makeMove(move);
        return nextState;
    }

    public int closeAllAvailableBoxes() {
        if (availableMovesCount == 0 || boxEdgeCount[3] == 0) return 0;
        int boxCount = 0;
        List<Edge> availableMoves = getAvailableMoves();
        boolean stateChanged = true;
        while (stateChanged) {
            stateChanged = false;
            int i = 0;
            while (i < availableMoves.size()) {
                if (addsNthEdge(availableMoves.get(i), 4)) {
                    makeMove(availableMoves.get(i));
                    boxCount++;
                    availableMoves.remove(i);
                    stateChanged = true;
                } else ++i;
            }
        }
        return boxCount;
    }

//    // TODO: REMOVE THIS
//    public Edge getLastMove() { return lastMove; }
}