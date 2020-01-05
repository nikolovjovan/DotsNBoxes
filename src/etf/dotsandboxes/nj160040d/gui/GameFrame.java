package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.Game;

import javax.swing.*;

public class GameFrame extends JFrame {

    Game game;
    boolean gameInProgress;

    public GameFrame(Game game) {
        this.game = game;
        this.gameInProgress = false;
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGame() {
        clearContentPane();
        setContentPane(new GameContentPane(game));
        pack();
        gameInProgress = true;
    }

    public void showMainMenu() {
        clearContentPane();
        setContentPane(new MainMenuContentPane(game));
        pack();
        gameInProgress = false;
    }

    public void startThinking() {
        if (!gameInProgress) return;
        ((GameContentPane) getContentPane()).startThinking();
    }

    public void stopThinking() {
        if (!gameInProgress) return;
        ((GameContentPane) getContentPane()).stopThinking();
    }

    public void update() {
        if (!gameInProgress) return;
        ((GameContentPane) getContentPane()).update();
    }

    private void clearContentPane() {
        getContentPane().removeAll();
        revalidate();
        repaint();
    }

    private void initUI() {
        setTitle("Dots & Boxes");
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new MainMenuContentPane(game));
        pack();
    }
}