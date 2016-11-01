package de.hannisoft.gaveplan.model;

public class Point {
    private final int id;
    private int x;
    private int y;

    public Point(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void rotate(double degree) {
        if (degree == 0) {
            return;
        }
        double xd = x * Math.cos(degree) - y * Math.sin(degree);
        x = Long.valueOf(Math.round(xd)).intValue();
        double yd = x * Math.sin(degree) + y * Math.cos(degree);
        y = Long.valueOf(Math.round(yd)).intValue();
    }

    public void scale(double xFactor, double yFactor) {
        x = Long.valueOf(Math.round(x * xFactor)).intValue();
        y = Long.valueOf(Math.round(y * yFactor)).intValue();
    }

    public void move(int xDelta, int yDelta) {
        x = x + xDelta;
        y = y + yDelta;
    }
}
