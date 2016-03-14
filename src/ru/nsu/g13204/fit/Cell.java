package ru.nsu.g13204.fit;

import ru.nsu.g13204.fit.pixel2d.vectors.Vec2dI;


class Cell extends Vec2dI {
    private double impact;

    public Cell(int x, int y, double impact) {
        super(x, y);
        this.impact = impact;
    }

    public double getImpact() {
        return impact;
    }

    public void setImpact(int impact) {
        this.impact = impact;
    }
}
