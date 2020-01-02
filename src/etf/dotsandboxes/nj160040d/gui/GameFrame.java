package etf.dotsandboxes.nj160040d.gui;

import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame() {
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMainMenu() {
        clearContentPane();
        setContentPane(new MainMenuContentPane());
        pack();
    }

    public void showGameBoard() {
        clearContentPane();
        setContentPane(new GameContentPane());
        pack();
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
        setContentPane(new MainMenuContentPane());
        pack();
    }
}