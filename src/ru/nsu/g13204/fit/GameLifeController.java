package ru.nsu.g13204.fit;


import ru.nsu.g13204.fit.pixel2d.vectors.Vec2dI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Timer;

class GameLifeFrame extends MainFrame {
    private final ModelSettings modelSettings;
    private final HexagonalPanel hexagonalPanel;
    private final Model model;
    private final ErrorWindow errorWindow;
    private JFrame about;
    private final ViewSettings viewSettings;

    private Timer modelTimer;
    private JMenuItem startMenu;
    private Color defaultBackground;

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
        viewSettings = new ViewSettings(this.hexagonalPanel);
        modelSettings = new ModelSettings(model, this.hexagonalPanel);
        errorWindow = new ErrorWindow();
    }

    private void createAllMenus() throws NoSuchMethodException {
        addSubMenu("File", KeyEvent.VK_F);
        addMenuItem("File/New", "Create new field", KeyEvent.VK_N, "clearField");
        addMenuItem("File/Open", "Open Field file", KeyEvent.VK_O, "openFile");
        addMenuItem("File/Save", "Save Field to file", KeyEvent.VK_S, "saveFile");
        addMenuItem("File/Exit", "Exit", KeyEvent.VK_E, "exit");
        addSubMenu("Edit", KeyEvent.VK_E);
        addMenuItem("Edit/Clear", "Clear field", KeyEvent.VK_E, "clearField");
        addMenuItem("Edit/Model", "Show model ViewSettings", KeyEvent.VK_M, "showModelSettings");
        addSubMenu("View", KeyEvent.VK_V);
        addMenuItem("View/Step", "Do step", KeyEvent.VK_E, "doStep");
        addMenuItem("View/Start", "StartPause", KeyEvent.VK_E, "startStopModel");
        addMenuItem("View/Settings", "Show ViewSettings window", KeyEvent.VK_E, "showViewSettings");
        addSubMenu("Help", KeyEvent.VK_H);
        addMenuItem("Help/About", "Show About Window", KeyEvent.VK_E, "showAbout");

        startMenu = (JMenuItem) getMenuElement("View/Start");
        defaultBackground = startMenu.getBackground();
    }

    public void openFile() {
        File file = getOpenFileName("hl", "Open HexaLife file");
        if (file == null)
            return;

        try {
            FileParser fileParser = new FileParser(file);
            fileParser.parse();
            // For model
            model.clear();
            model.checkSize(fileParser.width, fileParser.height);
            model.changeSize(fileParser.width, fileParser.height);
            for (Vec2dI cell: fileParser.cells) {
                model.set(cell, true);
            }
            // For controller view settings
            modelSettings.setParams(fileParser.width, fileParser.height);

            // Draw Params
            hexagonalPanel.setGridSize(fileParser.width, fileParser.height);
            hexagonalPanel.setDrawParams(fileParser.hexaWidthR, fileParser.lineThickness);

            // For controller model settings
            viewSettings.setParams(fileParser.hexaWidthR, fileParser.lineThickness);

        } catch (FileParserError fileParserError) {
            errorWindow.setErrorLabel(fileParserError.toString());
            errorWindow.setVisible(true);
        } catch (ChangeParamsError changeParamsError) {
            errorWindow.setErrorLabel(changeParamsError.toString());
            errorWindow.setVisible(true);
        }
    }

    public void saveFile() {
        File file = getOpenFileName("hl", "Save HexaLife file");
        if (file == null)
            return;

        if (file.exists()) {
            if (file.delete()) {
                errorWindow.setErrorLabel("Невозможно перезаписать файл");
                errorWindow.setVisible(true);
                return;
            }
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            errorWindow.setErrorLabel("Невозможно создать файл");
            errorWindow.setVisible(true);
            return;
        }

        if (!file.canWrite()) {
            errorWindow.setErrorLabel("Невозможно произвести запись в файл");
            errorWindow.setVisible(true);
            return;
        }

        BufferedWriter out;

        try {
            out = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

            Vec2dI modelSize = model.getSize();
            out.write(Integer.toString(modelSize.getX()) + " " + modelSize.getY() + "\n");

            int[] drawParams = hexagonalPanel.getDrawParams();
            out.write(Integer.toString(drawParams[0]) + "\n");
            out.write(Integer.toString(drawParams[1]) + "\n");

            List<Vec2dI> states = model.getStates();
            out.write(Integer.toString(states.size()) + "\n");
            for (Vec2dI state: states) {
                out.write(Integer.toString(state.getX()) + " " + Integer.toString(state.getY()) + "\n");
            }

            out.close();
        } catch (IOException e) {
            file.delete();
            errorWindow.setErrorLabel(e.toString());
            errorWindow.setVisible(true);
        }
    }

    public void exit() { System.exit(0); }

    public void clearField() { model.clear(); }

    public void showModelSettings() { modelSettings.setVisible(true); }

    public void doStep() {
        modelSettings.setEnabled(false, "Нельзя изменять работающую модель");
        model.step();
        modelSettings.setEnabled(true, "");
    }

    public void startStopModel() {
        if (model.isRun) {
            modelTimer.cancel();
            model.isRun = false;
            modelSettings.setEnabled(true, "");
            startMenu.setBackground(defaultBackground);
            startMenu.setText("Start");
        } else {
            modelTimer = new Timer();

            modelTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    model.step();
                }
            }, 0, 100);
            model.isRun = true;
            modelSettings.setEnabled(false, "Нельзя изменять работающую модель");
            startMenu.setBackground(Color.green);
            startMenu.setText("Stop");
        }
    }

    public void showViewSettings() { viewSettings.setVisible(true); }

    public void showAbout() { about.setVisible(true); }

    private void createAboutFrame() {
        about = new JFrame("About");
        about.setDefaultCloseOperation(HIDE_ON_CLOSE);
        about.setSize(400, 100);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("Life game prototype |"));
        panel.add(new JLabel("FIT NSU, Korovin 13204 @2016"));
        about.setContentPane(panel);
    }
}


class ViewSettings extends JFrame implements ChangeListener, PropertyChangeListener {

    private final HexagonalPanel hexagonalPanel;
    private final JFormattedTextField hexaWidthRInput;
    private final JSlider hexaWidthRSlider;
    private final JFormattedTextField lineThInput;
    private final JSlider lineThSlider;

    public ViewSettings(HexagonalPanel hexagonalPanel) {
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

    public void setParams(int hexaWidthR, int lineTh) {
        hexaWidthRInput.setValue(hexaWidthR);
        lineThInput.setValue(lineTh);
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
    private boolean globalEnabled = true;

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

        widthInput.addPropertyChangeListener(this);
        heightInput.addPropertyChangeListener(this);

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

    public void setParams(int width, int height) {
        widthInput.setValue(width);
        heightInput.setValue(height);
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
        try {
            model.changeSize(width, height);
            hexagonalPanel.setGridSize(width, height);
            insideSetEnabled(true, "");
        } catch (ChangeParamsError changeParamsError) {
            insideSetEnabled(false, changeParamsError.toString());
        }

        try {
            model.setParams(getParams());
            insideSetEnabled(true, "");
        } catch (ChangeParamsError changeParamsError) {
            insideSetEnabled(false, changeParamsError.toString());
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
            model.changeSize((int) widthInput.getValue(), (int) heightInput.getValue());
            model.checkParams(getParams())  ;
            insideSetEnabled(true, "");
        } catch (ChangeParamsError changeParamsError) {
            insideSetEnabled(false, changeParamsError.toString());
        }
    }

    private void insideSetEnabled(boolean b, String msg) {
        if (globalEnabled) {
            applyButton.setEnabled(b);
            errorLabel.setText(msg);
        }
    }

    public void setEnabled(boolean b, String msg) {
        globalEnabled = b;
        applyButton.setEnabled(b);
        errorLabel.setText(msg);
    }
}

class ErrorWindow extends JFrame {
    private final JLabel errorLabel;

    ErrorWindow() {
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLayout(new GridLayout(2, 1));
        this.setSize(400, 100);
        this.add(new JLabel("ОШИБКА:", JLabel.CENTER));
        errorLabel = new JLabel("", JLabel.CENTER);
        this.add(errorLabel);
    }

    public void setErrorLabel(String s) {
        errorLabel.setText(s);
    }
}