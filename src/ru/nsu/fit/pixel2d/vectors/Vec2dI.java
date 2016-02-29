package ru.nsu.fit.pixel2d.vectors;

public class Vec2dI {
    // Класс для работы с ТОЧКАМИ НА ЭКРАНЕ. Особенность в целочисленности
    private int x = 0;
    private int y = 0;

    public Vec2dI(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2dI(Vec2dI p0) {
        set(p0);
    }
    
    public Vec2dI(Vec2d v) {
        set(v);
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

    public void set(Vec2dI other) {
        x = other.x;
        y = other.y;
    }

    public void set(Vec2d other) {
        x = (int) other.getX();
        y = (int) other.getY();
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

    public Vec2dI multiple(int other) {
        return new Vec2dI((x * other), (y * other));
    }

    public void turnBack() {
        // Разворачивает вектор
        x = -x;
        y = -y;
    }

    public Vec2dI normal() {
        // Возвращает нормаль, которая всегда смотрит вверх (в сторону увеличения координаты y
        //noinspection SuspiciousNameCombination
        Vec2dI nrml = new Vec2dI(-y, x);
        if (x < 0) nrml.turnBack();
        return nrml;
    }

    public void move(Vec2d other) {
        x += other.getX();
        y += other.getY();
    }

    public void swap(Vec2dI other) {
        int res = x;
        x = other.x;
        other.x = res;

        res = y;
        y = other.y;
        other.y = res;
    }

    public int dy(Vec2dI other) {
        return y - other.y;
    }

    public int dx(Vec2dI other) {
        return x - other.x;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vec2dI other = (Vec2dI) obj;
        return equals(other);
    }

    public boolean equals(Vec2dI other) {
        return x == other.x && y == other.y;
    }

    public Vec2dI copy() {
        return new Vec2dI(x, y);
    }

    public String toString() {
        return "(" + Integer.toString(x) + "; "  + Integer.toString(y) + ")";
    }

}
