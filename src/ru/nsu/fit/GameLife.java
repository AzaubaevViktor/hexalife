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

public class GameLife {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        System.out.println("Created GUI on EDT? "+
                SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new HexagonalPanel(10, 20));
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
            repaint();
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(600, 600);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawHexagonalField(g);
        drawCells(g);
        g.setColor(Color.red);
        g.drawString(Integer.toString(hexaWidthR) + " " + Integer.toString(lineThickness), 1, 10);
    }

    private Vec2dI getPixelbyPlace(int x, int y) {
        double cos30 = Math.sqrt(3)/2.;
        int xStep = lineThickness + hexaWidthR * 2 - 1; // Подогнал -1
        double yStep = (int) (2 * hexaWidthR + lineThickness) * cos30; // Подогнал (int)
        double oddDx = (int) (hexaWidthR * 2 + lineThickness) / 2. - 1; // Подогнал (int) и -1
        int padding = lineThickness;
        boolean oddLine = y % 2 != 0;
        return new Vec2dI(
                (int) (Math.round(
                        padding + xStep * x +
                                (lineThickness + hexaWidthR * cos30) +
                                (oddLine ? oddDx : 0)
                )),
                (int) (Math.round(
                        padding + yStep * y +
                                (lineThickness / cos30 + hexaWidthR)
                ))
        );
    }

    protected void drawHexagonalField(Graphics g) {
        for (int y = 0; y < height; y++) {
            boolean oddLine = y % 2 != 0;
            for (int x = 0; x < (width - (oddLine ? 1 : 0)); x++) {

                drawer.drawHexagonal(g, getPixelbyPlace(x, y), hexaWidthR + lineThickness / 2, lineThickness, Color.black);
            }
        }
    }

    protected void drawCells(Graphics g) {
        for (int y = 0; y < height; y++) {
            boolean oddLine = y % 2 != 0;
            for (int x = 0; x < (width - (oddLine ? 1 : 0)); x++) {
                if (Math.random() > 0.8) {

                    drawer.drawFillHexagonal(g, getPixelbyPlace(x, y), hexaWidthR - 1, Color.green);
                }
            }
        }
    }

}