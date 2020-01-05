package etf.dotsandboxes.nj160040d;

import etf.dotsandboxes.nj160040d.gui.GameFrame;
import etf.dotsandboxes.nj160040d.logic.AIPlayer;
import etf.dotsandboxes.nj160040d.logic.Board;
import etf.dotsandboxes.nj160040d.logic.Player;

import java.awt.EventQueue;

public class Game extends Thread {

    private GameFrame gameFrame;
    private Board board;
    private Player player1, player2, currentPlayer, winner, loser;
    private boolean started, playerDone, over, showMainMenu;
    private int turn;

    public Game() {
        this.gameFrame = new GameFrame(this);
        this.started = false;
        this.playerDone = false;
        this.over = false;
        this.showMainMenu = false;
        this.turn = 0;
    }

    @Override
    public void run() {
        gameFrame.setVisible(true);
        while (true) {
            try {
                while (!started) Thread.sleep(60000);
            } catch (InterruptedException e) {
                if (!started) {
                    System.out.println("Error! Game is not started but thread is interrupted!");
                    return;
                }
            }
            currentPlayer = player1;
            gameFrame.startGame();
            while (!over) {
                gameFrame.update();
                playerDone = false;
                try {
                    if (getCurrentPlayer().getType() == Player.Type.HUMAN) {
                        while (!playerDone) Thread.sleep(60000);
                    } else {
                        // This is now being executed on this thread
                        ((AIPlayer) getCurrentPlayer()).computeNextMove();
                        // We may put the thread to sleep to simulate thinking time...
                        gameFrame.startThinking();
                        Thread.sleep(500);
                        gameFrame.stopThinking();
                    }
                } catch (InterruptedException e) {
                    if (!playerDone) {
                        System.out.println("Error! Player not done but thread is interrupted!");
                        return;
                    }
                }
                if (!board.playerDrawEdge(getCurrentPlayer().getNextMove())) {
                    currentPlayer = currentPlayer.equals(player1) ? player2 : player1;
                }
                turn++;
            }
            if (player1.getScore() + player2.getScore() == board.getMaxScore()) {
                // game was not interrupted before being completed
                if (player1.getScore() > player2.getScore()) { // player 1 wins
                    winner = player1;
                    loser = player2;
                } else if (player1.getScore() < player2.getScore()) { // player 2 wins
                    winner = player2;
                    loser = player1;
                } else { // tie
                    winner = loser = null;
                }
            }
            try {
                while (!showMainMenu) Thread.sleep(60000);
            } catch (InterruptedException e) {
                if (!showMainMenu) {
                    System.out.println("Error! Game is not finished but thread is interrupted!");
                    return;
                }
            }
            gameFrame.showMainMenu();
            started = false;
            over = false;
            showMainMenu = false;
        }
    }

    public Board getBoard() { return board; }

    public Player getPlayer1() { return player1; }
    public Player getPlayer2() { return player2; }

    public Player getCurrentPlayer() { return currentPlayer; }
    public Player getWinner() { return winner; }
    public Player getLoser() { return loser; }

    public boolean isStarted() { return started; }
    public boolean isOver() { return over; }

    public int getTurn() { return turn; }

    public void startGame(Board board, Player player1, Player player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        started = true;
        if (!Thread.currentThread().equals(this)) interrupt();
    }

    public void playerDone() {
        playerDone = true;
        if (!Thread.currentThread().equals(this)) interrupt();
    }

    public void endGame() {
        over = true;
        if (!Thread.currentThread().equals(this)) interrupt();
    }

    public void showMainMenu() {
        showMainMenu = over = playerDone = true;
        if (!Thread.currentThread().equals(this)) interrupt();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new Game().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}