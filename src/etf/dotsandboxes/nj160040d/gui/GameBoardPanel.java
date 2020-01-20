package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.logic.Edge;
import etf.dotsandboxes.nj160040d.logic.HumanPlayer;
import etf.dotsandboxes.nj160040d.logic.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameBoardPanel extends JPanel {

    private static final int dotDiameter                = 10;
    private static final int edgeThickness              = 4;
    private static final int highlightedDotDiameter     = 12;
    private static final int highlightedEdgeThickness   = 2;

    private static final boolean renderFlatDot = true;

    private static final int maxDotDiameter = Math.max(dotDiameter, highlightedDotDiameter);

    private Game game;
    private GameContentPane gameContentPane;

    private int width, height, spacing, edgeLength;
    private boolean boardEnabled;
    private Dimension requiredSize;
    private Point topLeft, bottomRight;
    private Edge highlightedEdge;

    public GameBoardPanel(Game game, GameContentPane gameContentPane) {
        this.game = game;
        this.gameContentPane = gameContentPane;

        this.width = game.getState().getWidth();
        this.height = game.getState().getHeight();

        if (this.width <= 8 && this.height <= 4) this.spacing = 96;
        if (this.width <= 16 && this.height <= 8) this.spacing = 72;
        else if (this.width <= 24 && this.height <= 12) this.spacing = 48;
        else if (this.width <= 32 && this.height <= 16) this.spacing = 32;
        else if (this.width <= 40 && this.height <= 20) this.spacing = 20;
        else if (this.width <= 48 && this.height <= 24) this.spacing = 16;
        else this.spacing = 12;

        this.boardEnabled = game.getState().getCurrentPlayer().getType() == Player.Type.HUMAN;

        this.highlightedEdge = new Edge();
        this.highlightedEdge.setValue(game.getState().getCurrentPlayer().getColorValue());

        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        boardEnabled = game.getState().getCurrentPlayer().getType() == Player.Type.HUMAN;
        highlightedEdge.setValue(game.getState().getCurrentPlayer().getColorValue());
        repaint();
    }

    private void updateRequiredSize() {
        edgeLength = spacing + dotDiameter;
        requiredSize = new Dimension(maxDotDiameter + edgeLength * width, maxDotDiameter + edgeLength * height);
    }

    private void updateBoardLocation() {
        topLeft = new Point(
                (getWidth() - requiredSize.width + maxDotDiameter) / 2,
                (getHeight() - requiredSize.height + maxDotDiameter) / 2);
        bottomRight = new Point(
                (getWidth() + requiredSize.width - maxDotDiameter) / 2,
                (getHeight() + requiredSize.height - maxDotDiameter) / 2
        );
    }

    private void initUI() {
        updateRequiredSize();
        setPreferredSize(requiredSize);
        updateBoardLocation();
        if (game.getMode() != Game.Mode.CvC_STEP) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!boardEnabled) return;
                    if (!highlightedEdge.isValid()) return;
                    ((HumanPlayer) game.getState().getCurrentPlayer()).setNextMove(highlightedEdge);
                    highlightedEdge.invalidate();
                    repaint();
                    game.playerDone();
                }
            });
        }
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (game.getMode() != Game.Mode.CvC_STEP && !boardEnabled) return;
                if (e.getX() < topLeft.x || e.getX() > bottomRight.x ||
                    e.getY() < topLeft.y || e.getY() > bottomRight.y) {
                    highlightedEdge.invalidate();
                } else {
                    int x = (e.getX() - topLeft.x) % edgeLength,
                        y = (e.getY() - topLeft.y) % edgeLength,
                        boardX = (e.getX() - topLeft.x) / edgeLength,
                        boardY = (e.getY() - topLeft.y) / edgeLength;
                    if (x >= y && x < edgeLength - y) highlightedEdge.setAsTopEdge(boardX, boardY);
                    else if (x >= (edgeLength - y) && x < y) highlightedEdge.setAsBottomEdge(boardX, boardY);
                    else if (y >= x && y < (edgeLength - x)) highlightedEdge.setAsLeftEdge(boardX, boardY);
                    else if (y > (edgeLength - x) && y < x) highlightedEdge.setAsRightEdge(boardX, boardY);
                    else highlightedEdge.invalidate();
                }
                if (highlightedEdge.isValid() && game.getState().isEdgeSet(highlightedEdge)) highlightedEdge.invalidate();
                if (highlightedEdge.isValid() && game.getMode() == Game.Mode.CvC_STEP) gameContentPane.showHeuristic(highlightedEdge);
                repaint();
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int availableSpacingX = ((getWidth() - maxDotDiameter) / width) - dotDiameter,
                    availableSpacingY = ((getHeight() - maxDotDiameter) / height) - dotDiameter;
                int availableSpacing = Math.min(availableSpacingX, availableSpacingY);
                if (availableSpacing != spacing) {
                    spacing = Math.max(availableSpacing, dotDiameter);
                    updateRequiredSize();
                }
                updateBoardLocation();
                repaint();
            }
        });
    }

    private void renderBox(Graphics g, byte colorValue, int x, int y) {
        Graphics2D gg = (Graphics2D) g.create();
        gg.setColor(ColorValue.valueToColor(colorValue));
        gg.fillRect(topLeft.x + edgeLength * x, topLeft.y + edgeLength * y, edgeLength, edgeLength);
        gg.dispose();
    }

    private void renderEdge(Graphics g, byte colorValue, boolean horizontal, int x, int y, int thickness, boolean dotted) {
        Graphics2D gg = (Graphics2D) g.create();
        gg.setColor(ColorValue.valueToColor(colorValue));
        gg.setStroke(new BasicStroke(
                thickness,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                0,
                dotted ? new float[]{5, 3} : null,
                0
        ));
        gg.drawLine(
                topLeft.x + edgeLength * x,
                topLeft.y + edgeLength * y,
                topLeft.x + edgeLength * (x + (horizontal ? 1 : 0)),
                topLeft.y + edgeLength * (y + (horizontal ? 0 : 1))
        );
        gg.dispose();
    }

    private void renderDot(Graphics g, int x, int y, int diameter) {
        int dotX = topLeft.x + edgeLength * x - diameter / 2, dotY = topLeft.y + edgeLength * y - diameter / 2;
        Graphics2D gg = (Graphics2D) g.create();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (renderFlatDot) {
            gg.setColor(ColorValue.colorBlack);
            gg.fillOval(dotX, dotY, diameter, diameter);
        } else {
            gg.setColor(Color.DARK_GRAY);
            gg.fillArc(dotX, dotY, diameter, diameter, 45, 180);
            gg.setColor(Color.BLACK);
            gg.fillArc(dotX, dotY, diameter, diameter, 225, 180);
            gg.setColor(ColorValue.colorBlack);
            gg.rotate(Math.toRadians(-45), dotX + (double) diameter / 2, dotY + (double) diameter / 2);
            gg.fillOval(dotX, dotY + 1, diameter, diameter - 2);
        }
        gg.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < height; ++i)
            for (int j = 0; j < width; ++j)
                if (game.getState().isBoxSet(j, i)) {
                    renderBox(g, game.getState().getBoxValue(j, i), j, i);
                }

        for (int i = 0; i <= height; ++i)
            for (int j = 0; j < width; ++j)
                renderEdge(g, game.getState().getEdgeValue(true, j, i), true, j, i, edgeThickness, false);

        for (int i = 0; i < height; ++i)
            for (int j = 0; j <= width; ++j)
                renderEdge(g, game.getState().getEdgeValue(false, j, i), false, j, i, edgeThickness, false);

        if (highlightedEdge.isValid()) {
            renderEdge(g, highlightedEdge.getValue(), highlightedEdge.isHorizontal(),
                    highlightedEdge.getX(), highlightedEdge.getY(), highlightedEdgeThickness, true);
        }

        for (int i = 0; i <= height; ++i)
            for (int j = 0; j <= width; ++j)
                if (j == highlightedEdge.getX() && i == highlightedEdge.getY() ||
                        highlightedEdge.isHorizontal() && j == highlightedEdge.getX() + 1 && i == highlightedEdge.getY() ||
                        !highlightedEdge.isHorizontal() && j == highlightedEdge.getX() && i == highlightedEdge.getY() + 1) {
                    renderDot(g, j, i, highlightedDotDiameter);
                } else {
                    renderDot(g, j, i, dotDiameter);
                }
    }
}