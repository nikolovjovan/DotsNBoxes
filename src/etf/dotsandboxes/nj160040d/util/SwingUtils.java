package etf.dotsandboxes.nj160040d.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SwingUtils {

    public static BufferedImage loadImage(String fileName) {
        try {
            return ImageIO.read(new File(fileName));
        } catch (IOException e) {
            return null;
        }
    }

    public static BufferedImage resizeImage(BufferedImage image, int scaledWidth, int scaledHeight, boolean smooth) {
        if (image == null) return null;
        BufferedImage output = new BufferedImage(scaledWidth, scaledHeight, image.getType());
        Graphics2D gg = output.createGraphics();
        if (!smooth) {
            gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            gg.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
        } else {
            Image tmp = image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
            gg.drawImage(tmp, 0, 0, null);
        }
        gg.dispose();
        return output;
    }

    public static BufferedImage resizeImageToFit(BufferedImage image, Dimension availableSpace, boolean smooth) {
        double scale = Math.min(
                availableSpace.width / (double) image.getWidth(),
                availableSpace.height / (double) image.getHeight());
        return resizeImage(image, (int) (image.getWidth() * scale), (int) (image.getHeight() * scale), smooth);
    }

    public static List<BufferedImage> createIconImages(String iconFileName, int[] iconSizes) {
        if (iconFileName == null || iconSizes == null || iconSizes.length == 0) return null;
        BufferedImage sourceImage = loadImage(iconFileName);
        if (sourceImage == null) return null;
        ArrayList<BufferedImage> iconImages = new ArrayList<>(iconSizes.length);
        for (int iconSize : iconSizes) {
            iconImages.add(resizeImageToFit(sourceImage, new Dimension(iconSize, iconSize), true));
        }
        return iconImages;
    }

    public static GridBagConstraints createConstraints(int inset, boolean resizable) {
        GridBagConstraints constraints = new GridBagConstraints();
        if (inset > 0) constraints.insets = new Insets(inset, inset, inset, inset);
        if (resizable) {
            constraints.weightx = constraints.weighty = 1;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.anchor = GridBagConstraints.CENTER;
        }
        constraints.gridx = constraints.gridy = 0;
        return constraints;
    }

    public static void addComponentHorizontally(JPanel panel, Component component, GridBagConstraints constraints) {
        if (component == null) return;
        if (constraints == null) {
            panel.add(component);
        } else {
            panel.add(component, constraints);
            ++constraints.gridx;
        }
    }

    public static void addComponentVertically(JPanel panel, Component component, GridBagConstraints constraints) {
        if (component == null) return;
        if (constraints == null) {
            panel.add(component);
        } else {
            panel.add(component, constraints);
            ++constraints.gridy;
        }
    }

    public static Component createHorizontalSpacer(int width) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(width, 5));
        return label;
    }

    public static Component createVerticalSpacer(int height) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(5, height));
        return label;
    }

    public static void addHorizontalSpacer(JPanel panel, GridBagConstraints constraints, int width) {
        addComponentHorizontally(panel, createHorizontalSpacer(width), constraints);
    }

    public static void addVerticalSpacer(JPanel panel, GridBagConstraints constraints, int height) {
        addComponentVertically(panel, createVerticalSpacer(height), constraints);
    }

    public static void addSplitPanel(JPanel panel, GridBagConstraints constraints, JPanel leftPanel, JPanel rightPanel) {
        JPanel splitPanel = new JPanel();
        GridLayout splitPanelLayout = new GridLayout(1, 2);
        splitPanelLayout.setHgap(15);
        splitPanel.setLayout(splitPanelLayout);
        splitPanel.add(leftPanel);
        splitPanel.add(rightPanel);
        addComponentVertically(panel, splitPanel, constraints);
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

    public static Border createTitledBorder(String title, int padding) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding));
    }

    public static Border createTitledBorder(String title) {
        return createTitledBorder(title, 5);
    }

    public static Dimension getTextSize(JLabel label, String text, Font font) {
        if (label == null) return getTextSize(text, font);
        label.setText(text);
        label.setFont(font);
        return label.getPreferredSize();
    }

    public static Dimension getTextSize(String text, Font font) {
        return getTextSize(new JLabel(), text, font);
    }

    public static Dimension getTextSize(Graphics g, String text) {
        if (text == null) return null;
        Graphics2D gg = (Graphics2D) g.create();
        FontMetrics fm = gg.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(text, gg);
        gg.dispose();
        return new Dimension((int) Math.ceil(rect.getWidth()), (int) Math.ceil(rect.getHeight()));
    }
}