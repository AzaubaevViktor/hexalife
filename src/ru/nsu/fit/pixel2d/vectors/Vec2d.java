package ru.nsu.fit.pixel2d.vectors;

public class Vec2d {
    private double x = 0;
    private double y = 0;

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        // Если тут будет более сложная логика, не забывать переделать везде на getX()
        return x;
    }

    public double getY() {
        // Если тут будет более сложная логика, не забывать переделать везде на getY()
        return y;
    }

    public void setX(double newX) {
        // Если тут будет более сложная логика, не забывать переделать везде на setX()
        x = newX;
    }

    public void setY(double newY) {
        // Если тут будет более сложная логика, не забывать переделать везде на setY()
        y = newY;
    }

    public double[] getArr() {
        return new double[]{x, y};
    }

    public void reflect() {
        double res = y;
        //noinspection SuspiciousNameCombination
        y = x;
        x = res;
    }

    public boolean isXmore(Vec2d other) {
        return x > other.x;
    }

    public Vec2d minus(Vec2d other) {
        return new Vec2d(x - other.x, y - other.y);
    }

    public Vec2d multipledouble(double other) {
        return new Vec2d((x * other), (y * other));
    }

    public Vec2d move(Vec2d other) {
        return new Vec2d(x + other.x, y + other.y);
    }

    public void swap(Vec2d other) {
        double res = x;
        x = other.x;
        other.x = res;

        res = y;
        y = other.y;
        other.y = res;
    }

    public double lenght() {
        return Math.sqrt(x * x + y * y);
    }

    public double dy(Vec2d other) {
        return y - other.y;
    }

    public double dx(Vec2d other) {
        return x - other.x;
    }

    public boolean eq(Vec2d other) {
        return x == other.x && y == other.y;
    }

    public Vec2d copy() {
        return new Vec2d(x, y);
    }
}
