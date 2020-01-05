package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.logic.*;
import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.util.SwingUtils;

import javax.swing.*;
import javax.swing.Box;
import java.awt.*;
import java.io.IOException;

public class MainMenuContentPane extends JPanel {

    static String[] aiDifficultyList;

    Game game;

    JTextField gameStateFileTextField;
    SpinnerModel boardWidthModel, boardHeightModel;
    JRadioButton modePvCRadioButton, modePvPRadioButton, modeCvCRadioButton;
    JTextField[] playerNameTextField;
    SpinnerModel[] aiPlayerDifficultyModel, aiPlayerTreeDepthModel;
    JPanel boardSizePanel;
    JPanel[] aiPlayerPanel;

    String gameStateFileName;

    static {
        AIPlayer.Difficulty[] difficulties = AIPlayer.Difficulty.values();
        aiDifficultyList = new String[difficulties.length];
        for (int i = 0; i < difficulties.length; ++i) aiDifficultyList[i] = difficulties[i].toString();
    }

    public MainMenuContentPane(Game game) {
        this.game = game;
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() throws IOException {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 5, 5, 5);

        setBorder(BorderFactory.createEmptyBorder(20, 10, 15, 10));

        JLabel titleLabel = new JLabel(new ImageIcon(SwingUtils.scaleImage("res/title.png", 500)));
        add(titleLabel, constraints);

        ++constraints.gridy;
        add(SwingUtils.createEmptyLabel(new Dimension(50, 10)), constraints);

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
        gameStateFilePanel.setPreferredSize(new Dimension(495, 60));

        ++constraints.gridy;
        add(gameStateFilePanel, constraints);

        boardWidthModel = new SpinnerNumberModel(3, 2, 50, 1);
        boardHeightModel = new SpinnerNumberModel(3, 2, 30, 1);

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
        boardSizePanel.setPreferredSize(new Dimension(240, 106));

        modePvCRadioButton = new JRadioButton("Player vs AI", true);
        modePvPRadioButton = new JRadioButton("Player vs Player");
        modeCvCRadioButton = new JRadioButton("AI vs AI");

        modePvCRadioButton.addActionListener(e -> {
            // show one AI settings panel
            SwingUtils.setPanelEnabled(aiPlayerPanel[0], false);
            SwingUtils.setPanelEnabled(aiPlayerPanel[1], true);
            // change default player names
            if (playerNameTextField[0].getText().equals("AI Player 1")) playerNameTextField[0].setText("Human Player 1");
            if (playerNameTextField[1].getText().equals("Human Player 2")) playerNameTextField[1].setText("AI Player 2");
        });
        modePvPRadioButton.addActionListener(e -> {
            // hide both AI settings panels
            SwingUtils.setPanelEnabled(aiPlayerPanel[0], false);
            SwingUtils.setPanelEnabled(aiPlayerPanel[1], false);
            // change default player names
            if (playerNameTextField[0].getText().equals("AI Player 1")) playerNameTextField[0].setText("Human Player 1");
            if (playerNameTextField[1].getText().equals("AI Player 2")) playerNameTextField[1].setText("Human Player 2");
        });
        modeCvCRadioButton.addActionListener(e -> {
            // show both AI settings panels
            SwingUtils.setPanelEnabled(aiPlayerPanel[0], true);
            SwingUtils.setPanelEnabled(aiPlayerPanel[1], true);
            // change default player names
            if (playerNameTextField[0].getText().equals("Human Player 1")) playerNameTextField[0].setText("AI Player 1");
            if (playerNameTextField[1].getText().equals("Human Player 2")) playerNameTextField[1].setText("AI Player 2");
        });

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(modePvCRadioButton);
        modeGroup.add(modePvPRadioButton);
        modeGroup.add(modeCvCRadioButton);

        JPanel modePanel = new JPanel(new GridLayout(3, 1));
        modePanel.add(modePvCRadioButton);
        modePanel.add(modePvPRadioButton);
        modePanel.add(modeCvCRadioButton);
        modePanel.setBorder(SwingUtils.createTitledBorder("Mode"));
        modePanel.setPreferredSize(new Dimension(240, 106));

        SwingUtils.addSplitPanel(this, constraints, boardSizePanel, modePanel);

        playerNameTextField = new JTextField[2];
        String[] defaultPlayerName = new String[] { "Human Player 1", "AI Player 2" };
        JPanel[] playerPanel = new JPanel[2];
        GridLayout[] playerPanelLayout = new GridLayout[2];

        aiPlayerDifficultyModel = new SpinnerModel[2];
        aiPlayerTreeDepthModel = new SpinnerModel[2];
        aiPlayerPanel = new JPanel[2];
        boolean[] aiPlayerPanelEnabled = new boolean[] { false, true };
        GridLayout[] aiPlayerPanelLayout = new GridLayout[2];

        for (int i = 0; i < 2; ++i) {
            playerNameTextField[i] = new JTextField(defaultPlayerName[i]);
            playerPanel[i] = new JPanel();
            playerPanelLayout[i] = new GridLayout(1, 2);
            playerPanelLayout[i].setHgap(5);
            playerPanelLayout[i].setVgap(5);
            playerPanel[i].setLayout(playerPanelLayout[i]);
            playerPanel[i].add(new JLabel("Player name:"));
            playerPanel[i].add(playerNameTextField[i]);
            playerPanel[i].setBorder(SwingUtils.createTitledBorder("Player " + (i + 1) + " Settings"));
            playerPanel[i].setPreferredSize(new Dimension(240, 54));

            aiPlayerDifficultyModel[i] = new SpinnerListModel(aiDifficultyList);
            aiPlayerTreeDepthModel[i] = new SpinnerNumberModel(1, 0, 10, 1);
            aiPlayerPanel[i] = new JPanel();
            aiPlayerPanelLayout[i] = new GridLayout(2, 2);
            aiPlayerPanelLayout[i].setHgap(5);
            aiPlayerPanelLayout[i].setVgap(5);
            aiPlayerPanel[i].setLayout(aiPlayerPanelLayout[i]);
            aiPlayerPanel[i].add(new JLabel("Difficulty:"));
            aiPlayerPanel[i].add(new JSpinner(aiPlayerDifficultyModel[i]));
            aiPlayerPanel[i].add(new JLabel("Game tree depth:"));
            aiPlayerPanel[i].add(new JSpinner(aiPlayerTreeDepthModel[i]));
            aiPlayerPanel[i].setBorder(SwingUtils.createTitledBorder("AI Player " + (i + 1) + " Settings"));
            aiPlayerPanel[i].setPreferredSize(new Dimension(240, 79));
            SwingUtils.setPanelEnabled(aiPlayerPanel[i], aiPlayerPanelEnabled[i]);
        }

        SwingUtils.addSplitPanel(this, constraints, playerPanel[0], playerPanel[1]);
        SwingUtils.addSplitPanel(this, constraints, aiPlayerPanel[0], aiPlayerPanel[1]);

        ++constraints.gridy;
        add(SwingUtils.createEmptyLabel(new Dimension(50, 0)), constraints);

        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.PLAIN, 40));
        startButton.addActionListener(e -> {
            Board board = gameStateFileName == null ?
                    new Board(game, (int) boardWidthModel.getValue(), (int) boardHeightModel.getValue()) :
                    new Board(game, gameStateFileName);
            Player player1, player2;

            if (modePvCRadioButton.isSelected() || modePvPRadioButton.isSelected()) {
                player1 = new HumanPlayer(game, playerNameTextField[0].getText().isEmpty() ?
                        "Human Player 1" : playerNameTextField[0].getText(), ColorValue.BLUE);
            } else {
                player1 = new AIPlayer(game, playerNameTextField[0].getText().isEmpty() ?
                        "AI Player 1" : playerNameTextField[0].getText(), ColorValue.BLUE,
                        (String) aiPlayerDifficultyModel[0].getValue(),
                        (int) aiPlayerTreeDepthModel[0].getValue());
            }
            if (modePvPRadioButton.isSelected()) {
                player2 = new HumanPlayer(game, playerNameTextField[1].getText().isEmpty() ?
                        "Human Player 2" : playerNameTextField[1].getText(), ColorValue.RED);
            } else {
                player2 = new AIPlayer(game, playerNameTextField[1].getText().isEmpty() ?
                        "AI Player 2" : playerNameTextField[1].getText(), ColorValue.RED,
                        (String) aiPlayerDifficultyModel[1].getValue(),
                        (int) aiPlayerTreeDepthModel[1].getValue());
            }
            game.startGame(board, player1, player2);
        });
        ++constraints.gridy;
        add(startButton, constraints);
    }
}