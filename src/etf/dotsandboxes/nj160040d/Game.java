package etf.dotsandboxes.nj160040d;

import etf.dotsandboxes.nj160040d.gui.GameFrame;
import etf.dotsandboxes.nj160040d.logic.*;

import java.awt.EventQueue;

public class Game implements Runnable {

    public enum Mode { PvC, PvP, CvC_STEP, CvC_QUICK }

    private static final long moveTime = 500000000; // 500 ms

    private Thread thread;
    private GameFrame gameFrame;
    private State state;
    private Mode mode;
    private boolean started, playerDone, nextStep, undoMove, over, showMainMenu;

    public Game() {
        this.thread = new Thread(this);
        this.gameFrame = new GameFrame(this);
        this.started = this.playerDone = this.nextStep = this.undoMove = this.over = this.showMainMenu = false;
    }

    public State getState() { return state; }
    public Mode getMode() { return mode; }

    public boolean isOver() { return over; }

    public void startThread() {
        if (thread.getState() != Thread.State.NEW) return;
        thread.start();
    }

    public void startGame(State state, Mode mode) {
        this.state = state;
        this.mode = mode;
        started = true;
        if (!Thread.currentThread().equals(thread)) thread.interrupt();
    }

    public void playerDone() {
        playerDone = true;
        if (!Thread.currentThread().equals(thread)) thread.interrupt();
    }

    public void nextStep() {
        if (over) return;
        nextStep = true;
        undoMove = false;
        if (!Thread.currentThread().equals(thread)) thread.interrupt();
    }

    public void undo() {
        if (over) return;
        nextStep = true;
        undoMove = true;
        if (!Thread.currentThread().equals(thread)) thread.interrupt();
    }

    public void endGame() {
        over = true;
        if (!Thread.currentThread().equals(thread)) thread.interrupt();
    }

    public void showMainMenu() {
        showMainMenu = over = nextStep = playerDone = true;
        if (!Thread.currentThread().equals(thread)) thread.interrupt();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        gameFrame.setVisible(true);
        while (true) {
            try {
                while (!started) Thread.sleep(60000);
            } catch (InterruptedException e) {
                if (!started) System.err.println("Error! Game is not started but thread is interrupted!");
            }
            gameFrame.startGame();
            while (!over) {
                playerDone = false;
                try {
                    if (state.getCurrentPlayer().getType() == Player.Type.HUMAN) {
                        while (!playerDone) Thread.sleep(60000);
                    } else {
                        gameFrame.startThinking();
                        long computeTime = ((AIPlayer) state.getCurrentPlayer()).computeNextMove();
                        if (mode == Mode.PvC || mode == Mode.PvP) {
                            if (computeTime < moveTime) {
                                long timeLeft = moveTime - computeTime;
                                Thread.sleep(timeLeft / 1000000, (int) (timeLeft % 1000000));
                            }
                        }
                        gameFrame.stopThinking();
                    }
                } catch (InterruptedException e) {
                    if (over) break;
                    if (!playerDone) System.err.println("Error! Player not done but thread is interrupted!");
                }
                if (mode == Mode.CvC_STEP) {
                    nextStep = false;
                    try {
                        while (!nextStep) Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        if (over) break;
                        if (!nextStep) System.err.println("Error! Next step not activated but thread is interrupted!");
                    }
                }
                if (undoMove) {
                    if (state.getPreviousMoves().empty()) continue;
                    if (state.undoMove()) gameFrame.update();
                    else System.err.println("Error! Failed to undo move: " + state.getPreviousMoves().peek());
                } else {
                    if (state.getAvailableMovesCount() == 0) break;
                    if (state.makeMove(state.getCurrentPlayer().getNextMove())) {
                        gameFrame.update();
                        if (state.getWinner() != null) over = true;
                    }
                    else System.err.println("Error! Failed to make move: " + state.getCurrentPlayer().getNextMove());
                }
            }
            try {
                while (!showMainMenu) Thread.sleep(60000);
            } catch (InterruptedException e) {
                if (!showMainMenu) System.err.println("Error! Game is not finished but thread is interrupted!");
            }
            gameFrame.showMainMenu();
            started = playerDone = nextStep = undoMove = over = showMainMenu = false;
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