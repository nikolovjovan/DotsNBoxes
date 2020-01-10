package etf.dotsandboxes.nj160040d.gui;

import etf.dotsandboxes.nj160040d.logic.State;
import etf.dotsandboxes.nj160040d.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ScorePanel extends JPanel {

    private static final int maxPlayerNameLength = 25;

    private static final Font scorePanelFont = new Font("Arial", Font.BOLD, 24);
    private static final int textHeightCompensation = 6; // stupid text rendering and text height...

    private static final int scoreSliderHeight = 20;
    private static final int scoreSliderArcDiameter = scoreSliderHeight;
    private static final int scoreSliderStrokeThickness = 1;

    private static final int textBoxHeight = ceilToTens(SwingUtils.getTextSize("WQXO", scorePanelFont).height + 20);
    private static final int textBoxArcDiameter = textBoxHeight / 4;
    private static final int textBoxStrokeThickness = 1;

    private static final int rectSpacing = 10;
    private static final int lineThickness = 4;
    private static final int lineArcDiameter = lineThickness;

    private State state;

    private String message, player1Name, player2Name;
    private Color messageColor, player1Color, player2Color;
    private Font messageFont;
    private int nameTextBoxWidth, scoreTextBoxWidth;

    public ScorePanel(State state) {
        this.state = state;
        this.player1Name = state.getPlayer1().getName();
        this.player2Name = state.getPlayer2().getName();
        if (this.player1Name.length() > maxPlayerNameLength) this.player1Name = this.player1Name.substring(0, maxPlayerNameLength - 3) + "...";
        if (this.player2Name.length() > maxPlayerNameLength) this.player2Name = this.player2Name.substring(0, maxPlayerNameLength - 3) + "...";
        this.messageColor = ColorValue.colorBlack;
        this.player1Color = ColorValue.valueToColor(state.getPlayer1().getColorValue());
        this.player2Color = ColorValue.valueToColor(state.getPlayer2().getColorValue());
        this.messageFont = scorePanelFont;
        this.nameTextBoxWidth = ceilToTens(Math.max(
                        SwingUtils.getTextSize(this.player1Name, scorePanelFont).width,
                        SwingUtils.getTextSize(this.player2Name, scorePanelFont).width) + 20);
        this.scoreTextBoxWidth = ceilToTens(
                SwingUtils.getTextSize(String.valueOf(state.getMaxScore()), scorePanelFont).width + 20);
        try {
            initUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() { repaint(); }

    public boolean canRenderMessage(String message) {
        if (message == null || message.length() == 0) return true;
        Dimension messageSize = SwingUtils.getTextSize(message, messageFont);
        int availableWidth = getWidth() - 2 * (nameTextBoxWidth + scoreTextBoxWidth + 2 * rectSpacing);
        return messageSize.width <= availableWidth && messageSize.height <= textBoxHeight;
    }

    public void setMessage(String message) { this.message = message; repaint(); }
    public void setMessageColor(Color messageColor) { this.messageColor = messageColor; }
    public void setMessageFont(Font messageFont) { this.messageFont = messageFont; }

    private void initUI() {
        setPreferredSize(new Dimension(
                2 * (nameTextBoxWidth + rectSpacing + scoreTextBoxWidth) + 2 * rectSpacing,
                scoreSliderHeight + rectSpacing / 2 + textBoxHeight + rectSpacing / 2 + lineThickness
        ));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                update();
            }
        });
    }

    private static int ceilToTens(int number) {
        return (int) (Math.ceil((double) number / 10) * 10);
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

    private void renderScoreSlider(Graphics g) {
        int innerX = scoreSliderStrokeThickness,
            innerY = scoreSliderStrokeThickness,
            innerWidth = getWidth() - 2 * scoreSliderStrokeThickness,
            innerHeight = scoreSliderHeight - 2 * scoreSliderStrokeThickness,
            innerArcDiameter = scoreSliderArcDiameter - 2 * scoreSliderStrokeThickness,
            innerRectX = scoreSliderArcDiameter / 2,
            innerRectWidth = getWidth() - scoreSliderArcDiameter,
            player1RectWidth = (int) Math.ceil(innerRectWidth * state.getPlayer1Score() / (double) state.getMaxScore()),
            player2RectWidth = (int) Math.ceil(innerRectWidth * state.getPlayer2Score() / (double) state.getMaxScore());
        renderRect(g, 0, 0, getWidth(), scoreSliderHeight, scoreSliderArcDiameter, scoreSliderStrokeThickness, Color.DARK_GRAY);
        if (player1RectWidth > 0) {
            if (player1RectWidth == innerRectWidth) {
                renderRect(g, innerX, innerY, innerWidth, innerHeight, innerArcDiameter, 0, player1Color);
            } else {
                renderRect(g, innerX, innerY, innerArcDiameter, innerHeight, innerArcDiameter, 0, player1Color);
                renderRect(g, innerRectX, innerY, innerArcDiameter / 2, innerHeight, 0, 0, Color.DARK_GRAY);
                renderRect(g, innerRectX, innerY, player1RectWidth, innerHeight, 0, 0, player1Color);
            }
        }
        if (player2RectWidth > 0) {
            if (player2RectWidth == innerRectWidth) {
                renderRect(g, innerX, innerY, innerWidth, innerHeight, innerArcDiameter, 0, player2Color);
            } else {
                renderRect(g, getWidth() - innerX - innerHeight, innerY, innerArcDiameter, innerHeight, innerArcDiameter, 0, player2Color);
                renderRect(g, getWidth() - innerRectX - innerArcDiameter / 2, innerY, innerArcDiameter / 2, innerHeight, 0, 0, Color.DARK_GRAY);
                renderRect(g, getWidth() - innerRectX - player2RectWidth, innerY, player2RectWidth, innerHeight, 0, 0, player2Color);
            }
        }
        renderRect(g, (getWidth() - scoreSliderStrokeThickness) / 2, 0, 2, scoreSliderHeight, 0, 0, Color.DARK_GRAY.darker());
    }

    private void renderCenteredText(Graphics g, int x, String text, Color textColor, Font textFont) {
        if (text == null) return;
        Graphics2D gg = (Graphics2D) g.create();
        gg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gg.setColor(textColor);
        gg.setFont(textFont);
        Dimension textSize = SwingUtils.getTextSize(gg, text);
        gg.drawString(text, x - textSize.width / 2, scoreSliderHeight + (rectSpacing + textBoxHeight + textSize.height) / 2 - textHeightCompensation);
        gg.dispose();
    }

    private void renderTextBox(Graphics g, int x, int textBoxWidth, Color textBoxColor, String text, Color textColor) {
        renderRect(g, x, scoreSliderHeight + rectSpacing / 2, textBoxWidth, textBoxHeight, textBoxArcDiameter, textBoxStrokeThickness, textBoxColor);
        renderCenteredText(g, x + textBoxWidth / 2, text, textColor, scorePanelFont);
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
                String.valueOf(state.getPlayer1Score()), player1Color);

        if (message != null && canRenderMessage(message)) {
            renderCenteredText(g, getWidth() / 2, message, messageColor, messageFont);
        }

        renderTextBox(g, getWidth() - nameTextBoxWidth, nameTextBoxWidth, player2Color,
                player2Name, Color.WHITE);
        renderTextBox(g, getWidth() - nameTextBoxWidth - rectSpacing - scoreTextBoxWidth,
                scoreTextBoxWidth, Color.WHITE,  String.valueOf(state.getPlayer2Score()), player2Color);

        if (state.getCurrentPlayer().equals(state.getPlayer1())) {
            renderLine(g, 0, player1Color);
        } else {
            renderLine(g, getWidth() - nameTextBoxWidth, player2Color);
        }
    }
}