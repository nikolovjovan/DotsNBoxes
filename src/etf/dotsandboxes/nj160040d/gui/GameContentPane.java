package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.logic.Game;

import javax.swing.*;
import java.awt.*;

public class GameContentPane extends JPanel {

    GameBoardPanel gameBoardPanel;

    public GameContentPane() {
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;

        gameBoardPanel = new GameBoardPanel(Game.getBoardWidth(), Game.getBoardHeight(), 25);
        add(gameBoardPanel, constraints);
    }

}