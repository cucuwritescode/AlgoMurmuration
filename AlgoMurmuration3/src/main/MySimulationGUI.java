package main;

import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;


import drawing.Canvas;

public class MySimulationGUI {

    private final int WIDTH = 1800;
    private final int HEIGHT = 1000;

    private JPanel controlPanel = new JPanel();
    private JLabel statusLabel = new JLabel();

    private MySimulationEngine engine;
    private Canvas canvas;

    
    private JSlider predatorSpeedSlider;

    public MySimulationGUI(Canvas canvas, MySimulationEngine engine) {
        this.canvas = canvas;
        this.engine = engine;

        configureSliders();
        configureButtons();
        initialiseWindow();
    }

    private void initialiseWindow() {
        JFrame window = new JFrame("Algorithmic Murmuration");

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(WIDTH, HEIGHT);
        window.setLayout(new BorderLayout());

        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(Color.DARK_GRAY);
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        window.add(statusLabel, BorderLayout.NORTH);
        window.add(canvas, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(controlPanel);
        scrollPane.setPreferredSize(new Dimension(300, HEIGHT));
        window.add(scrollPane, BorderLayout.EAST);

        window.setVisible(true);
    }

    private void configureButtons() {
        JPanel boidControlPanel = createTitledPanel("Boid Controls");

        JButton addButton = styledButton("Add Boid");
        addButton.addActionListener(e -> {
            engine.setPopulation(engine.getPopulation() + 1);
            engine.spawnBoid();
        });

        JButton removeButton = styledButton("Remove Boid");
        removeButton.addActionListener(e -> {
            if (engine.getPopulation() > 0) {
                engine.setPopulation(engine.getPopulation() - 1);
                engine.getBoids().get(0).unDraw();
                engine.getBoids().remove(0);
            }
        });

        JButton resetButton = styledButton("Reset All");
        resetButton.addActionListener(e -> engine.resetSimulation());

        JButton logButton = styledButton("Log Params");
        logButton.addActionListener(e -> {
            System.out.println("c=" + engine.getCohesion() +
                    " s=" + engine.getSeparation() +
                    " a=" + engine.getAlignment() +
                    " r=" + engine.getRange());
        });

        boidControlPanel.add(addButton);
        boidControlPanel.add(removeButton);
        boidControlPanel.add(resetButton);
        boidControlPanel.add(logButton);
        controlPanel.add(boidControlPanel);

        JPanel predatorControlPanel = createTitledPanel("Predator Controls");

        JButton addPredatorButton = styledButton("Add Predator");
        addPredatorButton.addActionListener(e -> {
            if (engine.getPredators().size() < 5) {
                engine.addPredator();
            }
        });

        JButton removePredatorButton = styledButton("Remove Predator");
        removePredatorButton.addActionListener(e -> engine.removePredator());

        predatorControlPanel.add(addPredatorButton);
        predatorControlPanel.add(removePredatorButton);
        controlPanel.add(predatorControlPanel);
    }

    private void configureSliders() {
        JPanel sliderPanel = createTitledPanel("Flocking Parameters");

        JSlider cohesionSlider = makeSlider(0, 1000, 250, e -> engine.setCohesion(((JSlider) e.getSource()).getValue() / 1000.0));
        JSlider separationSlider = makeSlider(0, 1000, 250, e -> engine.setSeparation(((JSlider) e.getSource()).getValue() / 1000.0));
        JSlider alignmentSlider = makeSlider(0, 1000, 250, e -> engine.setAlignment(((JSlider) e.getSource()).getValue() / 1000.0));
        JSlider rangeSlider = makeSlider(0, 100, 10, e -> engine.setRange(((JSlider) e.getSource()).getValue() * 10.0));

        sliderPanel.add(labeledSlider(cohesionSlider, "Cohesion"));
        sliderPanel.add(labeledSlider(separationSlider, "Separation"));
        sliderPanel.add(labeledSlider(alignmentSlider, "Alignment"));
        sliderPanel.add(labeledSlider(rangeSlider, "Visual Range"));

        
        predatorSpeedSlider = makeSlider(1, 100, 20, e -> engine.updatePredatorSpeed(((JSlider) e.getSource()).getValue()));

        
        sliderPanel.add(labeledSlider(predatorSpeedSlider, "Predator Speed"));

        controlPanel.add(sliderPanel);
    }

    private JSlider makeSlider(int min, int max, int majorTick, ChangeListener listener) {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, (min + max) / 2);
        slider.setMajorTickSpacing(majorTick);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBackground(Color.LIGHT_GRAY);
        slider.addChangeListener(listener);
        return slider;
    }

    private JPanel labeledSlider(JSlider slider, String name) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);
        JLabel label = new JLabel(name, JLabel.CENTER);
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.NORTH);
        panel.add(slider, BorderLayout.CENTER);
        return panel;
    }

    private JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setBackground(Color.GRAY);
        button.setForeground(Color.WHITE);
        return button;
    }

    private JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.DARK_GRAY);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                title,
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                Color.WHITE));
        return panel;
    }

    public JLabel getStatusMonitor() {
        return statusLabel;
    }
}
