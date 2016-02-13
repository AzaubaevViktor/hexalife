package ru.nsu.fit;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
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
        f.add(new MyPanel());
        f.pack();
        f.setVisible(true);
    }
}

class MyPanel extends JPanel {

    private int squareX = 50;
    private int squareY = 50;
    private int hexDiameter = 50;

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                moveSquare(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                moveSquare(e.getX(), e.getY());
            }
        });

    }

    private void moveSquare(int x, int y) {
        if ((squareX != x) || (squareY != y)) {
            repaint();
            squareX = x;
            squareY = y;
            repaint();
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(250, 200);
    }

    public static int getItself(int itself, int dummy)
    {
        return itself;
    }

    private void drawLine(Graphics g, Integer x0, Integer y0, Integer x1, Integer y1, Color color) {
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
        int dy = Math.abs(y1 - y0);
        int error = dx / 2;

        int ystep = (y0 < y1) ? 1 : -1;

        int y = y0;
        for (int x = x0; x <= x1; x++) {
            int _x = steep ? y : x;
            int _y = steep ? x : y;
            g.drawLine(_x, _y, _x, _y);
            error -= dy;
            if (error < 0) {
                y += ystep;
                error += dx;
            }
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawHexAround(g, squareX, squareY);
        Graphics2D g2 = (Graphics2D) g;
    }

    protected void drawHexAround(Graphics g, int x, int y) {
        double sin60 = Math.sqrt(3)/2.;
        double[][] matrix = {
                {hexDiameter , 0},
                {hexDiameter / 2., hexDiameter * sin60},
                {- hexDiameter / 2., hexDiameter * sin60},
                {- hexDiameter, 0},
                {- hexDiameter / 2., - hexDiameter * sin60},
                {hexDiameter / 2., - hexDiameter * sin60}
        };
        for (int i = 0; i < 7; i++) {
            drawLine(g,
                    x + (int) matrix[i % 6][0],
                    y + (int) matrix[i % 6][1],
                    x + (int) matrix[(i + 1) % 6][0],
                    y + (int) matrix[(i + 1) % 6][1],
                    Color.BLACK
            );
        }
    }
}