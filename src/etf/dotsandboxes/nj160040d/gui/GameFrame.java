package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.util.SwingUtils;

import javax.swing.*;
import java.awt.event.*;

public class GameFrame extends JFrame {

    private static final String iconFileName = "res/icon.png";
    private static final int[] iconSizes = { 16, 20, 24, 32, 40, 48, 64, 128, 256, 512, 1024 };

    private Game game;
    private boolean gameInProgress;

    public GameFrame(Game game) {
        this.game = game;
        this.gameInProgress = false;
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startGame() { changeContent(true); }
    public void showMainMenu() { changeContent(false); }

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

    private void changeContent(boolean gameInProgress) {
        clearContent();
        setContent(gameInProgress);
    }

    private void clearContent() {
        getContentPane().removeAll();
        revalidate();
        repaint();
    }

    private void setContent(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
        boolean wasMaximized = (getExtendedState() & MAXIMIZED_BOTH) != 0;
        setContentPane(gameInProgress ? new GameContentPane(game) : new MainMenuContentPane(game));
        if (wasMaximized) {
            setMinimumSize(getPreferredSize());
            setExtendedState(MAXIMIZED_BOTH);
            revalidate();
            repaint();
        } else {
            setLocationRelativeTo(null);
            pack();
            setMinimumSize(getSize());
        }
    }

    private void initUI() {
        setTitle("Dots and Boxes");
        setIconImages(SwingUtils.createIconImages(iconFileName, iconSizes));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContent(false);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                getContentPane().setSize(e.getComponent().getSize());
            }
        });
        addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                if ((e.getNewState() & MAXIMIZED_BOTH) != 0) {
                    getContentPane().setSize(e.getComponent().getSize());
                }
            }
        });
    }
}