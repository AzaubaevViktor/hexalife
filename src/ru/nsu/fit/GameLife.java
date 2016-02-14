package ru.nsu.fit;
import ru.nsu.fit.pixel2d.PixelDrawer;
import ru.nsu.fit.pixel2d.vectors.Vec2d;
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
        f.add(new TestPanel());
        f.pack();
        f.setVisible(true);
    }
}

