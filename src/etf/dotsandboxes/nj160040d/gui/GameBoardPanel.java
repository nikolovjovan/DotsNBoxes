package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.logic.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GameBoardPanel extends JPanel {

    static int radioButtonDiameter = 12;
    static int edgeThickness = 4;

    enum FillColor {
        TRANSPARENT, BLACK, RED, DARK_RED, BLUE, DARK_BLUE;

        static Color colorTransparent = new Color(0, 0, 0, 0);
        static Color colorBlack = new Color(16, 16, 16);
        static Color colorRed = new Color(240, 8, 0);
        static Color colorDarkRed = new Color(192, 8, 0);
        static Color colorBlue = new Color(0, 80, 240);
        static Color colorDarkBlue = new Color(0, 56, 192);

        public Color toColor() {
            switch (this) {
                case TRANSPARENT: return colorTransparent;
                case BLACK: return colorBlack;
                case RED: return colorRed;
                case BLUE: return colorBlue;
                case DARK_RED: return colorDarkRed;
                case DARK_BLUE: return colorDarkBlue;
                default: return null;
            }
        }
    }

    int width, height, borderThickness, spacing;

    FillColor[][] hEdgeColorMatrix, vEdgeColorMatrix, boxColorMatrix;

    boolean selecting;
    int selectedDotX, selectedDotY;

    boolean lastEdgeHorizontal;
    int lastEdgeX, lastEdgeY;

    JRadioButton[][] buttonMatrix;

    public GameBoardPanel(int width, int height, int borderThickness) {
        this.width = width;
        this.height = height;
        this.borderThickness = borderThickness;

        if (width <= 10 && height <= 10) this.spacing = 40;
        else if (width <= 30 && height <= 20) this.spacing = 25;
        else this.spacing = 10;

        this.hEdgeColorMatrix = new FillColor[height + 1][width];
        this.vEdgeColorMatrix = new FillColor[height][width + 1];
        this.boxColorMatrix = new FillColor[height][width];

        for (int i = 0; i < height + 1; ++i)
            for (int j = 0; j < width + 1; ++j) {
                if (j < width) this.hEdgeColorMatrix[i][j] = FillColor.TRANSPARENT;
                if (i < height) this.vEdgeColorMatrix[i][j] = FillColor.TRANSPARENT;
                if (j < width && i < height) {
                    if (i == j) {
                        // TEMPORARY, REMOVE
                        this.boxColorMatrix[i][i] = i % 2 == 0 ? FillColor.RED : FillColor.BLUE;
                    } else {
                        this.boxColorMatrix[i][j] = FillColor.TRANSPARENT;
                    }
                }
            }

        this.selecting = false;
        this.selectedDotX = -1;
        this.selectedDotY = -1;

        this.lastEdgeHorizontal = false;
        this.lastEdgeX = -1;
        this.lastEdgeY = -1;

        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawBox(Graphics g, Color color, int x, int y) {
        g.setColor(color);
        g.fillRect(
                borderThickness + radioButtonDiameter / 2 + (spacing + radioButtonDiameter) * x,
                borderThickness + radioButtonDiameter / 2 + (spacing + radioButtonDiameter) * y,
                spacing + radioButtonDiameter,
                spacing + radioButtonDiameter
        );
    }

    private void drawHorizontalEdge(Graphics g, Color color, int x, int y) {
        g.setColor(color);
        g.fillRect(
                borderThickness + radioButtonDiameter / 2 + (spacing + radioButtonDiameter) * x,
                borderThickness + (radioButtonDiameter - edgeThickness) / 2 + (spacing + radioButtonDiameter) * y,
                spacing + radioButtonDiameter,
                edgeThickness
        );
    }

    private void drawVerticalEdge(Graphics g, Color color, int x, int y) {
        g.setColor(color);
        g.fillRect(
                borderThickness + (radioButtonDiameter - edgeThickness) / 2 + (spacing + radioButtonDiameter) * x,
                borderThickness + radioButtonDiameter / 2 + (spacing + radioButtonDiameter) * y,
                edgeThickness,
                spacing + radioButtonDiameter
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        getBorder().getBorderInsets(this);

        for (int i = 0; i < height; ++i)
            for (int j = 0; j < width; ++j)
                if (boxColorMatrix[i][j] != FillColor.TRANSPARENT) {
                    drawBox(g, boxColorMatrix[i][j].toColor(), j, i);
                }

        for (int i = 0; i <= height; ++i)
            for (int j = 0; j < width; ++j)
                if (hEdgeColorMatrix[i][j] != FillColor.TRANSPARENT) {
                    drawHorizontalEdge(g, hEdgeColorMatrix[i][j].toColor(), j, i);
                }

        for (int i = 0; i < height; ++i)
            for (int j = 0; j <= width; ++j)
                if (vEdgeColorMatrix[i][j] != FillColor.TRANSPARENT) {
                    drawVerticalEdge(g, vEdgeColorMatrix[i][j].toColor(), j, i);
                }
    }

    private ActionListener dotClickListener = e -> {
        JRadioButton button = (JRadioButton) e.getSource();
        handleDotClick((int) button.getClientProperty("x"), (int) button.getClientProperty("y"));
    };

    private void handleDotClick(int x, int y) {
        if (!selecting) {
            selecting = true;
            selectedDotX = x;
            selectedDotY = y;
        } else {
            if (selectedDotX == x && selectedDotY == y) {
                // cancel current selection
                selecting = false;
                buttonMatrix[selectedDotY][selectedDotX].setSelected(false);
            } else if (Math.abs(selectedDotX - x) + Math.abs(selectedDotY - y) > 1) {
                // invalid second dot, deselect it
                buttonMatrix[y][x].setSelected(false);
            } else {
                // valid selection, deselect first and second dots
                selecting = false;
                buttonMatrix[selectedDotY][selectedDotX].setSelected(false);
                buttonMatrix[y][x].setSelected(false);
                if (lastEdgeX != -1 && lastEdgeY != -1) {
                    if (lastEdgeHorizontal) {
                        hEdgeColorMatrix[lastEdgeY][lastEdgeX] = FillColor.BLACK;
                    } else {
                        vEdgeColorMatrix[lastEdgeY][lastEdgeX] = FillColor.BLACK;
                    }
                }
                if (selectedDotY == y) { // horizontal edge
                    lastEdgeHorizontal = true;
                    lastEdgeX = Math.min(selectedDotX, x);
                    lastEdgeY = y;
                    Game.getHEdgeMatrix()[lastEdgeY][lastEdgeX] = true;
                    hEdgeColorMatrix[lastEdgeY][lastEdgeX] =
                            Game.getTurn() % 2 == 0 ? FillColor.DARK_RED : FillColor.DARK_BLUE;
                    if (lastEdgeY > 0) { // check upper box
                        if (Game.getHEdgeMatrix()[lastEdgeY - 1][lastEdgeX] &&
                                Game.getVEdgeMatrix()[lastEdgeY - 1][lastEdgeX] &&
                                Game.getVEdgeMatrix()[lastEdgeY - 1][lastEdgeX + 1]) {
                            Game.getBoxMatrix()[lastEdgeY - 1][lastEdgeX] = true;
                            boxColorMatrix[lastEdgeY - 1][lastEdgeX] =
                                    Game.getTurn() % 2 == 0 ? FillColor.RED : FillColor.BLUE;
                        }
                    }
                    if (lastEdgeY < height) { // check lower box
                        if (Game.getHEdgeMatrix()[lastEdgeY + 1][lastEdgeX] &&
                                Game.getVEdgeMatrix()[lastEdgeY][lastEdgeX] &&
                                Game.getVEdgeMatrix()[lastEdgeY][lastEdgeX + 1]) {
                            Game.getBoxMatrix()[lastEdgeY][lastEdgeX] = true;
                            boxColorMatrix[lastEdgeY][lastEdgeX] =
                                    Game.getTurn() % 2 == 0 ? FillColor.RED : FillColor.BLUE;
                        }
                    }
                } else { // vertical edge
                    lastEdgeHorizontal = false;
                    lastEdgeX = x;
                    lastEdgeY = Math.min(selectedDotY, y);
                    Game.getVEdgeMatrix()[lastEdgeY][lastEdgeX] = true;
                    vEdgeColorMatrix[lastEdgeY][lastEdgeX] =
                            Game.getTurn() % 2 == 0 ? FillColor.DARK_RED : FillColor.DARK_BLUE;
                    if (lastEdgeX > 0) { // check left side box
                        if (Game.getVEdgeMatrix()[lastEdgeY][lastEdgeX - 1] &&
                                Game.getHEdgeMatrix()[lastEdgeY][lastEdgeX - 1] &&
                                Game.getHEdgeMatrix()[lastEdgeY + 1][lastEdgeX - 1]) {
                            Game.getBoxMatrix()[lastEdgeY][lastEdgeX - 1] = true;
                            boxColorMatrix[lastEdgeY][lastEdgeX - 1] =
                                    Game.getTurn() % 2 == 0 ? FillColor.RED : FillColor.BLUE;
                        }
                    }
                    if (lastEdgeX < width) { // check right side box
                        if (Game.getVEdgeMatrix()[lastEdgeY][lastEdgeX + 1] &&
                                Game.getHEdgeMatrix()[lastEdgeY][lastEdgeX] &&
                                Game.getHEdgeMatrix()[lastEdgeY + 1][lastEdgeX]) {
                            Game.getBoxMatrix()[lastEdgeY][lastEdgeX] = true;
                            boxColorMatrix[lastEdgeY][lastEdgeX] =
                                    Game.getTurn() % 2 == 0 ? FillColor.RED : FillColor.BLUE;
                        }
                    }
                }
                repaint();
                Game.nextTurn();
            }
        }
    }

    private void initUI() {
        // hgap and vgap reduced by 1px because it seems to add 1px somewhere...
        setLayout(new GridLayout(height + 1, width + 1, spacing - 1, spacing - 1));
        buttonMatrix = new JRadioButton[height + 1][width + 1];
        for (int i = 0; i <= height; ++i)
            for (int j = 0; j <= width; ++j) {
                buttonMatrix[i][j] = new JRadioButton();
                buttonMatrix[i][j].putClientProperty("x", j);
                buttonMatrix[i][j].putClientProperty("y", i);
                buttonMatrix[i][j].setOpaque(false);
                buttonMatrix[i][j].setBorder(null);
                buttonMatrix[i][j].addActionListener(dotClickListener);
                add(buttonMatrix[i][j]);
            }
        if (borderThickness > 0) {
            setBorder(BorderFactory.createEmptyBorder(borderThickness, borderThickness, borderThickness, borderThickness));
        }
    }
}