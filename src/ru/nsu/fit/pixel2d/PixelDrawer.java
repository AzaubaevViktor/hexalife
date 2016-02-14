package ru.nsu.fit.pixel2d;

import com.sun.org.apache.bcel.internal.generic.NEW;
import ru.nsu.fit.pixel2d.vectors.Vec2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PixelDrawer {

    private <T> T getItself(T itself, T dummy) {
        return itself;
    }

    private List<Vec2d> pixelsLine1(Vec2d p0, Vec2d p1) {
        Vec2d dp = p1.minus(p0);
        boolean steep = Math.abs(dp.getY()) > Math.abs(dp.getX());

        if (steep) {
            p0.reflect();
            p1.reflect();
        }

        if (p0.isXmore(p1)) {
            p0.swap(p1);
        }

        dp = p1.minus(p0);

        double error = (dp.getX() / 2);

        int ystep = (dp.getY() > 0) ? 1 : -1;

        List<Vec2d> pixels = new ArrayList<Vec2d>();

        double dx = dp.getX();
        double dy = dp.getY();

        double y = p0.getY();
        for (double x = p0.getX(); x <= p1.getX(); x++) {
            double _x = steep ? y : x;
            double _y = steep ? x : y;

            pixels.add(new Vec2d(_x, _y));
            error -= Math.abs(dy);
            if (error < 0) {
                y += ystep;
                error += dx;
            }
        }
        return pixels;
    }

    public void drawLine1(Graphics g, Vec2d p0, Vec2d p1, Color color) {
        drawPixels(g, pixelsLine1(p0, p1), color);
    }

    private List<Vec2d> pixelsTriangle(Vec2d p0, Vec2d p1, Vec2d p2) {
        List<Vec2d> pixels = new ArrayList<Vec2d>();
        // Если все на одной прямой, рисуем две прямые и не паримся
        if (p1.dy(p0) == 0 && p2.dy(p1) == 0) {
            pixels.addAll(pixelsLine1(p0, p1));
            pixels.addAll(pixelsLine1(p1, p2));
            return pixels;
        }
        // Располагаем точки в порядке возрастания y -- сортировка пузырьком %)
        if (p0.dy(p1) > 0) p0 = getItself(p1, p1 = p0);
        if (p0.dy(p2) > 0) p0 = getItself(p2, p2 = p0);
        if (p1.dy(p2) > 0) p1 = getItself(p2, p2 = p1);

        double totalHeight = p2.dy(p0);

        for (int i = 0; i < totalHeight; i++) {
            boolean isSecondHalf = i > p1.dy(p0) || p1.dy(p0) == 0;
            double segmentHeight = (isSecondHalf ? p2.dy(p1) : p1.dy(p0));
            double alpha = (float) i / totalHeight; // totalHeight ненулевое, проверили
            double beta = (float) (i - (isSecondHalf ? p1.dy(p0) : 0)) / segmentHeight; // Деления на ноль нет
            double x1 = (p0.getX() + p2.dx(p0) * alpha);
            double x2 = (isSecondHalf ? p1.getX() + p2.dx(p1) * beta : p0.getX() + p1.dx(p0) * beta);
            if (x1 > x2) x1 = getItself(x2, x2 = x1); // swap
            for (double x = x1; x <= x2; x++) {
                pixels.add(new Vec2d(x, p0.getY() + i));
            }
        }
        return pixels;
    }

    public void drawFillTriangle(Graphics g, Vec2d p0, Vec2d p1, Vec2d p2, Color color) {
        drawPixels(g, pixelsTriangle(p0, p1, p2), color);
    }

    private List<Vec2d> pixelsConvexArea(List<Vec2d> dots) {
        // Как понятно из названия, только ВЫПУКЛЫЕ области. с невыпуклыми будет работать некорректно
        List<Vec2d> pixels = new ArrayList<Vec2d>();
        if (dots.size() == 1) pixels.addAll(dots);
        else if (dots.size() == 2) pixels.addAll(pixelsLine1(dots.get(0), dots.get(1)));
        else if (dots.size() >= 3) {
            Vec2d p0 = dots.get(0);
            for (int i = 1; i < dots.size() - 1; i++) {
                pixels.addAll(pixelsTriangle(p0, dots.get(i), dots.get(i + 1)));
            }
        }
        return pixels;
    }

    public void drawConvexArea(Graphics g, List<Vec2d> dots, Color color) {
        drawPixels(g, pixelsConvexArea(dots), color);
    }

    private List<Vec2d> pixelsFillHexagonal(Vec2d center, int radius) {
        List<Vec2d> dots = new ArrayList<Vec2d>();

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
            Vec2d dot = center.copy();
            dot.move(hexMatrix[i]);
            dots.add(dot);
        }

        return pixelsConvexArea(dots);
    }

    public void drawFillHexagonal(Graphics g, Vec2d center, int radius, Color color) {
        drawPixels(g, pixelsFillHexagonal(center, radius), color);
    }

    private void drawPixels(Graphics g, List<Vec2d> pixels, Color color) {
        g.setColor(color);
        float i = 0;
        for (Vec2d pixel: pixels) {
            int x = (int) Math.round(pixel.getX());
            int y = (int) Math.round(pixel.getY());
            g.setColor(new Color(
                    (float) Math.abs(Math.cos(x / 100.)),
                    (float) Math.abs(Math.sin(y / 100.)),
                    Math.min(i - (int) i, 1 + (int)i - i) ));
            g.drawLine(x, y, x, y);
            i += 0.01;
        }
    }
}
