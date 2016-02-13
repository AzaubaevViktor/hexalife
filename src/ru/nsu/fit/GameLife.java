package ru.nsu.fit;
import ru.nsu.fit.pixel2d.PixelDrawer;
import ru.nsu.fit.pixel2d.vectors.Vec2dI;

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
    PixelDrawer drawer = new PixelDrawer();

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


    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawHexAround(g, squareX, squareY);
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
//        for (int i = 0; i < 7; i++) {
//            drawLine(g,
//                    x + (int) matrix[i % 6][0],
//                    y + (int) matrix[i % 6][1],
//                    x + (int) matrix[(i + 1) % 6][0],
//                    y + (int) matrix[(i + 1) % 6][1],
//                    Color.BLACK,
//                    7
//            );
//        }
        drawer.drawLine1(g, new Vec2dI(100, 100), new Vec2dI(x, y), Color.black);
    }
}