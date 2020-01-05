package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.logic.Box;
import etf.dotsandboxes.nj160040d.logic.Edge;
import etf.dotsandboxes.nj160040d.logic.HumanPlayer;
import etf.dotsandboxes.nj160040d.logic.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameBoardPanel extends JPanel {

    static final int dotDiameter                = 10;
    static final int edgeThickness              = 4;
    static final int highlightedDotDiameter     = 12;
    static final int highlightedEdgeThickness   = 2;

    static final boolean renderFlatDot = true;

    static final int maxDotDiameter = Math.max(dotDiameter, highlightedDotDiameter);

    Game game;

    int width, height, spacing, edgeLength;
    boolean boardEnabled, cropped;
    Dimension requiredSize;
    Point topLeft, bottomRight;
    Edge highlightedEdge;

    public GameBoardPanel(Game game) {
        this.game = game;

        this.width = game.getBoard().getWidth();
        this.height = game.getBoard().getHeight();

        if (this.width <= 8 && this.height <= 4) this.spacing = 96;
        if (this.width <= 16 && this.height <= 8) this.spacing = 72;
        else if (this.width <= 24 && this.height <= 12) this.spacing = 48;
        else if (this.width <= 32 && this.height <= 16) this.spacing = 32;
        else if (this.width <= 40 && this.height <= 20) this.spacing = 20;
        else if (this.width <= 48 && this.height <= 24) this.spacing = 16;
        else this.spacing = 12;

        this.boardEnabled = game.getCurrentPlayer().getType() == Player.Type.HUMAN;
        this.cropped = false;

        this.highlightedEdge = new Edge();
        this.highlightedEdge.setColorValue(game.getCurrentPlayer().getColorValue());

        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        boardEnabled = game.getCurrentPlayer().getType() == Player.Type.HUMAN;
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
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!boardEnabled) return;
                if (!highlightedEdge.isValid()) return;
                ((HumanPlayer) game.getCurrentPlayer()).setNextMove(highlightedEdge);
                highlightedEdge.invalidate();
                highlightedEdge.setColorValue(game.getCurrentPlayer().getColorValue());
                repaint();
                game.playerDone();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!boardEnabled) return;
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
                if (highlightedEdge.isValid() && game.getBoard().isEdgeSet(highlightedEdge)) highlightedEdge.invalidate();
                repaint();
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                cropped = false;
                int availableSpacingX = ((getWidth() - maxDotDiameter) / width) - dotDiameter,
                    availableSpacingY = ((getHeight() - maxDotDiameter) / height) - dotDiameter;
                int availableSpacing = Math.min(availableSpacingX, availableSpacingY);
                if (availableSpacing != spacing) {
                    if (availableSpacing > dotDiameter) spacing = availableSpacing;
                    else {
                        spacing = dotDiameter;
                        cropped = true;
                    }
                    updateRequiredSize();
                }
                updateBoardLocation();
                repaint();
            }
        });
    }

    private void renderBox(Graphics g, Box box) {
        Graphics2D gg = (Graphics2D) g.create();
        gg.setColor(ColorValue.valueToColor(box.getColorValue()));
        gg.fillRect(
                topLeft.x + edgeLength * box.getX(),
                topLeft.y + edgeLength * box.getY(),
                edgeLength,
                edgeLength
        );
        gg.dispose();
    }

    private void renderEdge(Graphics g, Edge edge, int thickness, boolean dotted) {
        Graphics2D gg = (Graphics2D) g.create();
        gg.setColor(ColorValue.valueToColor(edge.getColorValue()));
        gg.setStroke(new BasicStroke(
                thickness,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL,
                0,
                dotted ? new float[]{5, 3} : null,
                0
        ));
        gg.drawLine(
                topLeft.x + edgeLength * edge.getX(),
                topLeft.y + edgeLength * edge.getY(),
                topLeft.x + edgeLength * (edge.getX() + (edge.isHorizontal() ? 1 : 0)),
                topLeft.y + edgeLength * (edge.getY() + (edge.isHorizontal() ? 0 : 1))
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
                if (game.getBoard().isBoxSet(j, i)) {
                    renderBox(g, game.getBoard().getBox(j, i));
                }

        for (int i = 0; i <= height; ++i)
            for (int j = 0; j < width; ++j)
                renderEdge(g, game.getBoard().getEdge(true, j, i ), edgeThickness, false);

        for (int i = 0; i < height; ++i)
            for (int j = 0; j <= width; ++j)
                renderEdge(g, game.getBoard().getEdge(false, j, i), edgeThickness, false);

        if (highlightedEdge.isValid()) {
            renderEdge(g, highlightedEdge, highlightedEdgeThickness, true);
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