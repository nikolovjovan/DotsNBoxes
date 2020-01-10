package etf.dotsandboxes.nj160040d.logic;

public class Box {

    private byte colorValue;
    private int x, y;

    public Box(byte colorValue, int x, int y) {
        this.colorValue = colorValue;
        this.x = x;
        this.y = y;
    }

    public Box(int x, int y) {
        this((byte) -1, x, y);
    }

    public Box() {
        this((byte) -1, -1, -1);
    }

    public byte getColorValue() { return colorValue; }
    public void setColorValue(byte colorValue) { this.colorValue = colorValue; }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int value) { x = value; }
    public void setY(int value) { y = value; }
}