package ru.nsu.fit.pixel2d;

import ru.nsu.fit.pixel2d.vectors.Vec2dI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PixelDrawer {

    private static int getItself(int itself, int dummy) {
        return itself;
    }

    public void drawLine1(Graphics g, Vec2dI p1, Vec2dI p2, Color color) {
        int x0 = p1.getX();
        int y0 = p1.getY();
        int x1 = p2.getX();
        int y1 = p2.getY();
        boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
        if (steep) {
            //noinspection SuspiciousNameCombination
            x0 = getItself(y0, y0 = x0);
            //noinspection SuspiciousNameCombination
            x1 = getItself(y1, y1 = x1);
        }

        if (x0 > x1) {
            x0 = getItself(x1, x1 = x0);
            y0 = getItself(y1, y1 = y0);
        }

        int dx = x1 - x0;
        int dy = y1 - y0;
        double len = Math.sqrt(dx * dx + dy* dy);

        int error = dx / 2;

        int ystep = (y0 < y1) ? 1 : -1;

        List<Vec2dI> pixels = new ArrayList<Vec2dI>();

        int y = y0;
        for (int x = x0; x <= x1; x++) {
            int _x = steep ? y : x;
            int _y = steep ? x : y;

            pixels.add(new Vec2dI(_x, _y));
            error -= Math.abs(dy);
            if (error < 0) {
                y += ystep;
                error += dx;
            }
        }

        drawPixels(g, pixels);

    }

    private void drawPixels(Graphics g, List<Vec2dI> pixels) {
        for (Vec2dI pixel: pixels) {
            int x = pixel.getX();
            int y = pixel.getY();
            g.drawLine(x, y, x, y);
        }
    }
}
