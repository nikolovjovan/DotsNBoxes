package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.logic.*;
import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.util.SwingUtils;

import javax.swing.*;
import javax.swing.Box;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

public class MainMenuContentPane extends JPanel {

    static String[] aiDifficultyList;
    static String logoFileName = "res/title.png";
    static String titleText = "Dots & Boxes";
    static String titleFontName = "Comic Sans MS";
    static int titleFontSize = 60;

    private Game game;

    private JPanel headerPanel, contentPanel, footerPanel;

    private BufferedImage logo;
    private JTextField gameStateFileTextField;
    private SpinnerModel boardWidthModel, boardHeightModel;
    private JRadioButton modePvCRadioButton, modePvPRadioButton;
    private JTextField[] playerNameTextField;
    private SpinnerModel[] aiPlayerDifficultyModel, aiPlayerTreeDepthModel;
    private JPanel boardSizePanel;
    private JPanel[] aiPlayerPanel;

    private String gameStateFileName;

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
        SwingUtils.addComponentVertically(gameStateFilePanel, Box.createRigidArea(new Dimension(5, 0)));
        SwingUtils.addComponentVertically(gameStateFilePanel, gameStateFileTextField);
        SwingUtils.addComponentVertically(gameStateFilePanel, Box.createRigidArea(new Dimension(10, 0)));
        SwingUtils.addComponentVertically(gameStateFilePanel, gameStateFileOpenButton);
        SwingUtils.addComponentVertically(gameStateFilePanel, Box.createRigidArea(new Dimension(5, 0)));
        SwingUtils.addComponentVertically(gameStateFilePanel, gameStateFileCancelButton);
        gameStateFilePanel.setBorder(SwingUtils.createTitledBorder("Load Previous Game State"));
        gameStateFilePanel.setPreferredSize(new Dimension(495, 60));

        SwingUtils.addComponentVertically(contentPanel, gameStateFilePanel, constraints);

        boardWidthModel = new SpinnerNumberModel(3, 2, 100, 1);
        boardHeightModel = new SpinnerNumberModel(3, 2, 40, 1);

        boardSizePanel = new JPanel();
        GridLayout boardSizePanelLayout = new GridLayout(2, 2);
        boardSizePanelLayout.setHgap(5);
        boardSizePanelLayout.setVgap(5);
        boardSizePanel.setLayout(boardSizePanelLayout);
        SwingUtils.addComponentVertically(boardSizePanel, new JLabel("Board width:"));
        SwingUtils.addComponentVertically(boardSizePanel, new JSpinner(boardWidthModel));
        SwingUtils.addComponentVertically(boardSizePanel, new JLabel("Board height:"));
        SwingUtils.addComponentVertically(boardSizePanel, new JSpinner(boardHeightModel));
        boardSizePanel.setBorder(SwingUtils.createTitledBorder("Board size"));
        boardSizePanel.setPreferredSize(new Dimension(240, 106));

        modePvCRadioButton = new JRadioButton("Player vs AI", true);
        modePvPRadioButton = new JRadioButton("Player vs Player");
        JRadioButton modeCvCRadioButton = new JRadioButton("AI vs AI");

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
        SwingUtils.addComponentVertically(modePanel, modePvCRadioButton);
        SwingUtils.addComponentVertically(modePanel, modePvPRadioButton);
        SwingUtils.addComponentVertically(modePanel, modeCvCRadioButton);
        modePanel.setBorder(SwingUtils.createTitledBorder("Mode"));
        modePanel.setPreferredSize(new Dimension(240, 106));

        SwingUtils.addSplitPanel(contentPanel, constraints, boardSizePanel, modePanel);

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
            SwingUtils.addComponentVertically(playerPanel[i], new JLabel("Player name:"));
            SwingUtils.addComponentVertically(playerPanel[i], playerNameTextField[i]);
            playerPanel[i].setBorder(SwingUtils.createTitledBorder("Player " + (i + 1) + " Settings"));
            playerPanel[i].setPreferredSize(new Dimension(240, 54));

            aiPlayerDifficultyModel[i] = new SpinnerListModel(aiDifficultyList);
            aiPlayerTreeDepthModel[i] = new SpinnerNumberModel(1, 0, 10, 1);
            aiPlayerPanel[i] = new JPanel();
            aiPlayerPanelLayout[i] = new GridLayout(2, 2);
            aiPlayerPanelLayout[i].setHgap(5);
            aiPlayerPanelLayout[i].setVgap(5);
            aiPlayerPanel[i].setLayout(aiPlayerPanelLayout[i]);
            SwingUtils.addComponentVertically(aiPlayerPanel[i], new JLabel("Difficulty:"));
            SwingUtils.addComponentVertically(aiPlayerPanel[i], new JSpinner(aiPlayerDifficultyModel[i]));
            SwingUtils.addComponentVertically(aiPlayerPanel[i], new JLabel("Game tree depth:"));
            SwingUtils.addComponentVertically(aiPlayerPanel[i], new JSpinner(aiPlayerTreeDepthModel[i]));
            aiPlayerPanel[i].setBorder(SwingUtils.createTitledBorder("AI Player " + (i + 1) + " Settings"));
            aiPlayerPanel[i].setPreferredSize(new Dimension(240, 79));
            SwingUtils.setPanelEnabled(aiPlayerPanel[i], aiPlayerPanelEnabled[i]);
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
            State state = gameStateFileName == null ?
                    new State(game, player1, player2, (int) boardWidthModel.getValue(), (int) boardHeightModel.getValue()) :
                    new State(game, player1, player2, gameStateFileName);
            game.startGame(state);
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