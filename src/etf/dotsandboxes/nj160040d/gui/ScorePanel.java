package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class ScorePanel extends JPanel {

    static Font scorePanelFont = new Font("Arial", Font.BOLD, 24);
    static int textHeightCompensation = 6; // stupid text rendering and text height...

    static int scoreSliderHeight = 20;
    static int scoreSliderArcDiameter = scoreSliderHeight;
    static int scoreSliderStrokeThickness = 1;

    static int textBoxHeight = ceilToTens(getTextSize("WQXO").height + 20);
    static int textBoxArcDiameter = textBoxHeight / 4;
    static int textBoxStrokeThickness = 1;

    static int rectSpacing = 10;
    static int lineThickness = 4;
    static int lineArcDiameter = lineThickness;

    static int scoreTextBoxWidth = ceilToTens(getTextSize("9999").width + 20);

    Game game;

    String player1Name, player2Name;
    Color player1Color, player2Color;
    int nameTextBoxWidth;

    public ScorePanel(Game game) {
        this.game = game;
        this.player1Name = game.getPlayer1().getName();
        this.player2Name = game.getPlayer2().getName();
        if (this.player1Name.length() > 20) this.player1Name = this.player1Name.substring(0, 17) + "...";
        if (this.player2Name.length() > 20) this.player2Name = this.player2Name.substring(0, 17) + "...";
        this.player1Color = ColorValue.valueToColor(ColorValue.getHighlightColor(game.getPlayer1().getColorValue()));
        this.player2Color = ColorValue.valueToColor(ColorValue.getHighlightColor(game.getPlayer2().getColorValue()));
        this.nameTextBoxWidth = ceilToTens(getTextSize(player1Name).width + 20);
        setPreferredSize(new Dimension(
                2 * (nameTextBoxWidth + rectSpacing + scoreTextBoxWidth) + 2 * rectSpacing,
                scoreSliderHeight + rectSpacing / 2 + textBoxHeight + rectSpacing / 2 + lineThickness
        ));
    }

    public void update() {
        repaint();
    }

    private static int ceilToTens(int number) {
        return (int) (Math.ceil((double) number / 10) * 10);
    }

    private static Dimension getTextSize(String text) {
        JLabel label = new JLabel(text);
        label.setFont(scorePanelFont);
        return label.getPreferredSize();
    }

    private static Dimension getTextSize(Graphics g, String text) {
        Graphics2D gg = (Graphics2D) g.create();
        FontMetrics fm = gg.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(text, gg);
        gg.dispose();
        return new Dimension((int) Math.ceil(rect.getWidth()), (int) Math.ceil(rect.getHeight()));
    }

    private void renderRect(Graphics g, int x, int y, int width, int height, int arcDiameter, int strokeThickness, Color color) {
        Graphics2D gg = (Graphics2D) g.create();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (strokeThickness == 0) {
            gg.setColor(color);
            if (arcDiameter == 0) gg.fillRect(x, y, width, height);
            else gg.fillRoundRect(x, y, width, height, arcDiameter, arcDiameter);
        } else {
            gg.setColor(color.darker());
            if (arcDiameter == 0) gg.fillRect(x, y, width, height);
            else gg.fillRoundRect(x, y, width, height, arcDiameter, arcDiameter);
            gg.setColor(color);
            if (arcDiameter == 0) gg.fillRect(x + strokeThickness, y + strokeThickness,
                    width - 2 * strokeThickness, height - 2 * strokeThickness);
            else gg.fillRoundRect(x + strokeThickness, y + strokeThickness,
                    width - 2 * strokeThickness, height - 2 * strokeThickness,
                    arcDiameter - strokeThickness, arcDiameter - strokeThickness);
        }
        gg.dispose();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void renderScoreSlider(Graphics g) {
        int innerX = scoreSliderStrokeThickness,
            innerY = scoreSliderStrokeThickness,
            innerWidth = getPreferredSize().width - 2 * scoreSliderStrokeThickness,
            innerHeight = scoreSliderHeight - 2 * scoreSliderStrokeThickness,
            innerArcDiameter = scoreSliderArcDiameter - 2 * scoreSliderStrokeThickness,
            innerRectX = scoreSliderArcDiameter / 2,
            innerRectY = innerY,
            innerRectWidth = getPreferredSize().width - scoreSliderArcDiameter,
            innerRectHeight = innerHeight,
            player1RectWidth = (int) Math.ceil(innerRectWidth * game.getPlayer1().getScore() / (double) game.getBoard().getMaxScore()),
            player2RectWidth = (int) Math.ceil(innerRectWidth * game.getPlayer2().getScore() / (double) game.getBoard().getMaxScore());
        renderRect(g, 0, 0, getPreferredSize().width, scoreSliderHeight, scoreSliderArcDiameter, scoreSliderStrokeThickness, Color.DARK_GRAY);
        if (player1RectWidth > 0) {
            if (player1RectWidth == innerRectWidth) {
                renderRect(g, innerX, innerY, innerWidth, innerHeight, innerArcDiameter, 0, player1Color);
            } else {
                renderRect(g, innerX, innerY, innerArcDiameter, innerHeight, innerArcDiameter, 0, player1Color);
                renderRect(g, innerRectX, innerRectY, innerArcDiameter / 2, innerRectHeight, 0, 0, Color.DARK_GRAY);
                renderRect(g, innerRectX, innerRectY, player1RectWidth, innerRectHeight, 0, 0, player1Color);
            }
        }
        if (player2RectWidth > 0) {
            if (player2RectWidth == innerRectWidth) {
                renderRect(g, innerX, innerY, innerWidth, innerHeight, innerArcDiameter, 0, player2Color);
            } else {
                renderRect(g, getPreferredSize().width - innerX - innerHeight, innerY, innerArcDiameter, innerHeight, innerArcDiameter, 0, player2Color);
                renderRect(g, getPreferredSize().width - innerRectX - innerArcDiameter / 2, innerRectY, innerArcDiameter / 2, innerRectHeight, 0, 0, Color.DARK_GRAY);
                renderRect(g, getPreferredSize().width - innerRectX - player2RectWidth, innerRectY, player2RectWidth, innerRectHeight, 0, 0, player2Color);
            }
        }
        renderRect(g, (getPreferredSize().width - scoreSliderStrokeThickness) / 2, 0, 2, scoreSliderHeight, 0, 0, Color.DARK_GRAY.darker());
    }

    private void renderTextBox(Graphics g, int x, int textBoxWidth, Color textBoxColor, String text, Color textColor) {
        int y = scoreSliderHeight + rectSpacing / 2;
        renderRect(g, x, y, textBoxWidth, textBoxHeight, textBoxArcDiameter, textBoxStrokeThickness, textBoxColor);
        Graphics2D gg = (Graphics2D) g.create();
        gg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gg.setFont(scorePanelFont);
        gg.setColor(textColor);
        Dimension textSize = getTextSize(gg, text);
        gg.drawString(text,x + (textBoxWidth - textSize.width) / 2,
                y + (textBoxHeight + textSize.height) / 2 - textHeightCompensation);
        gg.dispose();
    }

    private void renderLine(Graphics g, int x, Color lineColor) {
        renderRect(g,
                x + textBoxArcDiameter / 2,
                scoreSliderHeight + rectSpacing / 2 + textBoxHeight + rectSpacing / 2,
                nameTextBoxWidth - textBoxArcDiameter,
                lineThickness,
                lineArcDiameter,
                textBoxStrokeThickness,
                lineColor
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        renderScoreSlider(g);

        renderTextBox(g, 0, nameTextBoxWidth, player1Color, player1Name, Color.WHITE);
        renderTextBox(g, nameTextBoxWidth + rectSpacing, scoreTextBoxWidth, Color.WHITE,
                String.valueOf(game.getPlayer1().getScore()), player1Color);

        renderTextBox(g, getPreferredSize().width - nameTextBoxWidth, nameTextBoxWidth, player2Color,
                player2Name, Color.WHITE);
        renderTextBox(g, getPreferredSize().width - nameTextBoxWidth - rectSpacing - scoreTextBoxWidth,
                scoreTextBoxWidth, Color.WHITE,  String.valueOf(game.getPlayer2().getScore()), player2Color);

        if (game.getCurrentPlayer().equals(game.getPlayer1())) {
            renderLine(g, 0, player1Color);
        } else {
            renderLine(g, getPreferredSize().width - nameTextBoxWidth, player2Color);
        }
    }
}