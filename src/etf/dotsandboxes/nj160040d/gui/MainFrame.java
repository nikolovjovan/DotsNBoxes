package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainFrame extends JFrame {

    // TODO: Move to separate Game class...
    public static final String[] aiDifficultyList = { "Competitive", "Advanced", "Beginner" };

    public String getGameStateFileName() {
        return gameStateFileName;
    }

    private String gameStateFileName;

    SpinnerModel boardWidthModel, boardHeightModel;
    JRadioButton modePvCRadioButton, modePvPRadioButton, modeCvCRadioButton;
    JTextField gameStateFileTextField;
    SpinnerModel aiPlayer1DifficultyModel, aiPlayer1GameTreeDepthModel,
            aiPlayer2DifficultyModel, aiPlayer2GameTreeDepthModel;
    JPanel aiPlayer1Panel, aiPlayer2Panel;

    public MainFrame() {
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ActionListener startListener = e -> {
        // start the game
    };

    private void initUI() throws IOException {
        JPanel grid = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 5, 5, 5);

        grid.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel titleLabel = new JLabel(new ImageIcon(SwingUtils.scaleImage("res/title.png", 500)));
        grid.add(titleLabel, constraints);

        ++constraints.gridy;
        grid.add(SwingUtils.createEmptyLabel(new Dimension(50, 10)), constraints);

        boardWidthModel = new SpinnerNumberModel(3, 2, 50, 1);
        boardHeightModel = new SpinnerNumberModel(3, 2, 30, 1);

        JPanel boardSizePanel = new JPanel();
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
        });
        modePvPRadioButton.addActionListener(e -> {
            // hide both AI settings panels
            SwingUtils.setPanelEnabled(aiPlayer1Panel, false);
            SwingUtils.setPanelEnabled(aiPlayer2Panel, false);
        });
        modeCvCRadioButton.addActionListener(e -> {
            // show both AI settings panels
            SwingUtils.setPanelEnabled(aiPlayer1Panel, true);
            SwingUtils.setPanelEnabled(aiPlayer2Panel, true);
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

        SwingUtils.addSplitPanel(grid, constraints, boardSizePanel, modePanel);

        gameStateFileTextField = new JTextField("Click the 'Open' button to choose a file");
        gameStateFileTextField.setEditable(false);

        JButton gameStateFileOpenButton = new JButton("Open");
        gameStateFileOpenButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                gameStateFileName = fileChooser.getSelectedFile().getName();
                gameStateFileTextField.setText(gameStateFileName);
            }
        });

        JButton gameStateFileCancelButton = new JButton("Cancel");
        gameStateFileCancelButton.addActionListener(e -> {
            gameStateFileName = null;
            gameStateFileTextField.setText("Click the 'Open' button to choose a file");
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
        grid.add(gameStateFilePanel, constraints);

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

        SwingUtils.addSplitPanel(grid, constraints, aiPlayer1Panel, aiPlayer2Panel);

        ++constraints.gridy;
        grid.add(SwingUtils.createEmptyLabel(new Dimension(50, 10)), constraints);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(startListener);
        ++constraints.gridy;
        grid.add(startButton, constraints);

        ++constraints.gridy;
        grid.add(SwingUtils.createEmptyLabel(new Dimension(50, 10)), constraints);

        setTitle("Dots & Boxes");
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(grid);
        pack();
    }
}