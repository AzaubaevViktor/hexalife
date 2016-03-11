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
import java.util.Timer;
import java.util.TimerTask;

class GameLifeFrame extends MainFrame {
    private final ModelSettings modelSettings;
    private final HexagonalPanel hexagonalPanel;
    private final Model model;
    private JFrame about;
    private final JFrame viewSettings;

    private Timer modelTimer;

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
        addMenuItem("Edit/Clear", "Clear field", KeyEvent.VK_E, "clearField");
        addMenuItem("Edit/Model", "Show model viewSettings", KeyEvent.VK_M, "showModelSettings");
        addSubMenu("View", KeyEvent.VK_V);
        addMenuItem("View/Step", "Do step", KeyEvent.VK_E, "doStep");
        addMenuItem("View/Start\\Pause", "StartPause", KeyEvent.VK_E, "startStopModel");
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

    public void startStopModel() {
        if (model.isRun) {
            modelTimer.cancel();
            model.isRun = false;
        } else {
            modelTimer = new Timer();

            modelTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    model.step();
                }
            }, 0, 1000);
            model.isRun = true;
        }
    }

    public void showAbout() { about.setVisible(true); }

    public void clearField() { model.clear(); }

    public void showViewSettings() { viewSettings.setVisible(true); }

    public void showModelSettings() { modelSettings.setVisible(true); }

    public void exit() { System.exit(0); }
}


class viewSettings extends JFrame implements ChangeListener, PropertyChangeListener {

    private final HexagonalPanel hexagonalPanel;
    private final JFormattedTextField hexaWidthRInput;
    private final JSlider hexaWidthRSlider;
    private final JFormattedTextField lineThInput;
    private final JSlider lineThSlider;

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
        JCheckBox xorModeCheckBox = new JCheckBox("Xor mode", false);
        xorModeCheckBox.setName("xorCheckBox");
        JCheckBox displayImpactCheckBox = new JCheckBox("Display Impacts", false);
        displayImpactCheckBox.setName("displayImpactCheckBox");


        hexaWidthRInput.addPropertyChangeListener(this);
        hexaWidthRSlider.addChangeListener(this);
        lineThInput.addPropertyChangeListener(this);
        lineThSlider.addChangeListener(this);
        xorModeCheckBox.addChangeListener(this);
        displayImpactCheckBox.addChangeListener(this);

        this.add(hexaWidthRLabel);
        this.add(hexaWidthRInput);
        this.add(hexaWidthRSlider);

        this.add(lineThLabel);
        this.add(lineThInput);
        this.add(lineThSlider);

        this.add(xorModeCheckBox);
        this.add(displayImpactCheckBox);
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
                break;
            case "displayImpactCheckBox":
                this.hexagonalPanel.needDrawImpact = ((JCheckBox) e.getSource()).isSelected();
                break;
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

class ModelSettings extends JFrame implements ActionListener, PropertyChangeListener {
    private final Model model;
    private final HexagonalPanel hexagonalPanel;
    private final JFormattedTextField liveBeginInput;
    private final JFormattedTextField birthBeginInput;
    private final JFormattedTextField birthEndInput;
    private final JFormattedTextField liveEndInput;
    private final JLabel errorLabel;
    private final JButton applyButton;
    private JFormattedTextField widthInput;
    private JFormattedTextField heightInput;

    ModelSettings(Model model, HexagonalPanel hexagonalPanel) {
        this.model = model;
        this.hexagonalPanel = hexagonalPanel;

        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLayout(new GridLayout(9, 2));
        this.setSize(400, 400);

        Vec2dI size = model.getSize();
        int width = size.getX();
        int height = size.getY();

        this.add(new JLabel("Параметры поля:"));
        this.add(new JApplet());

        widthInput = addLabelField("Ширина:", width, "widthInput");
        heightInput = addLabelField("Высота:", height, "heightInput");

        this.add(new JLabel("Параметры игры:"));
        this.add(new JApplet());

        double[] params = model.getParams();

        liveBeginInput = addLabelField("Старт жизни:", params[0], "liveBeginInput");
        birthBeginInput = addLabelField("Старт рождения:", params[1], "birthBeginInput ");
        birthEndInput = addLabelField("Конец рождения:", params[2], "birthEndInput ");
        liveEndInput = addLabelField("Конец жизни:", params[3], "liveEndInput ");

        liveBeginInput.addPropertyChangeListener(this);
        birthBeginInput.addPropertyChangeListener(this);
        birthEndInput.addPropertyChangeListener(this);
        liveEndInput.addPropertyChangeListener(this);

        applyButton = new JButton("Apply");

        applyButton.addActionListener(this);

        this.add(applyButton);

        errorLabel = new JLabel("");
        this.add(errorLabel);
    }

    private JFormattedTextField addLabelField(String label, Object value, String name) {
        JLabel jLabel = new JLabel(label);
        JFormattedTextField ftf = new JFormattedTextField(value);
        ftf.setName(name);

        this.add(jLabel);
        this.add(ftf);

        return ftf;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Integer width = (Integer) widthInput.getValue();
        Integer height = (Integer) heightInput.getValue();
        model.changeSize(width, height);
        hexagonalPanel.setGridSize(width, height);


        try {
            model.setParams(getParams());
            errorLabel.setText("");
            applyButton.setEnabled(true);
        } catch (ChangeParamsError changeParamsError) {
            errorLabel.setText(changeParamsError.toString());
            applyButton.setEnabled(false);
        }
    }

    private double[] getParams() {
        return new double[]{
                (Double) liveBeginInput.getValue(),
                (Double) birthBeginInput.getValue(),
                (Double) birthEndInput.getValue(),
                (Double) liveEndInput.getValue()
        };
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        try {
            model.checkParams(getParams());
            errorLabel.setText("");
            applyButton.setEnabled(true);
        } catch (ChangeParamsError changeParamsError) {
            errorLabel.setText(changeParamsError.toString());
            applyButton.setEnabled(false);
        }

    }
}