package ru.nsu.g13204.fit.pixel2d.vectors;

public class Vec2d {
    private double x = 0;
    private double y = 0;

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2d(Vec2d p0) {
        x = p0.getX();
        y = p0.getY();
    }

    public Vec2d(Vec2dI dp) {
        x = dp.getX();
        y = dp.getY();
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

    public void set(Vec2d other) {
        x = other.x;
        y = other.y;
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

    public Vec2d multiple(double other) {
        return new Vec2d((x * other), (y * other));
    }

    public void turnBack() {
        // Разворачивает вектор
        x = -x;
        y = -y;
    }

    public Vec2d normal() {
        // Возвращает нормаль, которая всегда смотрит вверх (в сторону увеличения координаты y
        //noinspection SuspiciousNameCombination
        Vec2d normalVec = new Vec2d(-y, x);
        if (x < 0) normalVec.turnBack();
        return normalVec;
    }

    public void normalization() {
        double len = length();
        x /= len;
        y /= len;
    }

    public void move(Vec2d other) {
        x += other.x;
        y += other.y;
    }

    public void move(Vec2dI other) {
        x += other.getX();
        y += other.getY();
    }

    public void swap(Vec2d other) {
        double res = x;
        x = other.x;
        other.x = res;

        res = y;
        y = other.y;
        other.y = res;
    }

    public double length() {
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

    public String toString() {
        return "(" + Double.toString(x) + "; "  + Double.toString(y) + ")";
    }
}
