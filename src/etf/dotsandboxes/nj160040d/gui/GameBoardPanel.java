package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.Game;
import etf.dotsandboxes.nj160040d.logic.Board;
import etf.dotsandboxes.nj160040d.logic.Box;
import etf.dotsandboxes.nj160040d.logic.ColorValue;
import etf.dotsandboxes.nj160040d.logic.Edge;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameBoardPanel extends JPanel {

    static final int dotDiameter                = 10;
    static final int edgeThickness              = 4;
    static final int highlightedDotDiameter     = 12;
    static final int highlightedEdgeThickness   = 2;

    static final boolean renderFlatDot = true;

    static final int maxDotRadius = (int) Math.ceil((double) Math.max(dotDiameter, highlightedDotDiameter) / 2);

    static final Color colorTransparent = new Color(0, 0, 0, 0);
    static final Color colorBlue        = new Color(0, 80, 240);
    static final Color colorRed         = new Color(240, 8, 0);
    static final Color colorLightBlue   = new Color(48, 140, 255);
    static final Color colorLightRed    = new Color(255, 64, 48);
    static final Color colorBlack       = new Color(16, 16, 16);

    static Color valueToColor(byte value) {
        switch (value) {
            case ColorValue.TRANSPARENT:    return colorTransparent;
            case ColorValue.BLUE:           return colorBlue;
            case ColorValue.RED:            return colorRed;
            case ColorValue.LIGHT_BLUE:     return colorLightBlue;
            case ColorValue.LIGHT_RED:      return colorLightRed;
            case ColorValue.BLACK:          return colorBlack;
            default:                        return null;
        }
    }

    Board board;
    int borderThickness, spacing, edgeLength;
    Point topLeft, bottomRight;
    Edge lastEdge, highlightedEdge;

    public GameBoardPanel(Board board, int borderThickness) {
        this.board = board;
        this.borderThickness = borderThickness;

        if (board.getWidth() <= 8 && board.getHeight() <= 4) this.spacing = 96;
        if (board.getWidth() <= 16 && board.getHeight() <= 8) this.spacing = 72;
        else if (board.getWidth() <= 24 && board.getHeight() <= 12) this.spacing = 48;
        else if (board.getWidth() <= 32 && board.getHeight() <= 16) this.spacing = 32;
        else if (board.getHeight() <= 40 && board.getHeight() <= 20) this.spacing = 20;
        else if (board.getHeight() <= 48 && board.getHeight() <= 24) this.spacing = 16;
        else this.spacing = 12;

        this.edgeLength = spacing + dotDiameter;

        this.topLeft = new Point(
                this.borderThickness + maxDotRadius,
                this.borderThickness + maxDotRadius
        );

        this.bottomRight = new Point(
                this.topLeft.x + this.edgeLength * this.board.getWidth(),
                this.topLeft.y + this.edgeLength * this.board.getHeight()
        );

        this.lastEdge = new Edge();
        this.highlightedEdge = new Edge();
        this.highlightedEdge.setColorValue(Game.getCurrentColorValue());

        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Edge getLastEdge() { return lastEdge; }

    private void initUI() {
        setPreferredSize(new Dimension(
                bottomRight.x + maxDotRadius + borderThickness,
                bottomRight.y + maxDotRadius + borderThickness
        ));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!highlightedEdge.isValid()) return;
                if (lastEdge.isValid()) board.setEdgeColorValue(lastEdge, ColorValue.BLACK);
                board.playerDrawEdge(highlightedEdge);
                if (board.getNumberOfAvailableMoves() > 0) lastEdge.copy(highlightedEdge);
                else lastEdge.invalidate();
                highlightedEdge.invalidate();
                Game.nextTurn();
                highlightedEdge.setColorValue(Game.getCurrentColorValue());
                repaint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
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
                if (highlightedEdge.isValid() && board.isEdgeSet(highlightedEdge)) highlightedEdge.invalidate();
                repaint();
            }
        });
    }

    private void renderBox(Graphics g, Box box) {
        Graphics2D gg = (Graphics2D) g.create();
        gg.setColor(valueToColor(box.getColorValue()));
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
        gg.setColor(valueToColor(edge.getColorValue()));
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
            gg.setColor(colorBlack);
            gg.fillOval(dotX, dotY, diameter, diameter);
        } else {
            gg.setColor(Color.DARK_GRAY);
            gg.fillArc(dotX, dotY, diameter, diameter, 45, 180);
            gg.setColor(Color.BLACK);
            gg.fillArc(dotX, dotY, diameter, diameter, 225, 180);
            gg.setColor(colorBlack);
            gg.rotate(Math.toRadians(-45), dotX + (double) diameter / 2, dotY + (double) diameter / 2);
            gg.fillOval(dotX, dotY + 1, diameter, diameter - 2);
        }
        gg.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < board.getHeight(); ++i)
            for (int j = 0; j < board.getWidth(); ++j)
                if (board.isBoxSet(j, i)) {
                    renderBox(g, board.getBox(j, i));
                }

        for (int i = 0; i <= board.getHeight(); ++i)
            for (int j = 0; j < board.getWidth(); ++j)
                renderEdge(g, board.getEdge(true, j, i ), edgeThickness, false);

        for (int i = 0; i < board.getHeight(); ++i)
            for (int j = 0; j <= board.getWidth(); ++j)
                renderEdge(g, board.getEdge(false, j, i), edgeThickness, false);

        if (highlightedEdge.isValid()) {
            renderEdge(g, highlightedEdge, highlightedEdgeThickness, true);
        }

        for (int i = 0; i <= board.getHeight(); ++i)
            for (int j = 0; j <= board.getWidth(); ++j)
                if (j == highlightedEdge.getX() && i == highlightedEdge.getY() ||
                        highlightedEdge.isHorizontal() && j == highlightedEdge.getX() + 1 && i == highlightedEdge.getY() ||
                        !highlightedEdge.isHorizontal() && j == highlightedEdge.getX() && i == highlightedEdge.getY() + 1) {
                    renderDot(g, j, i, highlightedDotDiameter);
                } else {
                    renderDot(g, j, i, dotDiameter);
                }
    }
}