package etf.dotsandboxes.nj160040d;

import etf.dotsandboxes.nj160040d.gui.GameFrame;
import etf.dotsandboxes.nj160040d.logic.AIPlayer;
import etf.dotsandboxes.nj160040d.logic.Player;
import etf.dotsandboxes.nj160040d.logic.State;

import java.awt.EventQueue;

public class Game implements Runnable {

    private Thread thread;
    private GameFrame gameFrame;
    private State state;
    private boolean started, playerDone, over, showMainMenu;

    public Game() {
        this.thread = new Thread(this);
        this.gameFrame = new GameFrame(this);
        this.started = false;
        this.playerDone = false;
        this.over = false;
        this.showMainMenu = false;
    }

    public State getState() { return state; }

    public boolean isOver() { return over; }

    public void startThread() {
        if (thread.getState() != Thread.State.NEW) return;
        thread.start();
    }

    public void startGame(State state) {
        this.state = state;
        started = true;
        if (!Thread.currentThread().equals(thread)) thread.interrupt();
    }

    public void playerDone() {
        playerDone = true;
        if (!Thread.currentThread().equals(thread)) thread.interrupt();
    }

    public void endGame() {
        over = true;
        if (!Thread.currentThread().equals(thread)) thread.interrupt();
    }

    public void showMainMenu() {
        showMainMenu = over = playerDone = true;
        if (!Thread.currentThread().equals(thread)) thread.interrupt();
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
            gameFrame.startGame();
            while (!over) {
                playerDone = false;
                try {
                    if (state.getCurrentPlayer().getType() == Player.Type.HUMAN) {
                        while (!playerDone) Thread.sleep(60000);
                    } else {
                        // This is now being executed on this thread
                        ((AIPlayer) state.getCurrentPlayer()).computeNextMove();
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
                state.playerDrawEdge(state.getCurrentPlayer().getNextMove());
                gameFrame.update();
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

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                new Game().startThread();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}