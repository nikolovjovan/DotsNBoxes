package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.logic.*;
import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.util.SwingUtils;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class MainMenuContentPane extends JPanel {

    private static final byte[] playerColors = { ColorValue.BLUE, ColorValue.RED };
    private static final String[] aiDifficulties;

    private static final String logoFileName = "res/title.png";
    private static final String titleText = "Dots & Boxes";
    private static final String titleFontName = "Comic Sans MS";
    private static final int titleFontSize = 60;

    private Game game;

    private JPanel headerPanel, contentPanel, footerPanel;

    private BufferedImage logo;
    private JTextField gameStateFileTextField;
    private SpinnerModel boardWidthModel, boardHeightModel;
    private JPanel speedPanel;
    private JRadioButton modePvCRadioButton, modePvPRadioButton, modeCvCRadioButton, speedStep;
    private JTextField[] playerNameTextField;
    private SpinnerModel[] aiPlayerDifficultyModel, aiPlayerTreeDepthModel;
    private JPanel boardSizePanel;
    private JPanel[] aiPlayerPanel;

    private String gameStateFileName;

    static {
        AIPlayer.Difficulty[] difficulties = AIPlayer.Difficulty.values();
        aiDifficulties = new String[difficulties.length];
        for (int i = 0; i < difficulties.length; ++i) aiDifficulties[i] = difficulties[i].toString();
    }

    public MainMenuContentPane(Game game) {
        this.game = game;
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isPlayerAI(int playerIndex) {
        if (playerIndex < 0 || playerIndex > 1) return false;
        return playerIndex == 0 && modeCvCRadioButton.isSelected() ||
                playerIndex == 1 && (modePvCRadioButton.isSelected() || modeCvCRadioButton.isSelected());
    }

    private String getDefaultPlayerName(int playerIndex) {
        if (playerIndex < 0 || playerIndex > 1) return null;
        String name = isPlayerAI(playerIndex) ? aiPlayerDifficultyModel[playerIndex].getValue() + " AI" : "Human";
        return name + " Player " + (playerIndex + 1);
    }

    private String getPlayerName(int playerIndex) {
        if (playerIndex < 0 || playerIndex > 1) return null;
        return playerNameTextField[playerIndex].getText().isEmpty() ?
                getDefaultPlayerName(playerIndex) :
                playerNameTextField[playerIndex].getText();
    }

    private void updatePlayerName(int playerIndex) {
        if (playerIndex < 0 || playerIndex > 1) return;
        String playerName = playerNameTextField[playerIndex].getText();
        boolean isDefault = false;
        if (playerName.equals("Human Player " + (playerIndex + 1))) isDefault = true;
        if (!isDefault)
            for (String aiDifficulty : aiDifficulties)
                if (playerName.equals(aiDifficulty + " AI Player " + (playerIndex + 1))) {
                    isDefault = true;
                    break;
                }
        if (isDefault && !playerName.equals(getDefaultPlayerName(playerIndex)))
            playerNameTextField[playerIndex].setText(getDefaultPlayerName(playerIndex));
    }

    private ActionListener modeRadioButtonActionListener = e -> {
        for (int i = 0; i < 2; ++i) {
            SwingUtils.setPanelEnabled(aiPlayerPanel[i], isPlayerAI(i));
            SwingUtils.setPanelEnabled(speedPanel, modeCvCRadioButton.isSelected());
            updatePlayerName(i);
        }
    };

    private ChangeListener playerDifficultySpinnerChangeListener = e -> {
        for (int i = 0; i < 2; ++i) updatePlayerName(i);
    };

    private void initHeader() {
        headerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = SwingUtils.createConstraints(5, true);

        logo = SwingUtils.loadImage(logoFileName);
        JLabel titleLabel = new JLabel();
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        if (logo != null) {
            titleLabel.setIcon(new ImageIcon(SwingUtils.resizeImageToFit(logo, new Dimension(400, 300), true)));
        } else {
            titleLabel.setText(titleText);
            titleLabel.setFont(new Font(titleFontName, Font.BOLD, titleFontSize));
        }

        constraints.weighty = 1;
        SwingUtils.addVerticalSpacer(headerPanel, constraints, 10);

        constraints.weightx = 30;
        SwingUtils.addHorizontalSpacer(headerPanel, constraints, 20);
        constraints.weightx = 1;
        SwingUtils.addComponentHorizontally(headerPanel, titleLabel, constraints);
        constraints.weightx = 30;
        SwingUtils.addHorizontalSpacer(headerPanel, constraints, 20);

        constraints.gridx = 0;
        constraints.gridy++;

        constraints.weightx = constraints.weighty = 1;
        SwingUtils.addVerticalSpacer(headerPanel, constraints, 10);

        headerPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (logo == null) {
                    int tempFontSize = titleFontSize;
                    JLabel tempLabel = new JLabel();
                    Font tempFont = new Font(titleFontName, Font.BOLD, tempFontSize);
                    Dimension availableSize = titleLabel.getSize(),
                            tempTextSize = SwingUtils.getTextSize(tempLabel, titleText, tempFont);
                    while (tempTextSize.width < availableSize.width &&
                            tempTextSize.height < availableSize.height) {
                        tempFontSize++;
                        tempFont = new Font(titleFontName, Font.BOLD, tempFontSize);
                        tempTextSize = SwingUtils.getTextSize(tempLabel, titleText, tempFont);
                    }
                    titleLabel.setFont(new Font(titleFontName, Font.BOLD, tempFontSize - 1));
                } else {
                    titleLabel.setIcon(new ImageIcon(SwingUtils.resizeImageToFit(logo, titleLabel.getSize(), false)));
                }
            }
        });
    }

    private void initContent() {
        contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = SwingUtils.createConstraints(5, false);

        gameStateFileTextField = new JTextField("Click the 'Open' button to choose a file");
        gameStateFileTextField.setEditable(false);

        JButton gameStateFileOpenButton = new JButton("Open");
        gameStateFileOpenButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                gameStateFileName = fileChooser.getSelectedFile().getName();
                gameStateFileTextField.setText(gameStateFileName);
                SwingUtils.setPanelEnabled(boardSizePanel, false);
            }
        });

        JButton gameStateFileCancelButton = new JButton("Cancel");
        gameStateFileCancelButton.addActionListener(e -> {
            gameStateFileName = null;
            gameStateFileTextField.setText("Click the 'Open' button to choose a file");
            SwingUtils.setPanelEnabled(boardSizePanel, true);
        });

        JPanel gameStateFilePanel = new JPanel();
        gameStateFilePanel.setLayout(new BoxLayout(gameStateFilePanel, BoxLayout.X_AXIS));
        gameStateFilePanel.add(Box.createRigidArea(new Dimension(5, 0)));
        gameStateFilePanel.add(gameStateFileTextField);
        gameStateFilePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        gameStateFilePanel.add(gameStateFileOpenButton);
        gameStateFilePanel.add(Box.createRigidArea(new Dimension(5, 0)));
        gameStateFilePanel.add(gameStateFileCancelButton);
        gameStateFilePanel.setBorder(SwingUtils.createTitledBorder("Load Previous Game State"));
        gameStateFilePanel.setPreferredSize(new Dimension(615, 60));

        SwingUtils.addComponentVertically(contentPanel, gameStateFilePanel, constraints);

        boardWidthModel = new SpinnerNumberModel(3, 2, 90, 1);
        boardHeightModel = new SpinnerNumberModel(3, 2, 35, 1);

        boardSizePanel = new JPanel();
        GridLayout boardSizePanelLayout = new GridLayout(2, 2);
        boardSizePanelLayout.setHgap(5);
        boardSizePanelLayout.setVgap(5);
        boardSizePanel.setLayout(boardSizePanelLayout);
        boardSizePanel.add(new JLabel("Board width:"));
        boardSizePanel.add(new JSpinner(boardWidthModel));
        boardSizePanel.add(new JLabel("Board height:"));
        boardSizePanel.add(new JSpinner(boardHeightModel));
        boardSizePanel.setBorder(SwingUtils.createTitledBorder("Board size"));
        boardSizePanel.setPreferredSize(new Dimension(300, 106));

        modePvCRadioButton = new JRadioButton("Player vs AI", true);
        modePvCRadioButton.addActionListener(modeRadioButtonActionListener);
        modePvPRadioButton = new JRadioButton("Player vs Player");
        modePvPRadioButton.addActionListener(modeRadioButtonActionListener);
        modeCvCRadioButton = new JRadioButton("AI vs AI");
        modeCvCRadioButton.addActionListener(modeRadioButtonActionListener);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(modePvCRadioButton);
        modeGroup.add(modePvPRadioButton);
        modeGroup.add(modeCvCRadioButton);

        JPanel modePanel = new JPanel(new GridLayout(3, 1));
        modePanel.add(modePvCRadioButton);
        modePanel.add(modePvPRadioButton);
        modePanel.add(modeCvCRadioButton);

        speedStep = new JRadioButton("Step by step", true);
        JRadioButton speedQuick = new JRadioButton("Quick (no delay)");

        ButtonGroup gameSpeedGroup = new ButtonGroup();
        gameSpeedGroup.add(speedStep);
        gameSpeedGroup.add(speedQuick);

        speedPanel = new JPanel(new GridLayout(2, 1));
        speedPanel.add(speedStep);
        speedPanel.add(speedQuick);
        SwingUtils.setPanelEnabled(speedPanel, modeCvCRadioButton.isSelected());

        JPanel gameModePanel = new JPanel(new GridLayout(1, 2));
        gameModePanel.add(modePanel);
        gameModePanel.add(speedPanel);
        gameModePanel.setBorder(SwingUtils.createTitledBorder("Game Mode"));
        gameModePanel.setPreferredSize(new Dimension(300, 106));

        SwingUtils.addSplitPanel(contentPanel, constraints, boardSizePanel, gameModePanel);

        playerNameTextField = new JTextField[2];
        JPanel[] playerPanel = new JPanel[2];

        aiPlayerDifficultyModel = new SpinnerModel[2];
        aiPlayerTreeDepthModel = new SpinnerModel[2];
        aiPlayerPanel = new JPanel[2];

        for (int i = 0; i < 2; ++i) {
            playerNameTextField[i] = new JTextField();
            playerPanel[i] = new JPanel();
            GridLayout playerPanelLayout = new GridLayout(1, 2);
            playerPanelLayout.setHgap(5);
            playerPanelLayout.setVgap(5);
            playerPanel[i].setLayout(playerPanelLayout);
            playerPanel[i].add(new JLabel("Player name:"));
            playerPanel[i].add(playerNameTextField[i]);
            playerPanel[i].setBorder(SwingUtils.createTitledBorder("Player " + (i + 1) + " Settings"));
            playerPanel[i].setPreferredSize(new Dimension(300, 54));

            aiPlayerDifficultyModel[i] = new SpinnerListModel(aiDifficulties);
            aiPlayerTreeDepthModel[i] = new SpinnerNumberModel(1, 0, 10, 1);
            aiPlayerPanel[i] = new JPanel();
            GridLayout aiPlayerPanelLayout = new GridLayout(2, 2);
            aiPlayerPanelLayout.setHgap(5);
            aiPlayerPanelLayout.setVgap(5);
            aiPlayerPanel[i].setLayout(aiPlayerPanelLayout);
            JSpinner aiPlayerDifficultySpinner = new JSpinner(aiPlayerDifficultyModel[i]);
            aiPlayerDifficultySpinner.addChangeListener(playerDifficultySpinnerChangeListener);
            aiPlayerPanel[i].add(new JLabel("Difficulty:"));
            aiPlayerPanel[i].add(new JSpinner(aiPlayerDifficultyModel[i]));
            aiPlayerPanel[i].add(new JLabel("Game tree depth:"));
            aiPlayerPanel[i].add(new JSpinner(aiPlayerTreeDepthModel[i]));
            aiPlayerPanel[i].setBorder(SwingUtils.createTitledBorder("AI Player " + (i + 1) + " Settings"));
            aiPlayerPanel[i].setPreferredSize(new Dimension(300, 79));

            playerNameTextField[i].setText(getDefaultPlayerName(i));
            SwingUtils.setPanelEnabled(aiPlayerPanel[i], isPlayerAI(i));
        }

        SwingUtils.addSplitPanel(contentPanel, constraints, playerPanel[0], playerPanel[1]);
        SwingUtils.addSplitPanel(contentPanel, constraints, aiPlayerPanel[0], aiPlayerPanel[1]);
    }

    private void initFooter() {
        footerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = SwingUtils.createConstraints(5, true);

        constraints.weighty = 0;
        SwingUtils.addVerticalSpacer(footerPanel, constraints, 10);

        JPanel startButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints startButtonPanelConstraints = SwingUtils.createConstraints(5, false);
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.PLAIN, 40));
        startButton.addActionListener(e -> {
            Player[] players = new Player[2];
            for (int i = 0; i < 2; ++i) {
                if (isPlayerAI(i)) players[i] = new AIPlayer(game, getPlayerName(i), playerColors[i],
                        (String) aiPlayerDifficultyModel[i].getValue(), (int) aiPlayerTreeDepthModel[i].getValue());
                else players[i] = new HumanPlayer(game, getPlayerName(i), playerColors[i]);
            }
            State state = gameStateFileName == null ?
                    new State(game, players[0], players[1], (int) boardWidthModel.getValue(), (int) boardHeightModel.getValue()) :
                    new State(game, players[0], players[1], gameStateFileName);
            Game.Mode mode;
            if (modePvCRadioButton.isSelected()) mode = Game.Mode.PvC;
            else if (modePvPRadioButton.isSelected()) mode = Game.Mode.PvP;
            else if (speedStep.isSelected()) mode = Game.Mode.CvC_STEP;
            else mode = Game.Mode.CvC_QUICK;
            game.startGame(state, mode);
        });
        SwingUtils.addComponentVertically(startButtonPanel, startButton, startButtonPanelConstraints);

        constraints.weighty = 1;
        SwingUtils.addComponentVertically(footerPanel, startButtonPanel, constraints);

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
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initHeader();
        initContent();
        initFooter();

        GridBagConstraints constraints = SwingUtils.createConstraints(5, true);

        constraints.weighty = 1;
        SwingUtils.addComponentVertically(this, headerPanel, constraints);
        constraints.weighty = 0;
        SwingUtils.addComponentVertically(this, contentPanel, constraints);
        constraints.weighty = 1;
        SwingUtils.addComponentVertically(this, footerPanel, constraints);
    }
}