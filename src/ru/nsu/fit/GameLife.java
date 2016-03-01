package ru.nsu.fit;
import ru.nsu.fit.pixel2d.PixelDrawer;
import ru.nsu.fit.pixel2d.vectors.Vec2d;
import ru.nsu.fit.pixel2d.vectors.Vec2dI;

import javax.swing.SwingUtilities;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GameLife {
    private static HexagonalPanel hexagonalPanel;
    private static Model model;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI(10, 20);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void createAndShowGUI(int width, int height) throws NoSuchMethodException {
        System.out.println("Created GUI on EDT? "+
                SwingUtilities.isEventDispatchThread());
        // Model
        model = new Model(10, 20);

        MainFrame f = new GameLifeFrame(600, 600, "Swing Paint Demo", model);

        // HexaPanel
        hexagonalPanel = new HexagonalPanel(width, height, model);
        f.add(hexagonalPanel);
//        f.add(new TestPanel());
        f.pack();
        f.setVisible(true);
    }
}


class HexagonalPanel extends JPanel implements Observer {

    private int height;
    private int width;
    private int hexaWidthR = 40;
    private int lineThickness = 10;
    private PixelDrawer drawer = new PixelDrawer();
    private HexagonalChecker hexCheck = new HexagonalChecker(drawer);
    BufferedImage imgResult;
    private Model model;

    private List<Vec2dI> cells = new ArrayList<Vec2dI>();

    public HexagonalPanel(int width, int height, Model model) {
        this.width = width;
        hexCheck.width = width;
        this.height = height;
        hexCheck.width = width;
        hexCheck.hexaWidthR = hexaWidthR;
        hexCheck.lineThickness = lineThickness;

        Vec2dI leftDownHex = new Vec2dI(hexCheck.getCenterByPlace(width, height));
        imgResult = new BufferedImage(leftDownHex.getX(), leftDownHex.getY(), BufferedImage.TYPE_INT_RGB);
        drawer.clearAll(imgResult, new Vec2dI(imgResult.getWidth(), imgResult.getHeight()), Color.white);

        this.model = model;
        model.addObserver(this);

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
            Vec2dI coord = hexCheck.getPlaceByClick(p);
            if (coord != null) {
                setCell(coord);
            }
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(imgResult, 0, 0, this);
        clearField();
        drawHexagonalField();
        drawCells(cells);
        g.setColor(Color.red);
        g.drawString(Integer.toString(hexaWidthR) + " " + Integer.toString(lineThickness), 1, 10);
        repaint();
    }

    protected void drawHexagonalField() {
        for (int y = 0; y < height; y++) {
            boolean oddLine = y % 2 != 0;

            for (int x = 0; x < (width - (oddLine ? 1 : 0)); x++) {
                drawer.drawHexagonal(imgResult, hexCheck.getCenterByPlace(x, y), hexaWidthR + (lineThickness - 1) / 2., lineThickness, Color.black);
            }
        }
    }

    protected void clearField() {
        drawer.clearAll(imgResult, new Vec2dI(imgResult.getWidth(), imgResult.getHeight()), Color.white);
    }

    protected void drawCells(List<Vec2dI> cells) {
        if (cells == null) return;
        for (Vec2dI cell: cells) {

            drawer.drawFillHexagonal(imgResult, hexCheck.getCenterByPlace(cell.getX(), cell.getY()), hexaWidthR - 1, Color.green);
        }
    }

    public void setCell(Vec2dI cell) {
        model.set(cell.getY(), cell.getX(), true);
        if (!cells.contains(cell)) {
            this.cells.add(cell);
        }
        repaint();
    }

    @Override
    public void update(Observable o, Object arg) {
        Model model = (Model) o;
        cells = model.getStates();
        repaint();
    }
}

class HexagonalChecker {
    private PixelDrawer drawer;
    public int height;
    public int width;
    public int hexaWidthR;
    public int lineThickness;

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
        int lTh = lineThickness - 1; // Ибо координаты
        int xStep = lTh + hexaWidthR * 2;
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

        int x = getPlaceXByClick((int) y, click);
        if (x == -1) {
            x = getPlaceXByClick((int) y + 1, click);
            if (x != -1) {
                return new Vec2dI(x, (int) y + 1);
            }
        } else {
            return new Vec2dI(x, (int) y);
        }

        return null;
    }
}