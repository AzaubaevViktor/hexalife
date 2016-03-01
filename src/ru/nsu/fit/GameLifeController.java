package ru.nsu.fit;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

class GameLifeFrame extends MainFrame {
    Model model;
    private JFrame about;

    GameLifeFrame(int x, int y, String title, Model model) {
        super(x, y, title);
        this.model = model;
        try {
            createAllMenus();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        createAboutFrame();
    }

    private void createAllMenus() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addSubMenu("File/New", KeyEvent.VK_N);
        addSubMenu("File/Open", KeyEvent.VK_O);
        addSubMenu("File/Save", KeyEvent.VK_S);
        addSubMenu("Edit", KeyEvent.VK_E);
        addSubMenu("Edit/XOR\\Replace", KeyEvent.VK_E);
        addSubMenu("Edit/Clear", KeyEvent.VK_E);
        addSubMenu("Edit/Model", KeyEvent.VK_E);
        addSubMenu("View", KeyEvent.VK_V);
        addSubMenu("View/Display Impact Values", KeyEvent.VK_E);
        addMenuItem("View/Step", "Do step", KeyEvent.VK_E, "doStep");
        addSubMenu("View/Start\\Pause", KeyEvent.VK_E);
        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About", "Show About Window", KeyEvent.VK_E, "showAbout");
    }

    private void createAboutFrame() {
        about = new JFrame("About");
        about.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        about.setSize(400, 200);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("Life game prototype |"));
        panel.add(new JLabel("FIT NSU, Korovin 13204 @2016"));
        about.setContentPane(panel);
    }

    public void doStep() {
        model.step();
    }

    public void showAbout() {
        about.setVisible(true);
    }
}
