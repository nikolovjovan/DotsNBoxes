package etf.dotsandboxes.nj160040d.gui;

import java.awt.*;

public class ColorValue {
    public static final byte TRANSPARENT = 0x0;
    public static final byte BLACK = (byte) 0xFF;

    public static final byte OPAQUE_FLAG    = 0x1;
    public static final byte DARK_FLAG      = 0x2;

    public static final byte BLUE       = 0x1 << 4 | OPAQUE_FLAG;
    public static final byte RED        = 0x1 << 5 | OPAQUE_FLAG;

    public static final byte DARK_BLUE  = BLUE | DARK_FLAG;
    public static final byte DARK_RED   = RED | DARK_FLAG;

    public static byte getLastEdgeColor(byte colorValue) { return (byte) (colorValue | DARK_FLAG); }

    public static final Color colorTransparent = new Color(0, 0, 0, 0);
    public static final Color colorBlue        = new Color(48, 140, 255);
    public static final Color colorRed         = new Color(255, 64, 48);
    public static final Color colorDarkBlue    = new Color(0, 80, 240);
    public static final Color colorDarkRed     = new Color(240, 8, 0);
    public static final Color colorBlack       = new Color(16, 16, 16);

    public static Color valueToColor(byte value) {
        switch (value) {
            case ColorValue.TRANSPARENT:    return colorTransparent;
            case ColorValue.BLUE:           return colorBlue;
            case ColorValue.RED:            return colorRed;
            case ColorValue.DARK_BLUE:      return colorDarkBlue;
            case ColorValue.DARK_RED:       return colorDarkRed;
            case ColorValue.BLACK:          return colorBlack;
            default:                        return null;
        }
    }
}