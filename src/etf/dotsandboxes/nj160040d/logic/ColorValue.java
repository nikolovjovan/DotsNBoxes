package etf.dotsandboxes.nj160040d.logic;

public class ColorValue {
    public static final byte TRANSPARENT = 0x0;
    public static final byte BLACK = (byte) 0xFF;

    public static final byte OPAQUE_FLAG = 0x1;
    public static final byte LIGHT_FLAG  = 0x2;

    public static final byte BLUE        = 0x1 << 4 | OPAQUE_FLAG;
    public static final byte RED         = 0x1 << 5 | OPAQUE_FLAG;

    public static final byte LIGHT_BLUE  = BLUE | LIGHT_FLAG;
    public static final byte LIGHT_RED   = RED | LIGHT_FLAG;

    public static byte getLight(byte colorValue) { return (byte) (colorValue | LIGHT_FLAG); }
}