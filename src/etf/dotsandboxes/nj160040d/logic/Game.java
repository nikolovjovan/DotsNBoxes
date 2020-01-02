package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.gui.GameFrame;

import java.awt.EventQueue;

public class Game {

    private static GameFrame gameFrame;
    private static int boardWidth, boardHeight;
    private static Player player1, player2;
    private static int turn;
    private static boolean[][] hEdgeMatrix, vEdgeMatrix, boxMatrix;

    private Game() {}

    public static int getBoardWidth() { return boardWidth; }
    public static int getBoardHeight() { return boardHeight; }

    public static Player getPlayer1() { return player1; }
    public static Player getPlayer2() { return player2; }

    public static int getTurn() { return turn; }

    public static void nextTurn() {
        turn++;
    }

    public static boolean[][] getHEdgeMatrix() { return hEdgeMatrix; }
    public static boolean[][] getVEdgeMatrix() { return vEdgeMatrix; }
    public static boolean[][] getBoxMatrix() { return boxMatrix; }

    public static void startGame(int boardWidth, int boardHeight, Player player1, Player player2) {
        Game.boardWidth = boardWidth;
        Game.boardHeight = boardHeight;
        Game.player1 = player1;
        Game.player2 = player2;
        turn = 0; // even = player 1, odd = player 2
        hEdgeMatrix = new boolean[boardHeight + 1][boardWidth];
        vEdgeMatrix = new boolean[boardHeight][boardWidth + 1];
        boxMatrix = new boolean[boardHeight][boardWidth];
        gameFrame.showGameBoard();
    }

    public static void endGame() {
        gameFrame.showMainMenu();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                gameFrame = new GameFrame();
                gameFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}