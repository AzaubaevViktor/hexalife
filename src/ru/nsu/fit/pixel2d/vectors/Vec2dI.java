package ru.nsu.fit.pixel2d.vectors;

public class Vec2dI {
    private int x = 0;
    private int y = 0;

    public Vec2dI(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        // Если тут будет более сложная логика, не забывать переделать везде на getX()
        return x;
    }

    public int getY() {
        // Если тут будет более сложная логика, не забывать переделать везде на getY()
        return y;
    }

    public void setX(int newX) {
        // Если тут будет более сложная логика, не забывать переделать везде на setX()
        x = newX;
    }

    public void setY(int newY) {
        // Если тут будет более сложная логика, не забывать переделать везде на setY()
        y = newY;
    }

    public int[] getArr() {
        return new int[]{x, y};
    }

    public void reflect() {
        int res = y;
        //noinspection SuspiciousNameCombination
        y = x;
        x = res;
    }

    public boolean isXmore(Vec2dI other) {
        return x > other.x;
    }

    public Vec2dI minus(Vec2dI other) {
        return new Vec2dI(x - other.x, y - other.y);
    }

    public Vec2dI multipleInt(double other) {
        return new Vec2dI((int) (x * other), (int) (y * other));
    }

    public void swap(Vec2dI other) {
        int res = x;
        x = other.x;
        other.x = res;

        res = y;
        y = other.y;
        other.y = res;
    }

    public double lenght() {
        return Math.sqrt(x * x + y * y);
    }

    public int dy(Vec2dI other) {
        return y - other.y;
    }

    public int dx(Vec2dI other) {
        return x - other.x;
    }

    public boolean eq(Vec2dI other) {
        return x == other.x && y == other.y;
    }

    public Vec2dI copy() {
        return new Vec2dI(x, y);
    }
}
