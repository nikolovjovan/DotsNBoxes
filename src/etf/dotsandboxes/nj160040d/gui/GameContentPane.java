package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GameContentPane extends JPanel {

    static final String thinkingMessage = "Thinking...";
    static final Font messageFont = new Font("Arial", Font.BOLD, 32);

    Game game;

    JPanel headerPanel, contentPanel, footerPanel;
    ScorePanel scorePanel;
    GameBoardPanel gameBoardPanel;
    JLabel messageLabel;
    String message;
    Color messageColor;
    boolean messageOnScorePanel;

    GridBagConstraints messageLabelConstraints;

    public GameContentPane(Game game) {
        this.game = game;
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startThinking() { showMessage(thinkingMessage, ColorValue.colorBlack); }
    public void stopThinking() { showMessage(null, null); }

    public void update() {
        scorePanel.update();
        gameBoardPanel.update();
        if (game.isOver()) {
            if (game.getWinner() != null) {
                showMessage(game.getWinner().getName() + " Won!",
                        ColorValue.valueToColor(ColorValue.getLastEdgeColor(game.getWinner().getColorValue())));
            } else if (game.getPlayer1().getScore() == game.getPlayer2().getScore()) {
                showMessage("It is a tie!", ColorValue.colorBlack);
            } else {
                showMessage("Game was interrupted!", ColorValue.colorBlack);
            }
        }
    }

    private void showMessage(String message, Color color) {
        if (message == null) {
            this.message = null;
            this.messageColor = ColorValue.colorBlack;
            scorePanel.setMessage(null);
            messageLabel.setText(null);
            return;
        }
        this.message = message;
        this.messageColor = color;
        if (scorePanel.canRenderMessage(message)) {
            if (!messageOnScorePanel) {
                headerPanel.remove(messageLabel);
                headerPanel.setPreferredSize(new Dimension(
                        headerPanel.getPreferredSize().width,
                        headerPanel.getPreferredSize().height - messageLabel.getPreferredSize().height));
                messageLabel.setText(null);
                revalidate();
                repaint();
            }
            scorePanel.setMessageColor(color);
            scorePanel.setMessage(message);
            messageOnScorePanel = true;
        } else {
            if (messageOnScorePanel) {
                headerPanel.add(messageLabel, messageLabelConstraints);
                scorePanel.setMessage(null);
                revalidate();
                repaint();
            }
            messageLabel.setForeground(color);
            messageLabel.setText(message);
            messageOnScorePanel = false;
        }
    }

    private void initHeader() {
        headerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = SwingUtils.createConstraints(5, true);

        constraints.weighty = 0;

        scorePanel = new ScorePanel(game);
        scorePanel.setMessageFont(messageFont);
        SwingUtils.addComponentVertically(headerPanel, scorePanel, constraints);

        SwingUtils.addVerticalSpacer(headerPanel, constraints, 10);

        messageLabel = new JLabel(" ");
        messageLabel.setFont(messageFont);
        messageLabel.setForeground(ColorValue.colorBlack);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        SwingUtils.addComponentVertically(headerPanel, messageLabel, constraints);

        messageLabelConstraints = ((GridBagLayout) headerPanel.getLayout()).getConstraints(messageLabel);

        headerPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                showMessage(message, messageColor);
            }
        });
    }

    private void initContent() {
        contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = SwingUtils.createConstraints(5, true);

        constraints.weighty = 0;
        SwingUtils.addVerticalSpacer(contentPanel, constraints, 10);

        gameBoardPanel = new GameBoardPanel(game);
        constraints.weighty = 1;
        SwingUtils.addComponentVertically(contentPanel, gameBoardPanel, constraints);

        constraints.weighty = 0;
        SwingUtils.addVerticalSpacer(contentPanel, constraints, 10);

        JPanel endButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints endButtonPanelConstraints = SwingUtils.createConstraints(5, false);
        JButton endButton = new JButton("End Game");
        endButton.setFont(new Font("Arial", Font.PLAIN, 40));
        endButton.addActionListener(e -> game.showMainMenu());
        SwingUtils.addComponentVertically(endButtonPanel, endButton, endButtonPanelConstraints);

        constraints.weighty = 0;
        SwingUtils.addComponentVertically(contentPanel, endButtonPanel, constraints);
    }

    private void initFooter() {
        footerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = SwingUtils.createConstraints(5, true);
        constraints.anchor = GridBagConstraints.CENTER;

        constraints.weighty = 0;
        SwingUtils.addVerticalSpacer(contentPanel, constraints, 10);

        JLabel copyrightLabel = new JLabel("January 2020 - Jovan Nikolov 2016/0040");
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        copyrightLabel.setForeground(Color.GRAY);
        copyrightLabel.setHorizontalAlignment(JLabel.CENTER);

        constraints.weighty = 0;
        SwingUtils.addComponentVertically(footerPanel, copyrightLabel, constraints);
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initHeader();
        initContent();
        initFooter();

        add(headerPanel, BorderLayout.PAGE_START);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.PAGE_END);
    }
}