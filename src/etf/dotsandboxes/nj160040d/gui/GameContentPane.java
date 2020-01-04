package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.util.SwingUtils;

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

        gameBoardPanel = new GameBoardPanel(Game.getBoard(), 25);
        add(gameBoardPanel, constraints);

        ++constraints.gridy;
        add(SwingUtils.createEmptyLabel(new Dimension(500, 50)), constraints);

        JButton endButton = new JButton("End Game");
        endButton.addActionListener(e -> Game.endGame());
        ++constraints.gridy;
        add(endButton, constraints);

        ++constraints.gridy;
        add(SwingUtils.createEmptyLabel(new Dimension(500, 10)), constraints);
    }

}