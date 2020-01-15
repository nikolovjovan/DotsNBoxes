package etf.dotsandboxes.nj160040d.logic;

import etf.dotsandboxes.nj160040d.gui.ColorValue;

public class Edge {

    public static final Edge INVALID = new Edge();

    private byte value;
    private int x, y;
    private boolean horizontal;

    public Edge(byte value, int x, int y, boolean horizontal) {
        this.value = value;
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
    }

    public Edge(int x, int y, boolean horizontal) {
        this(ColorValue.TRANSPARENT, x, y, horizontal);
    }

    public Edge() {
        this((byte) 0, -1, -1, false);
    }

    public Edge getClone() { return new Edge(value, x, y, horizontal); }

    public void copy(Edge edge) {
        value = edge.value;
        x = edge.x;
        y = edge.y;
        horizontal = edge.horizontal;
    }

    public static Edge parseEdgeFromString(String string) {
        if (string == null || string.isEmpty()) return null;
        Edge edge = new Edge(0, 0, false);
        int i = 0;
        if (string.charAt(0) >= 'A' && string.charAt(0) <= 'Z') {
            while (i < string.length() && string.charAt(i) >= 'A' && string.charAt(i) <= 'Z')
                edge.y = edge.y * 26 + string.charAt(i++) - 'A';
            while (i < string.length() && string.charAt(i) >= '0' && string.charAt(i) <= '9')
                edge.x = edge.x * 10 + string.charAt(i++) - '0';
        } else if (string.charAt(0) >= '0' && string.charAt(0) <= '9') {
            edge.horizontal = true;
            while (i < string.length() && string.charAt(i) >= '0' && string.charAt(i) <= '9')
                edge.y = edge.y * 10 + string.charAt(i++) - '0';
            while (i < string.length() && string.charAt(i) >= 'A' && string.charAt(i) <= 'Z')
                edge.x = edge.x * 26 + string.charAt(i++) - 'A';
        }
        if (i < string.length()) return null;
        return edge;
    }

    public static String generateStringFromEdge(Edge edge) {
        StringBuilder sb = new StringBuilder();
        if (edge.isHorizontal()) sb.append(edge.getY());
        int dim = edge.isHorizontal() ? edge.getX() : edge.getY(), offset = sb.length();
        do {
            sb.insert(offset, (char) ('A' + dim % 26));
            dim /= 26;
        }
        while (dim > 0);
        if (!edge.isHorizontal()) sb.append(edge.getX());
        return sb.toString();
    }

    @Override
    public String toString() {
        if (!isValid()) return "Invalid Edge";
        return (horizontal ? "H" : "V") + x + "x" + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Edge)) return false;
        Edge e = (Edge) obj;
        return horizontal == e.horizontal && x == e.x && y == e.y;
    }

    public boolean isValid() { return x >= 0 && y >= 0; }
    public void invalidate() { x = y = -1; }

    public byte getValue() { return value; }
    public void setValue(byte value) { this.value = value; }

    public boolean isHorizontal() { return horizontal; }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setAsTopEdge(int boardX, int boardY) { horizontal = true; x = boardX; y = boardY; }
    public void setAsBottomEdge(int boardX, int boardY) { horizontal = true; x = boardX; y = boardY + 1; }
    public void setAsLeftEdge(int boardX, int boardY) { horizontal = false; x = boardX; y = boardY; }
    public void setAsRightEdge(int boardX, int boardY) { horizontal = false; x = boardX + 1; y = boardY; }
}