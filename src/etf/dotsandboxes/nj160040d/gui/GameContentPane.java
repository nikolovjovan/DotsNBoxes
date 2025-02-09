package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.logic.AIPlayer;
import etf.dotsandboxes.nj160040d.logic.Edge;
import etf.dotsandboxes.nj160040d.logic.Node;
import etf.dotsandboxes.nj160040d.logic.State;
import etf.dotsandboxes.nj160040d.util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

public class GameContentPane extends JPanel {

    private static final String thinkingMessage = "Thinking...";
    private static final String tieMessage = "It was a tie!";
    private static final String gameInterruptedMessage = "Game was interrupted!";
    private static final Font messageFont = new Font("Arial", Font.BOLD, 32);

    private Game game;

    private JPanel headerPanel, contentPanel, footerPanel;
    private ScorePanel scorePanel;
    private GameBoardPanel gameBoardPanel;
    private JLabel nextMoveLabel, moveLabel, heuristicLabel;
    private DefaultListModel<String> movesListModel;
    private JList<String> movesList;
    private JScrollBar movesVerticalScrollBar;
    private JLabel messageLabel;
    private GridBagConstraints messageLabelConstraints;

    private String message;
    private Color messageColor;
    private boolean messageOnScorePanel;

    public GameContentPane(Game game) {
        this.game = game;
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startThinking() { showMessage(thinkingMessage, ColorValue.colorBlack); }
    public void stopThinking() {
        showMessage(null, null);
        if (game.getMode() == Game.Mode.CvC_STEP) {
            nextMoveLabel.setText("Next move: " + game.getState().getCurrentPlayer().getNextMove());
        }
    }

    public void update() {
        scorePanel.update();
        gameBoardPanel.update();
        if (game.getState().getPreviousMoves().size() > movesListModel.size()) {
            Edge previousMove = game.getState().getPreviousMoves().peek();
            movesListModel.addElement(previousMove.toString() + " (" + Edge.generateStringFromEdge(previousMove) + ")");
        } else if (!movesListModel.isEmpty()) {
            movesListModel.removeElementAt(movesListModel.getSize() - 1);
        }
        movesList.setSelectedIndex(movesListModel.getSize() - 1);
        movesVerticalScrollBar.setValue(movesVerticalScrollBar.getMaximum());
        if (game.isOver()) {
            if (game.getState().getWinner() != null) {
                showMessage(game.getState().getWinner().getName() + " Won!",
                        ColorValue.valueToColor(ColorValue.getLastEdgeColor(game.getState().getWinner().getColorValue())));
            } else if (game.getState().getPlayer1Score() == game.getState().getPlayer2Score()) {
                showMessage(tieMessage, ColorValue.colorBlack);
            } else {
                showMessage(gameInterruptedMessage, ColorValue.colorBlack);
            }
        }
    }

    public void showHeuristic(Edge move) {
        if (game.getMode() != Game.Mode.CvC_STEP) return;
        moveLabel.setText("Move: " + move + " (" + Edge.generateStringFromEdge(move) + ")");
        List<Node> nodes = ((AIPlayer) game.getState().getCurrentPlayer()).getHeuristics();
        if (nodes == null || nodes.isEmpty()) heuristicLabel.setText("Heuristic: None");
        else {
            Node selectedNode = null;
            for (Node node : nodes)
                if (node.getMove().equals(move)) {
                    selectedNode = node;
                    break;
                }
            heuristicLabel.setText("Heuristic: " + (selectedNode == null ? "None" : selectedNode.getHeuristic()));
        }
    }

    private void updateMessageRenderer(String message) {
        if (scorePanel.canRenderMessage(message)) {
            if (!messageOnScorePanel) {
                messageOnScorePanel = true;
                messageLabel.setText(" ");
                headerPanel.remove(messageLabel);
                revalidate();
                repaint();
            }
        } else {
            if (messageOnScorePanel) {
                messageOnScorePanel = false;
                scorePanel.setMessage(null);
                headerPanel.add(messageLabel, messageLabelConstraints);
                revalidate();
                repaint();
            }
        }
    }

    private void showMessage(String message, Color color) {
        this.message = message;
        this.messageColor = color;
        updateMessageRenderer(message);
        if (message == null || message.isEmpty()) {
            scorePanel.setMessage(null);
            messageLabel.setText(" ");
            return;
        }
        if (messageOnScorePanel) {
            scorePanel.setMessageColor(color);
            scorePanel.setMessage(message);
        } else {
            messageLabel.setForeground(color);
            messageLabel.setText(message);
        }
    }

    private void initHeader() {
        headerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = SwingUtils.createConstraints(5, true);

        constraints.weighty = 0;

        scorePanel = new ScorePanel(game.getState());
        scorePanel.setMessageFont(messageFont);
        SwingUtils.addComponentVertically(headerPanel, scorePanel, constraints);

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
        contentPanel = new JPanel(new BorderLayout());

        gameBoardPanel = new GameBoardPanel(game, this);

        JPanel analyticsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints analyticsPanelConstraints = SwingUtils.createConstraints(5, true);
        analyticsPanelConstraints.weightx = analyticsPanelConstraints.weighty = 0;
        SwingUtils.addHorizontalSpacer(analyticsPanel, analyticsPanelConstraints, 20);
        if (game.getMode() == Game.Mode.CvC_STEP) {
            nextMoveLabel = new JLabel("Move: ");
            nextMoveLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nextMoveLabel.setPreferredSize(new Dimension(150, nextMoveLabel.getPreferredSize().height));
            SwingUtils.addComponentVertically(analyticsPanel, nextMoveLabel, analyticsPanelConstraints);
            moveLabel = new JLabel("Move: ");
            moveLabel.setFont(new Font("Arial", Font.BOLD, 16));
            moveLabel.setPreferredSize(new Dimension(150, moveLabel.getPreferredSize().height));
            SwingUtils.addComponentVertically(analyticsPanel, moveLabel, analyticsPanelConstraints);
            heuristicLabel = new JLabel("Heuristic: ");
            heuristicLabel.setPreferredSize(new Dimension(150, heuristicLabel.getPreferredSize().height));
            heuristicLabel.setFont(new Font("Arial", Font.BOLD, 16));
            SwingUtils.addComponentVertically(analyticsPanel, heuristicLabel, analyticsPanelConstraints);
        }
        JLabel movesLabel = new JLabel("Previous moves:");
        movesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        movesLabel.setPreferredSize(new Dimension(150, movesLabel.getPreferredSize().height));
        SwingUtils.addComponentVertically(analyticsPanel, movesLabel, analyticsPanelConstraints);
        movesListModel = new DefaultListModel<>();
        movesList = new JList<>(movesListModel);
        movesList.setFixedCellWidth(150);
        ((DefaultListCellRenderer) movesList.getCellRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane movesListScrollPane = new JScrollPane(movesList);
        movesListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        movesListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        movesVerticalScrollBar = movesListScrollPane.getVerticalScrollBar();
        analyticsPanelConstraints.weighty = 1;
        SwingUtils.addComponentVertically(analyticsPanel, movesListScrollPane, analyticsPanelConstraints);

        contentPanel.add(SwingUtils.createVerticalSpacer(10), BorderLayout.NORTH);
        contentPanel.add(SwingUtils.createHorizontalSpacer(analyticsPanel.getPreferredSize().width), BorderLayout.WEST);
        contentPanel.add(gameBoardPanel, BorderLayout.CENTER);
        contentPanel.add(analyticsPanel, BorderLayout.EAST);
        contentPanel.add(SwingUtils.createVerticalSpacer(10), BorderLayout.SOUTH);
    }

    private void initFooter() {
        footerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = SwingUtils.createConstraints(5, true);
        constraints.anchor = GridBagConstraints.CENTER;

        constraints.weighty = 0;
        SwingUtils.addVerticalSpacer(footerPanel, constraints, 10);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints buttonPanelConstraints = SwingUtils.createConstraints(5, false);
        if (game.getMode() == Game.Mode.CvC_STEP) {
            JButton nextStepButton = new JButton("Next Move");
            nextStepButton.setFont(new Font("Arial", Font.PLAIN, 40));
            nextStepButton.addActionListener(e -> game.nextStep());
            SwingUtils.addComponentHorizontally(buttonPanel, nextStepButton, buttonPanelConstraints);
            JButton undoButton = new JButton("Undo Move");
            undoButton.setFont(new Font("Arial", Font.PLAIN, 40));
            undoButton.addActionListener(e -> game.undo());
            SwingUtils.addComponentHorizontally(buttonPanel, undoButton, buttonPanelConstraints);
        }
        JButton endButton = new JButton("End Game");
        endButton.setFont(new Font("Arial", Font.PLAIN, 40));
        endButton.addActionListener(e -> {
            if (!game.isOver()) {
                if (JOptionPane.showConfirmDialog(this,
                        "Game is not over yet. Are you sure you want to end it?",
                        "Question?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ==
                        JOptionPane.NO_OPTION) return;
            }
            if (JOptionPane.showConfirmDialog(this,
                    "Do you want to save game state to a file?",
                    "Question?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ==
                    JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter stateFilter = new FileNameExtensionFilter("Game state file (*.state)", "state");
                FileNameExtensionFilter textFilter = new FileNameExtensionFilter("Normal text file (*.txt)", "txt");
                fileChooser.addChoosableFileFilter(stateFilter);
                fileChooser.addChoosableFileFilter(textFilter);
                fileChooser.setFileFilter(stateFilter);
                boolean invalid = true;
                while (invalid) {
                    int returnVal = fileChooser.showSaveDialog(this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                        if (fileChooser.getFileFilter() == stateFilter) filePath += ".state";
                        else if (fileChooser.getFileFilter() == textFilter) filePath += ".txt";
                        if (State.tryExportGameStateToFile(game.getState(), filePath)) {
                            invalid = false;
                            JOptionPane.showMessageDialog(this,
                                    "Successfully exported game state file!",
                                    "Info", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            if (JOptionPane.showConfirmDialog(this,
                                    "Failed to export game state file! Please try another again.",
                                    "Error!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE) ==
                                    JOptionPane.CANCEL_OPTION) {
                                if (JOptionPane.showConfirmDialog(this,
                                        "Are you sure you want to cancel?",
                                        "Question?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ==
                                        JOptionPane.YES_OPTION) invalid = false;
                            }
                        }
                    } else {
                        if (JOptionPane.showConfirmDialog(this,
                                "Are you sure you want to cancel?",
                                "Question?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ==
                                JOptionPane.YES_OPTION) invalid = false;
                    }
                }
            }
            game.showMainMenu();
        });
        SwingUtils.addComponentHorizontally(buttonPanel, endButton, buttonPanelConstraints);

        constraints.weighty = 1;
        SwingUtils.addComponentVertically(footerPanel, buttonPanel, constraints);

        constraints.weighty = 0;
        SwingUtils.addVerticalSpacer(footerPanel, constraints, 10);

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