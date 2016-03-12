package ru.nsu.fit;

import java.util.Observable;
import ru.nsu.fit.pixel2d.vectors.Vec2dI;

import java.util.ArrayList;
import java.util.List;

public class Model extends Observable {
    private double [][] impact; // y x
    private boolean [][] states; // y x
    private int width = 0, height = 0;
    public boolean isRun = false;

    // GameParams

    private double liveBegin = 2.0, liveEnd = 3.3, birthBegin = 2.3, birthEnd = 2.9, fstImpact = 1.0, sndImpact = 0.3;

    public Model(int width, int height) {
        this.width = width;
        this.height = height;
        impact = new double[height][width];
        states = new boolean[height][width];
    }

    public Vec2dI getSize() {
        return new Vec2dI(width, height);
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
        List<Vec2dI> cells = new ArrayList<>();
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

    public void set(Vec2dI cell, boolean b) {
        set(cell.getY(), cell.getX(), b);
    }

    private void unupdateSet(int y, int x, boolean isAlive) {
        if (checkXY(x, y)) {
            states[y][x] = isAlive;
        }
    }

    public void set(int y, int x, boolean isAlive) {
        unupdateSet(y, x, isAlive);
        setChanged();
        notifyObservers();
    }

    private boolean get(int y, int x) {
        // Про нечётные строки
        return checkXY(x, y) && states[y][x];
    }

    public double getImpact(int y, int x) {
        if (checkXY(x, y)) {
            return impact[y][x];
        } else {
            return 0;
        }

    }

    public void reCalcImpact() {
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
                int[] height = {0, 0};
                for (int j = 0; j < 12; j++) {
                    int xi = x + matrix[y % 2][j][1];
                    int yi = y + matrix[y % 2][j][0];
                    if (get(yi, xi)) height[matrix[y % 2][j][2]] += 1;
                }
                impact[y][x] = height[0] * fstImpact + height[1] * sndImpact;
            }
        }
    }

    private void setStatesByImpact() {
        double imp;
        boolean newState;
        for (int y = 0; y < height; y++) {
            int width_line = getWidthLine(y);
            for (int x = 0; x < width_line; x++) {
                imp = impact[y][x];
                // Смерть от одиночества или перенаселённости или продолжение нежития
                if ((imp < liveBegin) || (imp > liveEnd)) newState = false;
                // Рождение если мёртвый, продолжение жизни если живой
                else if ((birthBegin <= imp) && (imp <= birthEnd)) newState = true;
                // Продолжение жизни
                else if (get(y, x) && (liveBegin <= imp) && (imp <= liveEnd)) {
                    newState = true;
                }
                // Смерть в ином случае
                else newState = false;

                unupdateSet(y, x, newState);
            }
        }
    }

    public double[] getParams() {
        return new double[]{liveBegin, birthBegin, birthEnd, liveEnd};
    }

    public void setParams(double [] params) throws ChangeParamsError {
        checkParams(params);
        liveBegin = params[0];
        birthBegin = params[1];
        birthEnd = params[2];
        liveEnd = params[3];
        setChanged();
        notifyObservers();
    }

    public void checkParams(double[] params) throws ChangeParamsError {
        if (params.length != 4) {
            throw new ChangeParamsError("Ошибка при установке параметров");
        }

        for (int i = 0; i < 3; i++) {
            if (params[i] >= params[i + 1]) {
                throw new ChangeParamsError("Параметры должны возрастать");
            }
            if ((params[i] < 0) || (params[i + 1] < 0)) {
                throw new ChangeParamsError("Праметры не могут быть отрицательными");
            }
        }
    }


    public void step() {
        setStatesByImpact();
        reCalcImpact();
        setChanged();
        notifyObservers();
    }

    public void changeSize(int width, int height) throws ChangeParamsError {
        if (checkSize(width, height)) return;

        double [][] newImpact = new double[height][width];
        boolean [][] newStates = new boolean[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                newImpact[y][x] = getImpact(y, x);
                newStates[y][x] = get(y, x);
            }
        }

        impact = newImpact;
        states = newStates;
        this.width = width;
        this.height = height;
        setChanged();
        notifyObservers();
    }

    public boolean checkSize(int width, int height) throws ChangeParamsError {
        if ((this.width == width) && (this.height == height)) return true;

        if ((width < 0) || (height < 0))
            throw new ChangeParamsError("Размеры сетки должны быть положительными");
        return false;
    }

    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < getWidthLine(y); x++) {
                states[y][x] = false;
                impact[y][x] = 0.;
            }
        }
        setChanged();
        notifyObservers();
    }
}

