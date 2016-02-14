package ru.nsu.fit.pixel2d.vectors;

public class BasicLine {
    private int x0;
    private int y0;
    private int x1;
    private int y1;

    public BasicLine(int x0, int y0, int x1, int y1) {
        set(x0, y0, x1, y1);
    }

    public BasicLine(Vec2dI start, int length) {
        set(start, length);
    }

    public BasicLine(Vec2dI start, Vec2dI end) {
        set(start, end);
    }

    public BasicLine(Vec2dI dot) {
        x0 = x1 = dot.getX();
        y0 = y1 = dot.getY();
    }

    public void set(int x0, int y0, int x1, int y1) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public void set(Vec2dI start, Vec2dI end) {
        x0 = start.getX();
        y0 = start.getY();
        x1 = end.getX();
        y1 = end.getY();
    }

    public void set(Vec2dI start, int length) {
        int x = start.getX();
        int y = start.getY();
        set(x, y, x + length, y);
    }

    public boolean isHorisontal() {
        return y0 == y1;
    }

    public void extendX() {
        x1 += 1;
    }

    public int getX0() {
        return x0;
    }

    public void setX0(int x0) {
        this.x0 = x0;
    }

    public int getX1() {
        return x1;
    }

    public int getY0() {
        return y0;
    }

    public Vec2dI getStart() {
        return new Vec2dI(x0, y0);
    }

    public Vec2dI getEnd() {
        return new Vec2dI(x1, y1);
    }
}
