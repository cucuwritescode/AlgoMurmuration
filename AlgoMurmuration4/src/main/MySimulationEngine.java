package main;
import java.awt.Color;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import drawing.Canvas;
import geometry.CartesianCoordinate;
import obstacle.MyMouseObstacle;
import obstacle.Obstacle;
import tools.Utils;
import turtle.Boid;
import turtle.MyHawk;

public class MySimulationEngine {

    private int canvasWidth;
    private int canvasHeight;
    private double cohesion = 0.05;
    private double separation = 0.05;
    private double alignment = 0.05;
    private double range = 500;
    private int population = 30;
    private int numObstacles = 3;
    private int predatorCount = 1;
    private double predatorSpeed = 1.0;
    private double boidSpeed = 30.0;

    private final List<Boid> boids = Collections.synchronizedList(new ArrayList<>());
    private final List<Obstacle> obstacles = Collections.synchronizedList(new ArrayList<>());
    private final List<Predator> predators = Collections.synchronizedList(new ArrayList<>());

    private final Canvas canvas = new Canvas();
    private final MySimulationGUI gui;

    public MySimulationEngine() {
        gui = new MySimulationGUI(canvas, this);
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        canvas.setBackground(Color.blue);
        setupObstacles();
        setupBoids();
        setupPredators();
        attachMouseHandlers();
    }

    private void setupObstacles() {
        obstacles.add(new Obstacle(canvas, new CartesianCoordinate(100, 300), 120, 80, new Color(144, 238, 144)));
        obstacles.add(new Obstacle(canvas, new CartesianCoordinate(350, 200), 80, 150, new Color(144, 238, 144)));
        obstacles.add(new Obstacle(canvas, new CartesianCoordinate(550, 100), 150, 120, new Color(144, 238, 144)));
        for (Obstacle o : obstacles) o.draw();
    }

    private void setupBoids() {
        for (int i = 0; i < population; i++) spawnBoid();
        for (Boid b : boids) b.turn(Utils.generateInt(-180, 180));
    }

    public void updateBoidSpeed(int speed) {
        this.boidSpeed = speed;
        for (Boid b : boids) b.setSpeed(boidSpeed);
    }

    public void updatePredatorSpeed(int speed) {
        this.predatorSpeed = speed;
        for (Predator p : predators) p.setSpeed(predatorSpeed);
    }

    private void setupPredators() {
        for (int i = 0; i < predatorCount; i++) {
            int x = Utils.generateInt(0, canvasWidth);
            int y = Utils.generateInt(0, canvasHeight);
            predators.add(new MyHawk(canvas, new CartesianCoordinate(x, y), canvasWidth, canvasHeight));
        }
    }

    public void addPredator() {
        if (predators.size() < 5) {
            int x = Utils.generateInt(0, canvasWidth);
            int y = Utils.generateInt(0, canvasHeight);
            predators.add(new MyHawk(canvas, new CartesianCoordinate(x, y), canvasWidth, canvasHeight));
            System.out.println("predator added. total predators: " + predators.size());
        }
    }

    public void removePredator() {
        if (!predators.isEmpty()) {
            Predator predator = predators.get(predators.size() - 1);
            predator.unDraw(); // un-draw first
            predators.remove(predators.size() - 1); // then remove safely
            System.out.println("predator removed. total predators: " + predators.size());
        } else {
            System.out.println("no predators to remove");
        }
    }

    public void spawnBoid() {
        CartesianCoordinate point = new CartesianCoordinate(
                Utils.generateInt(0, canvasWidth),
                Utils.generateInt(0, canvasHeight)
        );

        for (Obstacle obs : obstacles) {
            if (point.toroidDistance(obs.getCenter(), canvasWidth, canvasHeight) < obs.getRadius()) {
                point = new CartesianCoordinate(0, 0);
            }
        }

        synchronized (boids) {
            boids.add(new Boid(canvas, point, canvasWidth, canvasHeight, Color.CYAN));
        }
    }
    public void removeBoid() {
        synchronized (boids) {
            if (boids.size() > 0) {
                Boid lastBoid = boids.remove(boids.size() - 1);
                if (lastBoid != null) {
                    try {
                        lastBoid.unDraw();
                    } catch (Exception e) {
                        System.out.println("failed to undraw boid: " + e.getMessage());
                    }
                }
                population = Math.max(0, population - 1);
                System.out.println("boid removed. total boids: " + population);
            } else {
                System.out.println("no boids left to remove.");
            }
        }
    }


    private void attachMouseHandlers() {
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                CartesianCoordinate m = new CartesianCoordinate(e.getX(), e.getY());
                synchronized (obstacles) {
                    if (!obstacles.isEmpty() && obstacles.get(obstacles.size() - 1) instanceof MyMouseObstacle) {
                        obstacles.remove(obstacles.size() - 1);
                    }
                    obstacles.add(new MyMouseObstacle(canvas, m, 100));
                }
            }
        });

        canvas.addMouseListener(new MouseAdapter() {
            CartesianCoordinate start;
            boolean dragging = false;

            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && obstacles.size() >= 5) {
                    synchronized (obstacles) {
                        obstacles.get(obstacles.size() - 2).unDraw();
                        obstacles.remove(obstacles.size() - 2);
                        numObstacles--;
                        System.out.println("obstacle removed. total obstacles: " + numObstacles);
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    dragging = true;
                    start = new CartesianCoordinate(e.getX(), e.getY());
                    synchronized (obstacles) {
                        obstacles.remove(obstacles.size() - 1);
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && dragging) {
                    dragging = false;
                    int w = (int) (e.getX() - start.getX());
                    int h = (int) (e.getY() - start.getY());

                    synchronized (obstacles) {
                        obstacles.add(new Obstacle(canvas, start, w, h, Color.RED));
                        obstacles.get(obstacles.size() - 1).draw();
                        start = new CartesianCoordinate(e.getX(), e.getY());
                        obstacles.add(new MyMouseObstacle(canvas, start, 0));
                        numObstacles++;
                        System.out.println("obstacle added. total obstacles: " + numObstacles);
                    }
                }
            }

            public void mouseEntered(MouseEvent e) {
                CartesianCoordinate p = new CartesianCoordinate(e.getX(), e.getY());
                synchronized (obstacles) {
                    obstacles.add(new MyMouseObstacle(canvas, p, 50));
                }
            }

            public void mouseExited(MouseEvent e) {
                if (!dragging) {
                    synchronized (obstacles) {
                        if (!obstacles.isEmpty()) {
                            obstacles.remove(obstacles.size() - 1);
                        }
                    }
                }
            }
        });
    }

    private void render() {
        for (Boid b : boids) b.draw();
        for (Obstacle o : obstacles) o.draw();
        for (Predator p : predators) p.draw();
    }

    private void update(int deltaTime) {
        List<Boid> boidsToRemove = new ArrayList<>();

        for (Boid b : boids) {
            b.fleeFrom(predators);
            b.wrapPosition(canvasWidth, canvasHeight);
            b.computeSteeringUpdate(boids, obstacles, cohesion, separation, alignment, range);
            b.enforceSpatialExclusion(obstacles);
            b.update(deltaTime);
        }

        // use copy of predator list to avoid concurrent modification
        for (Predator predator : new ArrayList<>(predators)) {
            predator.hunt(boids, deltaTime);
            for (Boid b : boids) {
                if (predator.collisionCheck(b)) {
                    boidsToRemove.add(b);
                    System.out.println("boid eaten by predator!");
                }
            }
            predator.avoidObstacles(obstacles);
            predator.wrapPosition(canvasWidth, canvasHeight);
            predator.update(deltaTime);
        }

        boids.removeAll(boidsToRemove);
    }

    public void resetSimulation() {
        synchronized (boids) {
            for (Boid boid : boids) boid.unDraw();
            boids.clear();
            population = 0;
        }

        synchronized (predators) {
            for (Predator predator : predators) predator.unDraw();
            predators.clear();
            predatorCount = 0;
        }

        synchronized (obstacles) {
            // remove all but the first 3 default obstacles
            while (obstacles.size() > 3) {
                obstacles.get(obstacles.size() - 1).unDraw();
                obstacles.remove(obstacles.size() - 1);
            }
            numObstacles = 3;
        }

        System.out.println("simulation reset to default obstacles.");
    }

    private void clearScreen() {
        for (Boid b : boids) b.unDraw();
        for (Obstacle o : obstacles) o.unDraw();
        for (Predator p : predators) p.unDraw();
    }

    public void start(int deltaTime) {
        while (true) {
            synchronized (obstacles) {
                synchronized (boids) {
                    clearScreen();
                    update(deltaTime);
                    render();
                }
            }
            Utils.pause(deltaTime);
            gui.getStatusMonitor().setText("boids: " + population + "  obstacles: " + numObstacles + "  predators: " + predators.size());
        }
    }

    //accessors and mutators
    public List<Boid> getBoids() { return boids; }
    public List<Predator> getPredators() { return predators; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public int getPopulation() { return population; }
    public void setPopulation(int population) { this.population = population; }
    public double getCohesion() { return cohesion; }
    public void setCohesion(double cohesion) { this.cohesion = cohesion; }
    public double getSeparation() { return separation; }
    public void setSeparation(double separation) { this.separation = separation; }
    public double getAlignment() { return alignment; }
    public void setAlignment(double alignment) { this.alignment = alignment; }
    public double getRange() { return range; }
    public void setRange(double range) { this.range = range; }
    public void setCanvasWidth(int canvasWidth) { this.canvasWidth = canvasWidth; }
    public void setCanvasHeight(int canvasHeight) { this.canvasHeight = canvasHeight; }
}