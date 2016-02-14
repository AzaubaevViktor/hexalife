package ru.nsu.fit.pixel2d;

import ru.nsu.fit.pixel2d.vectors.Vec2dI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PixelDrawer {

    private <T> T getItself(T itself, T dummy) {
        return itself;
    }

    private List<Vec2dI> pixelsLine1(Vec2dI p0, Vec2dI p1) {
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

        int error = dp.getX() / 2;

        int ystep = (dp.getY() > 0) ? 1 : -1;

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
                y += ystep;
                error += dx;
            }
        }
        return pixels;
    }

    public void drawLine1(Graphics g, Vec2dI p0, Vec2dI p1, Color color) {
        drawPixels(g, pixelsLine1(p0, p1), color);
    }

    private List<Vec2dI> pixelsTriangle(Vec2dI p0, Vec2dI p1, Vec2dI  p2) {
        List<Vec2dI> pixels = new ArrayList<Vec2dI>();
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

        int totalHeight = p2.dy(p0);

        for (int i = 0; i < totalHeight; i++) {
            boolean isSecondHalf = i > p1.dy(p0) || p1.dy(p0) == 0;
            int segmentHeight = isSecondHalf ? p2.dy(p1) : p1.dy(p0);
            double alpha = (float) i / totalHeight; // totalHeight ненулевое, проверили
            double beta = (float) (i - (isSecondHalf ? p1.dy(p0) : 0)) / segmentHeight; // Деления на ноль нет
            int x1 = (int) (p0.getX() + p2.dx(p0) * alpha);
            int x2 = (int) (isSecondHalf ? p1.getX() + p2.dx(p1) * beta : p0.getX() + p1.dx(p0) * beta);
            if (x1 > x2) x1 = getItself(x2, x2 = x1); // swap
            for (int x = x1; x <= x2; x++) {
                pixels.add(new Vec2dI(x, p0.getY() + i));
            }
        }
        return pixels;
    }

    public void fillTriangle(Graphics g, Vec2dI p0, Vec2dI p1, Vec2dI p2, Color color) {
        drawPixels(g, pixelsTriangle(p0, p1, p2), color);
    }

    private void drawPixels(Graphics g, List<Vec2dI> pixels, Color color) {
        g.setColor(color);
        int i = 0;
        for (Vec2dI pixel: pixels) {
            int x = pixel.getX();
            int y = pixel.getY();
            g.setColor(new Color(i % 255, 0, i % 255));
            g.drawLine(x, y, x, y);
            i++;
        }
    }
}
