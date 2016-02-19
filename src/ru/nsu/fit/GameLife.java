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
            panel.setCells(cells);
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
    private int lineThickness = 1;
    private PixelDrawer drawer = new PixelDrawer();

    private List<Vec2dI> cells;

    public HexagonalPanel(int width, int height) {
        this.width = width;
        this.height = height;
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
            hexaWidthR = p.getX() / 10;
            lineThickness = p.getY() / 10;
            if (lineThickness < 1) {
                lineThickness = 1;
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

    private Vec2d getCenterByPlace(int x, int y) {
        // Возвращает координаты по расположению ячейки
        double cos30 = Math.sqrt(3)/2.;
//        lineThickness = 0;
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

    protected void drawHexagonalField(Graphics g) {
        for (int y = 0; y < height; y++) {
            boolean oddLine = y % 2 != 0;

            int i = 0;
            for (int x = 0; x < (width - (oddLine ? 1 : 0)); x++) {
                Color color = Color.blue;
                switch (i % 3) {
                    case 0: color = Color.red;
                        break;
                    case 1: color = Color.green;
                        break;
                    case 2: color = Color.blue;
                        break;
                }

                drawer.drawHexagonal(g, getCenterByPlace(x, y), hexaWidthR + (lineThickness - 1) / 2., lineThickness, color);
                i++;
            }
        }
    }

    protected void drawCells(Graphics g, List<Vec2dI> cells) {
        if (cells == null) return;
        for (Vec2dI cell: cells) {

            drawer.drawFillHexagonal(g, getCenterByPlace(cell.getX(), cell.getY()), hexaWidthR - 1, Color.green);

        }
    }

    public void setCells(List<Vec2dI> cells) {
        this.cells = cells;
    }
}