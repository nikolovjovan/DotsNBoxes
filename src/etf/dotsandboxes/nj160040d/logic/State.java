package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.gui.ColorValue;
import etf.dotsandboxes.nj160040d.util.UnsafeLinkedList;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class State {

    public static int parsedWidth, parsedHeight;
    public static List<Edge> parsedMoves;

    private Game game;

    private Player player1, player2;
    private int width, height;

    private boolean canModifyGame;

    private Player currentPlayer, winner;
    private int score1, score2, maxScore;

    private UnsafeLinkedList<Edge> availableMoves;
    private Stack<Edge> previousMoves;
    private Stack<Player> previousPlayers;

    private int[] boxEdgeCount;
    private byte[][] hEdgeMatrix, vEdgeMatrix, boxMatrix;

    private State(Game game, Player player1, Player player2, int width, int height, boolean init) {
        this.game = game;
        this.player1 = player1;
        this.player2 = player2;
        this.width = width;
        this.height = height;

        this.canModifyGame = true;

        this.currentPlayer = player1;
        this.winner = null;
        this.score1 = this.score2 = 0;
        this.maxScore = this.width * this.height;

        this.availableMoves = new UnsafeLinkedList<>();
        this.previousMoves = new Stack<>();
        this.previousPlayers = new Stack<>();

        this.boxEdgeCount = new int[5];

        if (width > 0 && height > 0) {
            this.hEdgeMatrix = new byte[height + 1][width];
            this.vEdgeMatrix = new byte[height][width + 1];
            this.boxMatrix = new byte[height][width];
        }

        if (!init) return;

        this.boxEdgeCount[0] = this.width * this.height;

        for (int i = 0; i <= height; ++i)
            for (int j = 0; j < width; ++j)
                availableMoves.addLast(new Edge(j, i, true));
        for (int i = 0; i < height; ++i)
            for (int j = 0; j <= width; ++j)
                availableMoves.addLast(new Edge(j, i, false));
    }

    public State(Game game, Player player1, Player player2, int width, int height) {
        this(game, player1, player2, width, height, true);
    }

    public State getClone() {
        State clone = new State(game, player1, player2, width, height, false);

        clone.canModifyGame = canModifyGame;

        clone.currentPlayer = currentPlayer;
        clone.winner = winner;
        clone.score1 = score1;
        clone.score2 = score2;

        for (Edge move : availableMoves) clone.availableMoves.addLast(move);
        for (Edge move : previousMoves) clone.previousMoves.push(move);

        for (int i = 0; i < 5; ++i) clone.boxEdgeCount[i] = boxEdgeCount[i];

        for (int i = 0; i <= height; ++i)
            for (int j = 0; j <= width; ++j) {
                if (j < width) clone.hEdgeMatrix[i][j] = hEdgeMatrix[i][j];
                if (i < height) clone.vEdgeMatrix[i][j] = vEdgeMatrix[i][j];
                if (j < width && i < height) clone.boxMatrix[i][j] = boxMatrix[i][j];
            }

        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof State)) return false;
        State s = (State) obj;

        boolean equal = true;

        if (game != s.game) {
            System.out.println("game instance different");
            equal = false;
        }
        if (player1 != s.player1) {
            System.out.println("player1 instance different");
            equal = false;
        }
        if (player2 != s.player2) {
            System.out.println("player2 instance different");
            equal = false;
        }
        if (width != s.width) {
            System.out.println("width different: " + width + " vs " + s.width);
            equal = false;
        }
        if (height != s.height) {
            System.out.println("height different: " + height + " vs " + s.height);
            equal = false;
        }

        if (currentPlayer != s.currentPlayer) {
            System.out.println("currentPlayer different: " + currentPlayer.name + " vs " + s.currentPlayer.name);
            equal = false;
        }
        if (winner != s.winner) {
            System.out.println("winner different: " + winner.name + " vs " + s.winner.name);
            equal = false;
        }
        if (score1 != s.score1) {
            System.out.println("score1 different: " + score1 + " vs " + s.score1);
            equal = false;
        }
        if (score2 != s.score2) {
            System.out.println("score2 different: " + score2 + " vs " + s.score2);
            equal = false;
        }
        if (maxScore != s.maxScore) {
            System.out.println("maxScore different: " + maxScore + " vs " + s.maxScore);
            equal = false;
        }

        if (availableMoves.size() != s.availableMoves.size()) {
            System.out.println("availableMoves different: " + availableMoves.size() + " vs " + s.availableMoves.size());
            equal = false;
        }

        if (previousMoves.size() == s.previousMoves.size()) {
            ListIterator<Edge> myIt = previousMoves.listIterator();
            ListIterator<Edge> sIt = s.previousMoves.listIterator();
            int i = 0;
            while (myIt.hasNext() && sIt.hasNext()) {
                Edge myMove = myIt.next();
                Edge sMove = sIt.next();
                if (myMove != sMove) {
                    System.out.println("previousMoves[" + i + "] different: " + myMove + " vs " + sMove);
                    System.out.println("previousMoves different");
                    equal = false;
                }
            }
        } else equal = false;

        for (int i = 0; i <= 4; ++i) if (boxEdgeCount[i] != s.boxEdgeCount[i]) {
            System.out.println("boxEdgeCount[" + i + "] different: " + boxEdgeCount[i] + " vs " + s.boxEdgeCount[i]);
            equal = false;
        }

        for (int i = 0; i <= height; ++i)
            for (int j = 0; j <= width; ++j) {
                if (j < width) if (hEdgeMatrix[i][j] != s.hEdgeMatrix[i][j]) {
                    System.out.println("hEdgeMatrix[" + i + "][" + j + "] different: " + hEdgeMatrix[i][j] + " vs " + s.hEdgeMatrix[i][j]);
                    equal = false;
                }
                if (i < height) if (vEdgeMatrix[i][j] != s.vEdgeMatrix[i][j]) {
                    System.out.println("vEdgeMatrix[" + i + "][" + j + "] different: " + vEdgeMatrix[i][j] + " vs " + s.vEdgeMatrix[i][j]);
                    equal = false;
                }
                if (j < width && i < height) if (boxMatrix[i][j] != s.boxMatrix[i][j]) {
                    System.out.println("boxMatrix[" + i + "][" + j + "] different: " + boxMatrix[i][j] + " vs " + s.boxMatrix[i][j]);
                    equal = false;
                }
            }

        if (canModifyGame != s.canModifyGame) {
            System.out.println("canModifyGame different: " + canModifyGame + " vs " + s.canModifyGame);
            equal = false;
        }

        return equal;
    }

    public static boolean tryParseGameStateFromFile(String gameStateFilePath) {
        try (Scanner sc = new Scanner(new File(gameStateFilePath))) {
            parsedWidth = sc.nextInt();
            parsedHeight = sc.nextInt();
            sc.nextLine();
            if (parsedWidth > 0 && parsedHeight > 0) {
                parsedMoves = new ArrayList<>();
                while (sc.hasNextLine()) {
                    String move = sc.nextLine();
                    if (move.isEmpty()) continue;
                    Edge parsedMove = Edge.parseEdgeFromString(move);
                    if (parsedMove == null) {
                        System.err.println("Error! Failed to parse move: '" + move + "'.");
                        return false;
                    }
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
            writer.println(state.width + " " + state.height);
            for (Edge move : state.previousMoves) {
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

    public boolean getCanModifyGame() { return canModifyGame; }
    public void setCanModifyGame(boolean canModifyGame) { this.canModifyGame = canModifyGame; }

    public Player getCurrentPlayer() { return currentPlayer; }
    public Player getWinner() { return winner; }

    public int getPlayer1Score() { return score1; }
    public int getPlayer2Score() { return score2; }

    public int getPlayerScore(Player player) {
        return player == player1 ? score1 : (player == player2 ? score2 : -1);
    }
    public int getOpponentScore(Player player) {
        return player == player1 ? score2 : (player == player2 ? score1 : -1);
    }

    public int getMaxScore() { return maxScore; }
    public int getCurrentPlayerScore() { return getPlayerScore(currentPlayer); }

    public Stack<Edge> getPreviousMoves() { return previousMoves; }

    public UnsafeLinkedList<Edge> getAvailableMoves() { return availableMoves; }

    public byte getBoxValue(int x, int y) { return boxMatrix[y][x]; }
    public boolean isBoxSet(int x, int y) { return getBoxValue(x, y) != ColorValue.TRANSPARENT; }

    public byte getEdgeValue(boolean horizontal, int x, int y) { return horizontal ? hEdgeMatrix[y][x] : vEdgeMatrix[y][x]; }
    public boolean isEdgeSet(boolean horizontal, int x, int y) { return getEdgeValue(horizontal, x, y) != ColorValue.TRANSPARENT; }

    public byte getEdgeValue(Edge edge) {
        return edge.isHorizontal() ? hEdgeMatrix[edge.getY()][edge.getX()] : vEdgeMatrix[edge.getY()][edge.getX()];
    }
    public void setEdgeValue(Edge edge, byte value) {
        if (edge.isHorizontal()) hEdgeMatrix[edge.getY()][edge.getX()] = value;
        else vEdgeMatrix[edge.getY()][edge.getX()] = value;
    }
    public boolean isEdgeSet(Edge edge) { return getEdgeValue(edge) != ColorValue.TRANSPARENT; }

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

    public State getNextBoardState(Edge move) {
        State nextState = getClone();
        nextState.canModifyGame = false;
        nextState.makeMove(move);
        return nextState;
    }

    public boolean makeMove(Edge move) {
        if (move == null || !move.isValid() || isEdgeSet(move) || availableMoves.isEmpty()) return false;
        int x = move.getX(), y = move.getY();
        if (!previousMoves.empty()) setEdgeValue(previousMoves.peek(), ColorValue.BLACK);
        availableMoves.remove(move);
        previousMoves.push(move);
        previousPlayers.push(currentPlayer);
        if (availableMoves.isEmpty()) setEdgeValue(move, ColorValue.BLACK);
        else setEdgeValue(move, ColorValue.getLastEdgeColor(currentPlayer.getColorValue()));
        int score = getCurrentPlayerScore();
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
        if (playsAgain) {
            if (currentPlayer == player1) score1 = score;
            else score2 = score;
        } else {
            if (currentPlayer == player1) currentPlayer = player2;
            else currentPlayer = player1;
        }
        if (score1 + score2 == getMaxScore()) winner = score1 > score2 ? player1 : (score1 < score2 ? player2 : null);
        if (canModifyGame && availableMoves.isEmpty()) game.endGame();
        return true;
    }

    public boolean undoMove() {
        /*for (int i = 0; i <= height; ++i) {
            for (int j = 0; j < width; ++j) System.out.print(" " + (hEdgeMatrix[i][j] != ColorValue.TRANSPARENT ? "─" : "."));
            System.out.println();
            if (i < height) for (int j = 0; j <= width; ++j) System.out.print((vEdgeMatrix[i][j] != ColorValue.TRANSPARENT ? "|" : ".") + (j < width && boxMatrix[i][j] != ColorValue.TRANSPARENT ? "x" : " "));
            System.out.println();
        }*/
        if (previousMoves == null || previousMoves.empty() || !isEdgeSet(previousMoves.peek())) return false;
        Edge previousMove = previousMoves.pop();
        availableMoves.addLast(previousMove);
        currentPlayer = previousPlayers.pop();

        if (winner != null) winner = null;

        int x = previousMove.getX(), y = previousMove.getY();

        boolean playedAgain = false;
        int score = getCurrentPlayerScore();

        setEdgeValue(previousMove, ColorValue.TRANSPARENT);

        if (previousMove.isHorizontal()) {
            if (y > 0) {
                int edgeCount = getEdgeCount(x, y - 1);
                if (edgeCount == 3) {
                    playedAgain = true;
                    score--;
                    boxMatrix[y - 1][x] = ColorValue.TRANSPARENT;
                }
                boxEdgeCount[edgeCount + 1]--;
                boxEdgeCount[edgeCount]++;
            }
        } else {
            if (x > 0) {
                int edgeCount = getEdgeCount(x - 1, y);
                if (edgeCount == 3) {
                    playedAgain = true;
                    score--;
                    boxMatrix[y][x - 1] = ColorValue.TRANSPARENT;
                }
                boxEdgeCount[edgeCount + 1]--;
                boxEdgeCount[edgeCount]++;
            }
        }
        if (previousMove.isHorizontal() && y < height || !previousMove.isHorizontal() && x < width) {
            int edgeCount = getEdgeCount(x, y);
            if (edgeCount == 3) {
                playedAgain = true;
                score--;
                boxMatrix[y][x] = ColorValue.TRANSPARENT;
            }
            boxEdgeCount[edgeCount + 1]--;
            boxEdgeCount[edgeCount]++;
        }

        if (playedAgain) {
            if (currentPlayer == player1) score1 = score;
            else score2 = score;
        }

        if (!previousMoves.empty()) setEdgeValue(previousMoves.peek(), ColorValue.getLastEdgeColor(previousPlayers.peek().getColorValue()));

        if (winner != null) winner = null;

        /*setEdgeValue(previousMove, ColorValue.TRANSPARENT);
        int score = getCurrentPlayerScore();
        boolean playedAgain = true;
        if (previousMove.isHorizontal()) {
            if (y > 0) {
                int edgeCount = getEdgeCount(x, y - 1);
                boxEdgeCount[edgeCount + 1]--;
                boxEdgeCount[edgeCount]++;
                if (edgeCount == 3) {
                    boxMatrix[y - 1][x] = ColorValue.TRANSPARENT;
                    score--;
                    playedAgain = true;
                }
            }
        } else {
            if (x > 0) {
                int edgeCount = getEdgeCount(x - 1, y);
                boxEdgeCount[edgeCount + 1]--;
                boxEdgeCount[edgeCount]++;
                if (edgeCount == 3) {
                    boxMatrix[y][x - 1] = ColorValue.TRANSPARENT;
                    score--;
                    playedAgain = true;
                }
            }
        }
        if (previousMove.isHorizontal() && y < height || !previousMove.isHorizontal() && x < width) {
            int edgeCount = getEdgeCount(x, y);
            boxEdgeCount[edgeCount + 1]--;
            boxEdgeCount[edgeCount]++;
            if (edgeCount == 3) {
                boxMatrix[y][x] = ColorValue.TRANSPARENT;
                score--;
                playedAgain = true;
            }
        }
        if (playedAgain) {
            if (currentPlayer == player1) score1 = score;
            else score2 = score;
        } else {
            if (currentPlayer == player1) currentPlayer = player2;
            else currentPlayer = player1;
        }
        if (!previousMoves.empty()) setEdgeValue(previousMoves.peek(), ColorValue.getLastEdgeColor(currentPlayer.getColorValue()));
        if (winner != null) winner = null;*/
        return true;
    }

    public boolean undoMovesUntil(Edge move) {
        if (previousMoves == null || previousMoves.empty() || !isEdgeSet(move)) return false;
        while (!previousMoves.empty() && previousMoves.peek() != move) if (!undoMove()) return false;
        return true;
    }

    public int closeAllAvailableBoxes() {
        if (availableMoves.isEmpty() || boxEdgeCount[3] == 0) return 0;
        int boxCount = 0;
        boolean stateChanged = true;
        while (stateChanged) {
            stateChanged = false;
            int i = 0;
            while (i < availableMoves.size()) {
                if (addsNthEdge(availableMoves.get(i), 4)) {
                    makeMove(availableMoves.get(i));
                    boxCount++;
                    stateChanged = true;
                } else ++i;
            }
        }
        return boxCount;
    }

    public boolean nextMove(Edge move, boolean turnByTurn) {
        if (!makeMove(move)) return false;
        if (turnByTurn) closeAllAvailableBoxes();
        return true;
    }

    public boolean undoMove(Edge move, boolean turnByTurn) {
        if (turnByTurn) if (!undoMovesUntil(move)) return false;
        undoMove();
        return true;
    }
}