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

    SpinnerModel boardWidthModel, boardHeightModel;
    JRadioButton modePvCRadioButton, modePvPRadioButton, modeCvCRadioButton;
    JTextField player1NameTextField, player2NameTextField, gameStateFileTextField;
    SpinnerModel aiPlayer1DifficultyModel, aiPlayer1GameTreeDepthModel,
            aiPlayer2DifficultyModel, aiPlayer2GameTreeDepthModel;
    JPanel boardSizePanel, aiPlayer1Panel, aiPlayer2Panel;

    String gameStateFileName;

    static {
        AIPlayer.Difficulty[] difficulties = AIPlayer.Difficulty.values();
        aiDifficultyList = new String[difficulties.length];
        for (int i = 0; i < difficulties.length; ++i) aiDifficultyList[i] = difficulties[i].toString();
    }

    public MainMenuContentPane() {
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

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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
        gameStateFilePanel.setBorder(SwingUtils.createTitledBorder("Load game state"));

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

        modePvCRadioButton = new JRadioButton("Player vs AI", true);
        modePvPRadioButton = new JRadioButton("Player vs Player");
        modeCvCRadioButton = new JRadioButton("AI vs AI");

        modePvCRadioButton.addActionListener(e -> {
            // show one AI settings panel
            SwingUtils.setPanelEnabled(aiPlayer1Panel, false);
            SwingUtils.setPanelEnabled(aiPlayer2Panel, true);
            // change default player names
            if (player1NameTextField.getText().equals("AI Player 1")) player1NameTextField.setText("Human Player 1");
            if (player2NameTextField.getText().equals("Human Player 2")) player2NameTextField.setText("AI Player 2");
        });
        modePvPRadioButton.addActionListener(e -> {
            // hide both AI settings panels
            SwingUtils.setPanelEnabled(aiPlayer1Panel, false);
            SwingUtils.setPanelEnabled(aiPlayer2Panel, false);
            // change default player names
            if (player1NameTextField.getText().equals("AI Player 1")) player1NameTextField.setText("Human Player 1");
            if (player2NameTextField.getText().equals("AI Player 2")) player2NameTextField.setText("Human Player 2");
        });
        modeCvCRadioButton.addActionListener(e -> {
            // show both AI settings panels
            SwingUtils.setPanelEnabled(aiPlayer1Panel, true);
            SwingUtils.setPanelEnabled(aiPlayer2Panel, true);
            // change default player names
            if (player1NameTextField.getText().equals("Human Player 1")) player1NameTextField.setText("AI Player 1");
            if (player2NameTextField.getText().equals("Human Player 2")) player2NameTextField.setText("AI Player 2");
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

        SwingUtils.addSplitPanel(this, constraints, boardSizePanel, modePanel);

        player1NameTextField = new JTextField("Human Player 1");
        JPanel player1Panel = new JPanel();
        GridLayout player1PanelLayout = new GridLayout(1, 2);
        player1PanelLayout.setHgap(5);
        player1PanelLayout.setVgap(5);
        player1Panel.setLayout(player1PanelLayout);
        player1Panel.add(new JLabel("Player name:"));
        player1Panel.setBorder(SwingUtils.createTitledBorder("Player 1 Settings"));
        player1Panel.add(player1NameTextField);

        player2NameTextField = new JTextField("AI Player 2");
        JPanel player2Panel = new JPanel();
        GridLayout player2PanelLayout = new GridLayout(1, 2);
        player2PanelLayout.setHgap(5);
        player2PanelLayout.setVgap(5);
        player2Panel.setLayout(player2PanelLayout);
        player2Panel.add(new JLabel("Player name:"));
        player2Panel.setBorder(SwingUtils.createTitledBorder("Player 2 Settings"));
        player2Panel.add(player2NameTextField);

        SwingUtils.addSplitPanel(this, constraints, player1Panel, player2Panel);

        aiPlayer1DifficultyModel = new SpinnerListModel(aiDifficultyList);
        aiPlayer1DifficultyModel.setValue("Beginner");
        aiPlayer1GameTreeDepthModel = new SpinnerNumberModel(1, 0, 10, 1);
        aiPlayer1Panel = new JPanel();
        GridLayout aiPlayer1PanelLayout = new GridLayout(2, 2);
        aiPlayer1PanelLayout.setHgap(5);
        aiPlayer1PanelLayout.setVgap(5);
        aiPlayer1Panel.setLayout(aiPlayer1PanelLayout);
        aiPlayer1Panel.add(new JLabel("Difficulty:"));
        aiPlayer1Panel.add(new JSpinner(aiPlayer1DifficultyModel));
        aiPlayer1Panel.add(new JLabel("Game tree depth:"));
        aiPlayer1Panel.add(new JSpinner(aiPlayer1GameTreeDepthModel));
        aiPlayer1Panel.setBorder(SwingUtils.createTitledBorder("AI Player 1 Settings"));
        SwingUtils.setPanelEnabled(aiPlayer1Panel, false);

        aiPlayer2DifficultyModel = new SpinnerListModel(aiDifficultyList);
        aiPlayer2DifficultyModel.setValue("Beginner");
        aiPlayer2GameTreeDepthModel = new SpinnerNumberModel(1, 0, 10, 1);
        aiPlayer2Panel = new JPanel();
        GridLayout aiPlayer2PanelLayout = new GridLayout(2, 2);
        aiPlayer2PanelLayout.setHgap(5);
        aiPlayer2PanelLayout.setVgap(5);
        aiPlayer2Panel.setLayout(aiPlayer2PanelLayout);
        aiPlayer2Panel.add(new JLabel("Difficulty:"));
        aiPlayer2Panel.add(new JSpinner(aiPlayer2DifficultyModel));
        aiPlayer2Panel.add(new JLabel("Game tree depth:"));
        aiPlayer2Panel.add(new JSpinner(aiPlayer2GameTreeDepthModel));
        aiPlayer2Panel.setBorder(SwingUtils.createTitledBorder("AI Player 2 Settings"));
        SwingUtils.setPanelEnabled(aiPlayer2Panel, true);

        SwingUtils.addSplitPanel(this, constraints, aiPlayer1Panel, aiPlayer2Panel);

        ++constraints.gridy;
        add(SwingUtils.createEmptyLabel(new Dimension(50, 10)), constraints);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            Board board = new Board((int) boardWidthModel.getValue(), (int) boardHeightModel.getValue());
            Player player1, player2;

            if (modePvCRadioButton.isSelected() || modePvPRadioButton.isSelected()) {
                player1 = new HumanPlayer(player1NameTextField.getText().isEmpty() ?
                        "Human Player 1" : player1NameTextField.getText(), ColorValue.BLUE);
            } else {
                player1 = new AIPlayer(player1NameTextField.getText().isEmpty() ?
                        "AI Player 1" : player1NameTextField.getText(), ColorValue.BLUE,
                        (String) aiPlayer1DifficultyModel.getValue(),
                        (int) aiPlayer1GameTreeDepthModel.getValue());
            }
            if (modePvPRadioButton.isSelected()) {
                player2 = new HumanPlayer(player2NameTextField.getText().isEmpty() ?
                        "Human Player 2" : player2NameTextField.getText(), ColorValue.RED);
            } else {
                player2 = new AIPlayer(player2NameTextField.getText().isEmpty() ?
                        "AI Player 2" : player2NameTextField.getText(), ColorValue.RED,
                        (String) aiPlayer2DifficultyModel.getValue(),
                        (int) aiPlayer2GameTreeDepthModel.getValue());
            }
            Game.startGame(board, player1, player2);
        });
        ++constraints.gridy;
        add(startButton, constraints);

        ++constraints.gridy;
        add(SwingUtils.createEmptyLabel(new Dimension(50, 10)), constraints);
    }
}