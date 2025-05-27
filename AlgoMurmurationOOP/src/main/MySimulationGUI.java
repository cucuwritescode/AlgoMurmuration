package main;
//TODO


import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import drawing.Canvas;

public class MySimulationGUI {

    private final int WIDTH = 1800;
    private final int HEIGHT = 1000;

    private JPanel controlPanel = new JPanel();
    private JLabel statusLabel = new JLabel();

    private MySimulationEngine engine;
    private Canvas canvas;

    private JSlider boidSpeedSlider;
    private JSlider predatorSpeedSlider;

    public MySimulationGUI(Canvas canvas, MySimulationEngine engine) {
        this.canvas = canvas;
        this.engine = engine;

        configureSliders();
        configureButtons();
        initialiseWindow();
    }

    //sets up main window with canvas and control panel
    private void initialiseWindow() {
        JFrame window = new JFrame("Algorithmic Murmuration");

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(WIDTH, HEIGHT);
        window.setLayout(new BorderLayout());

        //buttons for predator actions
        JButton addPredatorButton = new JButton("Add Predator");
        addPredatorButton.addActionListener(e -> {
            if (engine.getPredators().size() < 5) {
                engine.addPredator();  // Add predator method to your engine
            } else {
                System.out.println("Max predators reached");
            }
        });

        JButton removePredatorButton = new JButton("Remove Predator");
        removePredatorButton.addActionListener(e -> {
            engine.removePredator();  // Remove predator
        });

        // Panel for Predator Speed Slider
        JPanel speedPanel = new JPanel(new GridLayout(1, 2));
        speedPanel.setBackground(Color.DARK_GRAY);
        speedPanel.add(new JLabel("Predator Speed"));
        speedPanel.add(predatorSpeedSlider);

        // Layout configuration for control panel
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(Color.DARK_GRAY);
        controlPanel.add(addPredatorButton);
        controlPanel.add(removePredatorButton);
        controlPanel.add(speedPanel);  // Add speed panel to control panel

        // Set up the status label
        statusLabel.setForeground(Color.RED);

        // Add the canvas and the statusLabel to the window
        window.add(canvas, BorderLayout.CENTER);  // Canvas goes in the center
        window.add(statusLabel, BorderLayout.NORTH);  // Status label goes at the top
        window.add(controlPanel, BorderLayout.EAST);  // Control panel on the right side for better space usage

        window.setVisible(true);
    }

    // Adds buttons to the control panel
    private void configureButtons() {
        JButton addButton = new JButton("Add Boid");
        addButton.addActionListener(e -> {
            engine.setPopulation(engine.getPopulation() + 1);
            engine.spawnBoid();
        });

        JButton removeButton = new JButton("Remove Boid");
        removeButton.addActionListener(e -> {
            if (engine.getPopulation() > 0) {
                engine.setPopulation(engine.getPopulation() - 1);
                engine.getBoids().get(0).unDraw();
                engine.getBoids().remove(0);
            }
        });

        JButton resetButton = new JButton("Reset All");
        resetButton.addActionListener(e -> engine.resetSimulation());

        JButton logButton = new JButton("Log Params");
        logButton.addActionListener(e -> {
            System.out.println("c=" + engine.getCohesion() +
                    " s=" + engine.getSeparation() +
                    " a=" + engine.getAlignment() +
                    " r=" + engine.getRange());
        });

        controlPanel.add(addButton);
        controlPanel.add(removeButton);
        controlPanel.add(resetButton);
        controlPanel.add(logButton);
    }

    // Adds sliders for flocking parameters
    private void configureSliders() {
        JSlider cohesionSlider = new JSlider();
        customizeSlider(cohesionSlider);
        cohesionSlider.addChangeListener(e -> engine.setCohesion((double) cohesionSlider.getValue() / 1000));

        JSlider separationSlider = new JSlider();
        customizeSlider(separationSlider);
        separationSlider.addChangeListener(e -> engine.setSeparation((double) separationSlider.getValue() / 1000));

        JSlider alignmentSlider = new JSlider();
        customizeSlider(alignmentSlider);
        alignmentSlider.addChangeListener(e -> engine.setAlignment((double) alignmentSlider.getValue() / 1000));

        JSlider rangeSlider = new JSlider();
        customizeSlider(rangeSlider);
        rangeSlider.addChangeListener(e -> engine.setRange((double) rangeSlider.getValue() * 10));

        // Boid Speed Slider
        boidSpeedSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 30);
        boidSpeedSlider.setMajorTickSpacing(20);
        boidSpeedSlider.setPaintTicks(true);
        boidSpeedSlider.setPaintLabels(true);
        boidSpeedSlider.addChangeListener(e -> engine.updateBoidSpeed(boidSpeedSlider.getValue()));

        // Predator Speed Slider
        predatorSpeedSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 30);
        predatorSpeedSlider.setMajorTickSpacing(20);
        predatorSpeedSlider.setPaintTicks(true);
        predatorSpeedSlider.setPaintLabels(true);
        predatorSpeedSlider.addChangeListener(e -> engine.updatePredatorSpeed(predatorSpeedSlider.getValue()));

        controlPanel.add(createSliderPanel(cohesionSlider, "Cohesion", 0, 1, 5));
        controlPanel.add(createSliderPanel(separationSlider, "Separation", 0, 1, 5));
        controlPanel.add(createSliderPanel(alignmentSlider, "Alignment", 0, 1, 5));
        controlPanel.add(createSliderPanel(rangeSlider, "Visual Range", 0, 1000, 5));
    }

    // Wraps a slider and its label in a vertical panel
    private JPanel createSliderPanel(JSlider slider, String label, int min, int max, int steps) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.white);

        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        for (int i = 0; i < steps; i++) {
            int raw = (100 / (steps - 1)) * i;
            double value = ((double) max / (steps - 1)) * i + min;
            labels.put(raw, new JLabel(String.format("%.1f", value)));
        }
        slider.setLabelTable(labels);

        panel.add(new JLabel(label));
        panel.add(slider);
        return panel;
    }

    // Customizes slider colors
    private void customizeSlider(JSlider slider) {
        slider.setBackground(Color.white);  // Background color
        slider.setForeground(Color.LIGHT_GRAY);  // Foreground (handle) color
        UIManager.put("Slider.tickColor", Color.WHITE);  // Tick marks color
        UIManager.put("Slider.trackColor", Color.GRAY);  // Track color
        UIManager.put("Slider.thumbColor", Color.WHITE);  // Thumb color
    }

    // --- Getter Methods ---
    public JLabel getStatusMonitor() {
        return statusLabel;
    }

    public JPanel getControlPanel() {
        return controlPanel;
    }

    public int getWindowWidth() {
        return WIDTH;
    }

    public int getWindowHeight() {
        return HEIGHT;
    }
}
