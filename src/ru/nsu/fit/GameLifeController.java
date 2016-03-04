package ru.nsu.fit;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class GameLifeFrame extends MainFrame {
    private final HexagonalPanel hexagonalPanel;
    Model model;
    private JFrame about;
    private JFrame settings;

    GameLifeFrame(int x, int y, String title, Model model, HexagonalPanel hexagonalPanel) {
        super(x, y, title);
        this.model = model;
        this.hexagonalPanel = hexagonalPanel;
        try {
            createAllMenus();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        createAboutFrame();
        settings = new Settings(model, hexagonalPanel);
    }

    private void createAllMenus() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addSubMenu("File/New", KeyEvent.VK_N);
        addSubMenu("File/Open", KeyEvent.VK_O);
        addSubMenu("File/Save", KeyEvent.VK_S);
        addSubMenu("Edit", KeyEvent.VK_E);
        addSubMenu("Edit/XOR\\Replace", KeyEvent.VK_E);
        addSubMenu("Edit/Clear", KeyEvent.VK_E);
        addMenuItem("Edit/Settings", "Show settings window", KeyEvent.VK_E, "showSettings");
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

    public void showSettings() {
        settings.setVisible(true);
    }
}


class Settings extends JFrame implements ActionListener, ChangeListener, PropertyChangeListener {

    private final Model model;
    private final HexagonalPanel hexagonalPanel;
    private final JFormattedTextField hexaWidthRInput;
    private final JSlider hexaWidthRSlider;

    Settings(Model model, HexagonalPanel hexagonalPanel) {
        this.model = model;
        this.hexagonalPanel = hexagonalPanel;
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLayout(new GridLayout(2, 5));
        this.setSize(400, 200);

        double[] params = model.getParams();

        JLabel hexaWidthRLabel = new JLabel("Размер шестиугольников");
        hexaWidthRInput = new JFormattedTextField(20);
        hexaWidthRInput.setName("hexaWidthRInput");
        hexaWidthRSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 20);
        hexaWidthRSlider.setName("hexaWidthRSlider");
        final JFormattedTextField hexaWidthRInput2 = new JFormattedTextField(6);

        hexaWidthRInput.addPropertyChangeListener(this);
        hexaWidthRSlider.addChangeListener(this);

        this.add(hexaWidthRLabel);
        this.add(hexaWidthRInput);
        this.add(hexaWidthRSlider);
        this.add(hexaWidthRInput2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        switch(source.getName()) {
            case "hexaWidthRSlider":
                hexaWidthRInput.setValue(source.getValue());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        switch (evt.getPropertyName()) {
            case "value":
                System.out.println(evt.toString());
                JFormattedTextField source = (JFormattedTextField) evt.getSource();
                switch (source.getName()) {
                    case "hexaWidthRInput":
                        hexagonalPanel.setDrawParams((Integer) evt.getNewValue(), 1);
                        hexaWidthRSlider.setValue((Integer) evt.getNewValue());
                        break;
                }
                break;
        }
    }
}