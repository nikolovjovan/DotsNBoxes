package etf.dotsandboxes.nj160040d.gui;

import java.awt.*;

public class ColorValue {
    public static final byte TRANSPARENT = 0x0;
    public static final byte BLACK = (byte) 0xFF;

    public static final byte OPAQUE_FLAG = 0x1;
    public static final byte LIGHT_FLAG  = 0x2;

    public static final byte BLUE        = 0x1 << 4 | OPAQUE_FLAG;
    public static final byte RED         = 0x1 << 5 | OPAQUE_FLAG;

    public static final byte LIGHT_BLUE  = BLUE | LIGHT_FLAG;
    public static final byte LIGHT_RED   = RED | LIGHT_FLAG;

    public static byte getHighlightColor(byte colorValue) { return (byte) (colorValue | LIGHT_FLAG); }

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
}