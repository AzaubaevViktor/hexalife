package ru.nsu.fit;
import ru.nsu.fit.pixel2d.PixelDrawer;
import ru.nsu.fit.pixel2d.vectors.Vec2d;
import ru.nsu.fit.pixel2d.vectors.Vec2dI;

import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameLife {

    private static HexagonalPanel panel;
    private static Model model;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                model = new Model(10, 20);
                createAndShowGUI(10, 20);
                model.randomGenerate();
            }
        });
        int i = 1;
        while (i < 10) {
            i++;
            List<Vec2dI> cells = model.getStates();
            System.out.println("step");
            model.step();
//            panel.setCells(cells);
            panel.repaint();
            try {
                Thread.sleep(2000);
                // any action
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void createAndShowGUI(int width, int height) {
        System.out.println("Created GUI on EDT? "+
                SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new HexagonalPanel(width, height);
        f.add(panel);
//        f.add(new TestPanel());
        f.pack();
        f.setVisible(true);
    }
}


class HexagonalPanel extends JPanel {

    private int height;
    private int width;
    private int hexaWidthR = 20;
    private int lineThickness = 10;
    private PixelDrawer drawer = new PixelDrawer();
    private HexagonalChecker hexCheck = new HexagonalChecker(drawer);

    private List<Vec2dI> cells = new ArrayList<Vec2dI>();

    public HexagonalPanel(int width, int height) {
        this.width = width;
        hexCheck.width = width;
        this.height = height;
        hexCheck.width = width;
        repaint();
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                mouseHandler(e.getButton(), new Vec2dI(e.getX(), e.getY()));
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int b1 = MouseEvent.BUTTON1_DOWN_MASK;
                int b3 = MouseEvent.BUTTON3_DOWN_MASK;
                int button = (e.getModifiersEx() & b1) == b1 ? 1 : (e.getModifiersEx() & b3) == b3 ? 3 : 0;
                mouseHandler(button, new Vec2dI(e.getX(), e.getY()));
            }
        });
    }

    private void mouseHandler(int button, Vec2dI p) {
        if (button == 1) {
//            hexaWidthR = p.getX() / 10;
//            lineThickness = p.getY() / 10;
//            if (lineThickness < 1) {
//                lineThickness = 1;
//            }
            cells.clear();
            Vec2dI coord = hexCheck.getPlaceByClick(p);
            if (coord != null) {
                System.out.println(coord);
                setCell(coord);
            }
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawHexagonalField(g);
        drawCells(g, cells);
        g.setColor(Color.red);
        g.drawString(Integer.toString(hexaWidthR) + " " + Integer.toString(lineThickness), 1, 10);
        repaint();
    }

    protected void drawHexagonalField(Graphics g) {
        for (int y = 0; y < height; y++) {
            boolean oddLine = y % 2 != 0;

            int i = 0;
            for (int x = 0; x < (width - (oddLine ? 1 : 0)); x++) {
                drawer.drawHexagonal(g, hexCheck.getCenterByPlace(x, y), hexaWidthR + (lineThickness - 1) / 2., lineThickness, Color.black);
                i++;
            }
        }
    }

    protected void drawCells(Graphics g, List<Vec2dI> cells) {
        if (cells == null) return;
        for (Vec2dI cell: cells) {

            drawer.drawFillHexagonal(g, hexCheck.getCenterByPlace(cell.getX(), cell.getY()), hexaWidthR - 1, Color.green);

        }
    }

    public void setCell(Vec2dI cell) {
        this.cells.add(cell);
    }
}

class HexagonalChecker {
    private PixelDrawer drawer;
    public int height;
    public int width;
    public int hexaWidthR = 20;
    public int lineThickness = 10;

    public HexagonalChecker(PixelDrawer drawer) {
        this.drawer = drawer;
    }

    public Vec2d getCenterByPlace(int x, int y) {
        // Возвращает координаты по расположению ячейки
        double cos30 = Math.sqrt(3)/2.;
        int lTh = lineThickness - 1; // Ибо координаты
        int xStep = lTh + hexaWidthR * 2;
        double yStep = ((2 * hexaWidthR + lTh) * cos30);
        double oddDx = ((hexaWidthR * 2 + lTh) / 2.);
        int padding = lineThickness;
        boolean oddLine = y % 2 != 0;
        return new Vec2d(
                padding + xStep * x +
                        (lineThickness /2. + hexaWidthR) + // координата первого 6угольника
                        (oddLine ? oddDx : 0)
                ,
                padding + yStep * y +
                        (lineThickness /2. + hexaWidthR / cos30) // аналогично
        );
    }

    public boolean checkClickTriangle(Vec2dI p1, Vec2dI p2, Vec2dI p3, Vec2dI click) {
        int a = (p1.getX() - click.getX()) * (p2.getY() - p1.getY()) - (p2.getX() - p1.getX()) * (p1.getY() - click.getY());
        int b = (p2.getX() - click.getX()) * (p3.getY() - p2.getY()) - (p3.getX() - p2.getX()) * (p2.getY() - click.getY());
        int c = (p3.getX() - click.getX()) * (p1.getY() - p3.getY()) - (p1.getX() - p3.getX()) * (p3.getY() - click.getY());
        return (a >= 0 && b >= 0 && c >= 0) || (a <= 0 && b <= 0 && c <= 0);
    }

    public boolean checkClickHexa(Vec2d center, Vec2dI click) {
        List<Vec2dI> dots = drawer.dotsHexagonal(center, hexaWidthR);
        Vec2dI p0 = dots.remove(0);
        for (int i = 0; i < dots.size() - 1; i++) {
            if (checkClickTriangle(p0, dots.get(i), dots.get(i + 1), click)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkClick(int x, int y, Vec2dI click) {
        Vec2d center = getCenterByPlace(x, y);
        return checkClickHexa(center, click);
    }

    public int getPlaceXByClick(int y, Vec2dI click) {
        double cos30 = Math.sqrt(3)/2.;
        int lTh = lineThickness - 1; // Ибо координаты
        int xStep = lTh + hexaWidthR * 2;
        double yStep = ((2 * hexaWidthR + lTh) * cos30);
        double oddDx = ((hexaWidthR * 2 + lTh) / 2.);
        int padding = lineThickness;
        boolean oddLine = y % 2 != 0;

        double x = (click.getX() - (lineThickness /2. + hexaWidthR) - padding - (oddLine ? oddDx : 0)) / xStep;
        x += 1/2.; // Выравнивающий коэффициент, чтобы сдвинуть целые значения к началу интервала
        if (checkClick((int) x, y, click)) {
            return (int) x;
        } else if (((x - (int) x) >= .5) && (checkClick((int) x + 1, y, click)))  {
            return (int) x + 1;
        }
        return -1;
    }

    public Vec2dI getPlaceByClick(Vec2dI click) {
        // Возвращает номер ячейки по координатам
        double cos30 = Math.sqrt(3)/2.;
        int lTh = lineThickness - 1; // Ибо координаты
        double yStep = ((2 * hexaWidthR + lTh) * cos30);
        int padding = lineThickness;

        double y = (click.getY() - (lineThickness /2. + hexaWidthR / cos30) - padding +
                hexaWidthR / cos30 // выравнивающий коэффициент, чтобы сдвинуть целые значения к началу интервала
        ) / yStep;

        int x = -1;

        x = getPlaceXByClick((int) y, click);
        if (x == -1) {
            x = getPlaceXByClick((int) y + 1, click);
            if (x != -1) {
                return new Vec2dI((int) x, (int) y + 1);
            }
        } else {
            return new Vec2dI((int) x, (int) y);
        }

        return null;
    }
}