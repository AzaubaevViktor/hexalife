package ru.nsu.fit.pixel2d.vectors;

public class Vec2dI {
    private int x = 0;
    private int y = 0;

    public Vec2dI(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int newX) {
        x = newX;
    }

    public void setY(int newY) {
        y = newY;
    }

    public int[] getArr() {
        return new int[]{x, y};
    }
}
