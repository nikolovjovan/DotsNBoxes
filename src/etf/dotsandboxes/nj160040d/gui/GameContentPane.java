package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.util.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class GameContentPane extends JPanel {

    Game game;

    ScorePanel scorePanel;
    JLabel messageLabel;
    GameBoardPanel gameBoardPanel;

    public GameContentPane(Game game) {
        this.game = game;
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startThinking() {
        messageLabel.setText("Thinking...");
        // TODO: Render something nicer perhaps?
    }

    public void stopThinking() {
        messageLabel.setText(" ");
        // TODO: Render something nicer perhaps?
    }

    public void update() {
        scorePanel.update();
        gameBoardPanel.update();
        if (game.isOver()) {
            if (game.getWinner() != null) {
                messageLabel.setForeground(ColorValue.valueToColor(ColorValue.getHighlightColor(game.getWinner().getColorValue())));
                messageLabel.setText(game.getWinner().getName() + " Won!");
            } else {
                messageLabel.setForeground(ColorValue.colorBlack);
                messageLabel.setText("The game was a tie!");
            }
        }
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 5, 5, 5);

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        scorePanel = new ScorePanel(game);
        add(scorePanel, constraints);

        ++constraints.gridy;
        add(SwingUtils.createEmptyLabel(new Dimension(50, 10)), constraints);

        ++constraints.gridy;
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 32));
        messageLabel.setForeground(ColorValue.colorBlack);
        add(messageLabel, constraints);

        ++constraints.gridy;
        add(SwingUtils.createEmptyLabel(new Dimension(50, 10)), constraints);

        ++constraints.gridy;
        gameBoardPanel = new GameBoardPanel(game, 10);
        add(gameBoardPanel, constraints);

        ++constraints.gridy;
        add(SwingUtils.createEmptyLabel(new Dimension(500, 50)), constraints);

        JButton endButton = new JButton("End Game");
        endButton.addActionListener(e -> game.showMainMenu());
        ++constraints.gridy;
        add(endButton, constraints);

        ++constraints.gridy;
        add(SwingUtils.createEmptyLabel(new Dimension(500, 10)), constraints);
    }
}