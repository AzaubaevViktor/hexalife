package ru.nsu.fit;


import ru.nsu.fit.pixel2d.vectors.Vec2dI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class GameLifeFrame extends MainFrame {
    private final ModelSettings modelSettings;
    private final HexagonalPanel hexagonalPanel;
    private Model model;
    private JFrame about;
    private JFrame viewSettings;

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
        viewSettings = new viewSettings(this.hexagonalPanel);
        modelSettings = new ModelSettings(model, this.hexagonalPanel);
    }

    private void createAllMenus() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addSubMenu("File/New", KeyEvent.VK_N);
        addSubMenu("File/Open", KeyEvent.VK_O);
        addSubMenu("File/Save", KeyEvent.VK_S);
        addMenuItem("File/Exit", "Exit", KeyEvent.VK_E, "exit");
        addSubMenu("Edit", KeyEvent.VK_E);
        addSubMenu("Edit/Clear", KeyEvent.VK_E);
        addMenuItem("Edit/Model", "Show model viewSettings", KeyEvent.VK_M, "showModelSettings");
        addSubMenu("View", KeyEvent.VK_V);
        addMenuItem("View/Display Impact Values", "On/Off", KeyEvent.VK_E, "changeDrawImpact");
        addMenuItem("View/Step", "Do step", KeyEvent.VK_E, "doStep");
        addSubMenu("View/Start\\Pause", KeyEvent.VK_E);
        addMenuItem("View/Settings", "Show viewSettings window", KeyEvent.VK_E, "showViewSettings");
        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About", "Show About Window", KeyEvent.VK_E, "showAbout");
    }

    private void createAboutFrame() {
        about = new JFrame("About");
        about.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        about.setSize(400, 100);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("Life game prototype |"));
        panel.add(new JLabel("FIT NSU, Korovin 13204 @2016"));
        about.setContentPane(panel);
    }

    public void doStep() { model.step(); }

    public void showAbout() { about.setVisible(true); }

    public void showViewSettings() { viewSettings.setVisible(true); }

    public void showModelSettings() { modelSettings.setVisible(true); }

    public void changeDrawImpact() { this.hexagonalPanel.needDrawImpact = !this.hexagonalPanel.needDrawImpact; }

    public void exit() { System.exit(0); }
}


class viewSettings extends JFrame implements ChangeListener, PropertyChangeListener {

    private final HexagonalPanel hexagonalPanel;
    private final JFormattedTextField hexaWidthRInput;
    private final JSlider hexaWidthRSlider;
    private final JFormattedTextField lineThInput;
    private final JSlider lineThSlider;
    private final JCheckBox xorModeCheckBox;

    viewSettings(HexagonalPanel hexagonalPanel) {
        this.hexagonalPanel = hexagonalPanel;
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLayout(new GridLayout(3, 3));
        this.setSize(400, 200);

        JLabel hexaWidthRLabel = new JLabel("Размер шестиугольников");
        hexaWidthRInput = new JFormattedTextField(20);
        hexaWidthRInput.setName("hexaWidthRInput");
        hexaWidthRSlider = new JSlider(JSlider.HORIZONTAL, 5, 100, 20);
        hexaWidthRSlider.setName("hexaWidthRSlider");

        JLabel lineThLabel = new JLabel("Толщина линии");
        lineThInput = new JFormattedTextField(1);
        lineThInput.setName("lineThInput");
        lineThSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        lineThSlider.setName("lineThSlider");
        xorModeCheckBox = new JCheckBox("Xor mode", false);
        xorModeCheckBox.setName("xorCheckBox");

        hexaWidthRInput.addPropertyChangeListener(this);
        hexaWidthRSlider.addChangeListener(this);
        lineThInput.addPropertyChangeListener(this);
        lineThSlider.addChangeListener(this);
        xorModeCheckBox.addChangeListener(this);

        this.add(hexaWidthRLabel);
        this.add(hexaWidthRInput);
        this.add(hexaWidthRSlider);

        this.add(lineThLabel);
        this.add(lineThInput);
        this.add(lineThSlider);

        this.add(xorModeCheckBox);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JComponent rawSource = (JComponent) e.getSource();
        switch(rawSource.getName()) {
            case "hexaWidthRSlider":
                hexaWidthRInput.setValue(((JSlider) e.getSource()).getValue());
                break;
            case "lineThSlider":
                lineThInput.setValue(((JSlider) e.getSource()).getValue());
                break;
            case "xorCheckBox":
                hexagonalPanel.xorClickMode = ((JCheckBox) e.getSource()).isSelected();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        switch (evt.getPropertyName()) {
            case "value":
                JFormattedTextField source = (JFormattedTextField) evt.getSource();
                switch (source.getName()) {
                    case "hexaWidthRInput":
                        hexagonalPanel.setDrawParams((Integer) evt.getNewValue(), (Integer) lineThInput.getValue());
                        hexaWidthRSlider.setValue((Integer) evt.getNewValue());
                        break;
                    case "lineThInput":
                        hexagonalPanel.setDrawParams((Integer) hexaWidthRInput.getValue(), (Integer) evt.getNewValue());
                        lineThSlider.setValue((Integer) evt.getNewValue());
                        break;
                }
                break;
        }
    }
}

class ModelSettings extends JFrame implements ActionListener {
    private final Model model;
    private final JButton button;
    private final HexagonalPanel hexagonalPanel;
    private JFormattedTextField widthInput;
    private JFormattedTextField heightInput;

    ModelSettings(Model model, HexagonalPanel hexagonalPanel) {
        this.model = model;
        this.hexagonalPanel = hexagonalPanel;

        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLayout(new GridLayout(3, 3));
        this.setSize(400, 100);

        Vec2dI size = model.getSize();
        int width = size.getX();
        int height = size.getY();

        JLabel widthLabel = new JLabel("Ширина:");
        widthInput = new JFormattedTextField(width);
        widthInput.setName("widthInput");

        JLabel heightLabel = new JLabel("Высота:");
        heightInput = new JFormattedTextField(height);
        heightInput.setName("heightInput");

        button = new JButton("Apply");

        button.addActionListener(this);

        this.add(widthLabel);
        this.add(widthInput);

        this.add(heightLabel);
        this.add(heightInput);

        this.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Integer width = (Integer) widthInput.getValue();
        Integer height = (Integer) heightInput.getValue();
        model.changeSize(width, height);
        hexagonalPanel.setGridSize(width, height);
    }
}