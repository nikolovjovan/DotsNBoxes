package etf.dotsandboxes.nj160040d.logic;

public class Box {

    byte value;
    int x, y;

    public Box() {
        value = -1;
        x = y = -1;
    }

    public Box(byte value, int x, int y) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

    public byte getValue() { return value; }
    public void setValue(byte value) { this.value = value; }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setX(int value) { x = value; }
    public void setY(int value) { y = value; }
}