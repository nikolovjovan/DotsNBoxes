package etf.dotsandboxes.nj160040d.logic;

public class ColorValue {
    public static final byte TRANSPARENT = 0x0;
    public static final byte BLACK = (byte) 0xFF;

    public static final byte OPAQUE_FLAG = 0x1;
    public static final byte DARK_FLAG   = 0x2;

    public static final byte BLUE        = 0x1 << 4 | OPAQUE_FLAG;
    public static final byte RED         = 0x1 << 5 | OPAQUE_FLAG;

    public static final byte DARK_BLUE   = BLUE | DARK_FLAG;
    public static final byte DARK_RED    = RED | DARK_FLAG;

    public static byte getDark(byte colorValue) { return (byte) (colorValue | DARK_FLAG); }
}