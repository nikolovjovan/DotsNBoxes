package etf.dotsandboxes.nj160040d;

import etf.dotsandboxes.nj160040d.gui.GameFrame;
import etf.dotsandboxes.nj160040d.logic.Board;
import etf.dotsandboxes.nj160040d.logic.Player;

import java.awt.EventQueue;

public class Game {

    private static GameFrame gameFrame;
    private static Board board;
    private static Player player1, player2;
    private static int turn;

    private Game() {}

    public static Board getBoard() { return board; }

    public static Player getPlayer1() { return player1; }
    public static Player getPlayer2() { return player2; }

    public static int getTurn() { return turn; }

    public static void nextTurn() {
        // TODO: add turn change logic
        turn++;
    }

    public static void startGame(Board board, Player player1, Player player2) {
        Game.board = board;
        Game.player1 = player1;
        Game.player2 = player2;
        turn = 1; // odd = player 1, even = player 2
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