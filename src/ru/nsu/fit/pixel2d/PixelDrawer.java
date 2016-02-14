package ru.nsu.fit.pixel2d;

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

        List<Vec2dI> pixels = new ArrayList<Vec2dI>();

        int dx = dp.getX();
        int dy = dp.getY();

        int y = p0.getY();
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

    private List<Vec2dI> pixelsTriangle(Vec2dI p0, Vec2dI p1, Vec2dI p2) {
        List<Vec2dI> pixels = new ArrayList<Vec2dI>();
        // Прорисовываем границы
        pixels.addAll(pixelsLine1(p0, p1));
        pixels.addAll(pixelsLine1(p1, p2));
        pixels.addAll(pixelsLine1(p2, p0));
        // Если все на одной прямой, не паримся -- всё, что надо уже прорисовано
        if (p1.dy(p0) == 0 && p2.dy(p1) == 0) {
            return pixels;
        }

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
            for (int x = minX + 1; x < maxX; x++) {
                pixels.add(new Vec2dI(x, y));
            }
        }

        return pixels;
    }

    private List<Vec2dI> pixelsConvexArea(List<Vec2dI> dots) {
        // Как понятно из названия, только ВЫПУКЛЫЕ области. с невыпуклыми будет работать некорректно
        List<Vec2dI> pixels = new ArrayList<Vec2dI>();
        if (dots.size() == 1) pixels.addAll(dots);
        else if (dots.size() == 2) pixels.addAll(pixelsLine1(dots.get(0), dots.get(1)));
        else if (dots.size() >= 3) {
            Vec2dI p0 = dots.get(0);
            for (int i = 1; i < dots.size() - 1; i++) {
                pixels.addAll(pixelsTriangle(p0, dots.get(i), dots.get(i + 1)));
            }
        }
        return pixels;
    }

    private List<Vec2dI> pixelsFillHexagonal(Vec2dI center, int radius) {
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

        return pixelsConvexArea(dots);
    }

    private List<Vec2dI> pixelsLine(Vec2dI p0, Vec2dI p1, int thickness) {
        List<Vec2dI> dots = new ArrayList<Vec2dI>();
        if (thickness < 0) {
            return dots;
        }

        if (thickness == 1) {
            dots.add(p0);
            dots.add(p0);
            dots.add(p1);
            return dots;
        }

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

        return pixelsConvexArea(dots);
    }

    private void drawPixels(Graphics g, List<Vec2dI> pixels, Color color) {
        g.setColor(color);
        for (Vec2dI pixel: pixels) {
            int x = pixel.getX();
            int y = pixel.getY();
            g.setColor(color);
            g.drawLine(x, y, x, y);
        }
    }

    public void drawLine1(Graphics g, Vec2dI p0, Vec2dI p1, Color color) {
        drawPixels(g, pixelsLine1(p0, p1), color);
    }

    public void drawFillTriangle(Graphics g, Vec2dI p0, Vec2dI p1, Vec2dI p2, Color color) {
        drawPixels(g, pixelsTriangle(p0, p1, p2), color);
    }

    public void drawConvexArea(Graphics g, List<Vec2dI> dots, Color color) {
        drawPixels(g, pixelsConvexArea(dots), color);
    }

    public void drawFillHexagonal(Graphics g, Vec2dI center, int radius, Color color) {
        drawPixels(g, pixelsFillHexagonal(center, radius), color);
    }

    public void drawLine(Graphics g, Vec2dI p0, Vec2dI p1, int thickness, Color color) {
        drawPixels(g, pixelsLine(p0, p1, thickness), color);
    }
}
