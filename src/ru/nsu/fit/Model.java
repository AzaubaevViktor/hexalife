package ru.nsu.fit;

import java.util.Observable;
import ru.nsu.fit.pixel2d.vectors.Vec2dI;

import java.util.ArrayList;
import java.util.List;

public class Model extends Observable {
    public double [][] impact; // y x
    public boolean [][] states; // y x
    private int width = 0, height = 0;

    // GameParams

    public double LIVE_BEGIN = 2.0, LIVE_END = 3.3, BIRTH_BEGIN = 2.3, BIRTH_END = 2.9, FST_IMPACT = 1.0, SND_IMPACT = 0.3;

    public Model(int width, int height) {
        this.width = width;
        this.height = height;
        impact = new double[height][width];
        states = new boolean[height][width];
    }

    private int getWidthLine(int y) {
        return width + ((y % 2 == 0) ? 0 : -1);
    }

    private boolean checkXY(int x, int y) {
        return ((0 <= x) && (x < getWidthLine(y))) && ((0 <= y) && (y < height));
    }

    public void randomGenerate() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < getWidthLine(y); x++) {
                if (Math.random() > 0.8) {
                    states[y][x] = true;
                }
            }
        }
        setChanged();
        notifyObservers();
    }

    public List<Vec2dI> getStates() {
        List<Vec2dI> cells = new ArrayList<Vec2dI>();
        for (int y = 0; y < height; y++) {
            int widthLine = getWidthLine(y);
            for (int x = 0; x < widthLine; x++) {
                if (states[y][x]) {
                    cells.add(new Vec2dI(x, y));
                }
            }
        }
        return cells;
    }

    public void set(int y, int x, boolean isAlive) {
        if (checkXY(x, y)) {
            states[y][x] = isAlive;
        }
    }

    private boolean get(int y, int x) {
        // Про нечётные строки
        return checkXY(x, y) && states[y][x];
    }

    public double getImpact(int y, int x) {
        if (checkXY(x, y)) {
            return impact[y][x];
        } else {
            return -1;
        }

    }

    public void recalcImpact() {
        // Матрица: dy, dx, dist
        // Эта для строк
        int[][][] matrix = {
                { // Для нечётных
                        {-2, 0, 1},
                        {-1, -2, 1}, {-1, -1, 0}, {-1, 0, 0}, {-1, 1, 1},
                        {0, -1, 0}, {0, 1, 0},
                        {1, -2, 1}, {1, -1, 0}, {1, 0, 0}, {1, 1, 1},
                        {2, 0, 1}
                },
                { // Для чётных
                        {-2, 0, 1},
                        {-1, -1, 1}, {-1, 0, 0}, {-1, 1, 0}, {-1, 2, 1},
                        {0, -1, 0}, {0, 1, 0},
                        {1, -1, 1}, {1, 0, 0}, {1, 1, 0}, {1, 2, 1},
                        {2, 0, 1}
                }
        };

        for (int y = 0; y < height; y++) {
            int width_line = getWidthLine(y);
            for (int x = 0; x < width_line; x++) {
                int[] neight = {0, 0};
                for (int j = 0; j < 12; j++) {
                    int xi = x + matrix[y % 2][j][1];
                    int yi = y + matrix[y % 2][j][0];
                    if (get(yi, xi)) neight[matrix[y % 2][j][2]] += 1;
                }
                impact[y][x] = neight[0] * FST_IMPACT + neight[1] * SND_IMPACT;
            }
        }
    }

    private void setStatesByImpact() {
        double imp;
        for (int y = 0; y < height; y++) {
            int width_line = getWidthLine(y);
            for (int x = 0; x < width_line; x++) {
                imp = impact[y][x];
                // Смерть от одиночества или перенаселённости или продолжение нежития
                if ((imp < LIVE_BEGIN) || (imp > LIVE_END)) set(y, x, false);
                // Рождение если мёртвый, продолжение жизни если живой
                else if ((BIRTH_BEGIN <= imp) && (imp <= BIRTH_END)) set(y, x, true);
                // Продолжение жизни
                else if (get(y, x) && (LIVE_BEGIN <= imp) && (imp <= LIVE_END)) {
                    set(y, x, true);
                }
                // Смерть в ином случае
                else set(y, x, false);
            }
        }
    }

    public void step() {
        recalcImpact();
        setStatesByImpact();
        setChanged();
        notifyObservers();
    }
}
