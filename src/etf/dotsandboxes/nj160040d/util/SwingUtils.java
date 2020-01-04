package etf.dotsandboxes.nj160040d.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SwingUtils {

    public static Image resizeImage(String inputImagePath, int scaledWidth, int scaledHeight) throws IOException {
        BufferedImage inputImage = ImageIO.read(new File(inputImagePath));
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());
        Graphics2D g2d = outputImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        return outputImage;
    }

    public static Image scaleImage(String inputImagePath, int scaledWidth) throws IOException {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
        int scaledHeight = (int) (inputImage.getHeight() * (scaledWidth / (double) inputImage.getWidth()));
        return resizeImage(inputImagePath, scaledWidth, scaledHeight);
    }

    public static Image scaleImage(String inputImagePath, double percentage) throws IOException {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
        int scaledWidth = (int) (inputImage.getWidth() * percentage);
        int scaledHeight = (int) (inputImage.getHeight() * percentage);
        return resizeImage(inputImagePath, scaledWidth, scaledHeight);
    }

    public static JLabel createEmptyLabel(Dimension d) {
        JLabel label = new JLabel();
        label.setPreferredSize(d);
        return label;
    }

    public static Border createTitledBorder(String title, int padding) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding));
    }

    public static Border createTitledBorder(String title) {
        return createTitledBorder(title, 5);
    }

    public static void addSplitPanel(JPanel panel, GridBagConstraints constraints, JPanel leftPanel, JPanel rightPanel) {
        JPanel splitPanel = new JPanel();
        GridLayout splitPanelLayout = new GridLayout(1, 2);
        splitPanelLayout.setHgap(15);
        splitPanel.setLayout(splitPanelLayout);
        splitPanel.add(leftPanel);
        splitPanel.add(rightPanel);
        ++constraints.gridy;
        panel.add(splitPanel, constraints);
    }

    public static void setPanelEnabled(JPanel panel, Boolean isEnabled) {
        panel.setEnabled(isEnabled);
        for (Component c : panel.getComponents()) {
            if (JPanel.class.isAssignableFrom(c.getClass())) {
                setPanelEnabled((JPanel) c, isEnabled);
            } else {
                c.setEnabled(isEnabled);
            }
        }
    }
}