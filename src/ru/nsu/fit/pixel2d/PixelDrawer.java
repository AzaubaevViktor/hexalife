package ru.nsu.fit.pixel2d;

import ru.nsu.fit.pixel2d.vectors.BasicLine;
import ru.nsu.fit.pixel2d.vectors.Vec2d;
import ru.nsu.fit.pixel2d.vectors.Vec2dI;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PixelDrawer {

    private <T> T getItself(T itself, T dummy) {
        return itself;
    }

    private List<Vec2dI> pixelsLine1(Vec2dI _p0, Vec2dI _p1) {
        Vec2dI p0 = _p0.copy();
        Vec2dI p1 = _p1.copy();

        Vec2dI dp = p1.minus(p0);
        boolean steep = Math.abs(dp.getY()) > Math.abs(dp.getX());

        if (steep) {
            p0.reflect();
            p1.reflect();
        }

        if (p0.isXmore(p1)) {
            p0.swap(p1);
        }

        dp = p1.minus(p0);

        int error = (dp.getX() / 2);

        int yStep = (dp.getY() > 0) ? 1 : -1;

        int dx = dp.getX();
        int dy = dp.getY();

        int y = p0.getY();
        ArrayList<Vec2dI> pixels = new ArrayList<Vec2dI>();

        for (int x = p0.getX(); x <= p1.getX(); x++) {
            int _x = steep ? y : x;
            int _y = steep ? x : y;

            pixels.add(new Vec2dI(_x, _y));
            error -= Math.abs(dy);
            if (error < 0) {
                y += yStep;
                error += dx;
            }
        }

        return pixels;
    }

    private List<BasicLine> linesTriangle(Vec2dI p0, Vec2dI p1, Vec2dI p2) {
        List<Vec2dI> pixels = new ArrayList<Vec2dI>();
        List<BasicLine> lines = new ArrayList<BasicLine>();
        // Смотрим пиксели границ

        if (p1.dy(p0) == 0 && p2.dy(p1) == 0) {
            lines.add(new BasicLine(p0, p1));
            lines.add(new BasicLine(p1, p2));
            lines.add(new BasicLine(p0, p2));
            return lines;
        }

        pixels.addAll(pixelsLine1(p0, p1));
        pixels.addAll(pixelsLine1(p1, p2));
        pixels.addAll(pixelsLine1(p2, p0));

        Map<Integer, Integer> minXs = new HashMap<Integer, Integer>();
        Map<Integer, Integer> maxXs = new HashMap<Integer, Integer>();

        for (Vec2dI pixel: pixels) {
            int x = pixel.getX();
            int y = pixel.getY();
            if (!minXs.containsKey(y)) {
                minXs.put(y, x);
                maxXs.put(y, x);
                continue;
            }
            if (minXs.get(y) > x) {
                minXs.put(y, x);
            }
            if (maxXs.get(y) < x) {
                maxXs.put(y, x);
            }
        }

        for (Integer y: minXs.keySet()) {
            int minX = minXs.get(y);
            int maxX = maxXs.get(y);
            lines.add(new BasicLine(minX, y, maxX, y));
        }

        return lines;
    }

    private List<BasicLine> linesConvexArea(List<Vec2dI> dots) {
        // Как понятно из названия, только ВЫПУКЛЫЕ области. с невыпуклыми будет работать некорректно
        List<BasicLine> lines = new ArrayList<BasicLine>();

        // Точка
        if (dots.size() == 1) {
            lines.add(new BasicLine(dots.get(0)));
        }
        // Линия
        else if (dots.size() == 2) lines.add(new BasicLine(dots.get(0), dots.get(1)));
        // Один и больше тругольников
        else if (dots.size() >= 3) {
            Vec2dI p0 = dots.get(0);
            for (int i = 1; i < dots.size() - 1; i++) {
                lines.addAll(linesTriangle(p0, dots.get(i), dots.get(i + 1)));
            }
        }

        return lines;
    }

    private List<BasicLine> linesFillHexagonal(Vec2dI center, int radius) {
        List<Vec2dI> dots = new ArrayList<Vec2dI>();

        double sin60 = Math.sqrt(3)/2.;
        Vec2d[] hexMatrix = {
                new Vec2d(radius, 0),
                new Vec2d(radius / 2., radius * sin60),
                new Vec2d(-radius / 2., radius * sin60),
                new Vec2d(-radius, 0),
                new Vec2d(-radius / 2., -radius * sin60),
                new Vec2d(radius / 2., -radius * sin60)
        };

        for (int i = 0; i < 6; i++) {
            Vec2d dot = new Vec2d(center);
            dot.move(hexMatrix[i]);
            dots.add(new Vec2dI(dot));
        }

        return linesConvexArea(dots);
    }

    private List<BasicLine> pixelsLine(Vec2dI p0, Vec2dI p1, int thickness) {
        List<Vec2dI> dots = new ArrayList<Vec2dI>();

        if (thickness == 1) {
            dots.add(p0);
            dots.add(p1);
        } else {

            Vec2dI dp = p1.minus(p0);
            Vec2d normal = new Vec2d(dp).normal();
            normal.normalization(); // Имеем 1ный вектор, перпендикулярный прямой
            Vec2d dot = new Vec2d(p0);

            // первая точка
            dot.move(normal.multiple(thickness / 2.));
            dots.add(new Vec2dI(dot));
            // вторая точка
            dot.move(dp);
            dots.add(new Vec2dI(dot));
            // Третья
            normal.turnBack();
            dot.move(normal.multiple(thickness));
            dots.add(new Vec2dI(dot));
            // Четвертая
            dp.turnBack();
            dot.move(dp);
            dots.add(new Vec2dI(dot));
        }
        return linesConvexArea(dots);
    }

    private void drawBasicLines(Graphics g, List<BasicLine> lines, Color color) {
        g.setColor(color);
        for (BasicLine line: lines) {
            if (line.isHorisontal()) {
                int x0 = line.getX0();
                int x1 = line.getX1();
                int y = line.getY0();
                for (int x = x0; x <= x1; x++) {
                    g.drawLine(x, y, x, y);
                }
            } else {
                for (Vec2dI pixel: pixelsLine1(line.getStart(), line.getEnd())) {
                    int x = pixel.getX();
                    int y = pixel.getY();
                    g.drawLine(x, y, x, y);
                }
            }
        }
    }

    public void drawLine1(Graphics g, Vec2dI p0, Vec2dI p1, Color color) {
        ArrayList<BasicLine> lines = new ArrayList<BasicLine>();
        lines.add(new BasicLine(p0, p1));
        drawBasicLines(g, lines, color);
    }

    public void drawFillTriangle(Graphics g, Vec2dI p0, Vec2dI p1, Vec2dI p2, Color color) {
        drawBasicLines(g, linesTriangle(p0, p1, p2), color);
    }

    public void drawConvexArea(Graphics g, List<Vec2dI> dots, Color color) {
        drawBasicLines(g, linesConvexArea(dots), color);
    }

    public void drawFillHexagonal(Graphics g, Vec2dI center, int radius, Color color) {
        drawBasicLines(g, linesFillHexagonal(center, radius), color);
    }

    public void drawLine(Graphics g, Vec2dI p0, Vec2dI p1, int thickness, Color color) {
        drawBasicLines(g, pixelsLine(p0, p1, thickness), color);
    }
}
