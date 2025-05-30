package main;

import java.awt.Color;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import drawing.Canvas;
import geometry.MyCartesianCoordinate;
import obstacle.MyMouseObstacle;
import obstacle.Obstacle;
import tools.Utils;
import turtle.MyBoid;
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
    private double boidSpeed = 30.0; //default boid speed

    private final List<MyBoid> boids = Collections.synchronizedList(new ArrayList<>());
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
        attachMouseHandlers();  // Make sure the mouse handler is attached
    }

    private void setupObstacles() {
        obstacles.add(new Obstacle(canvas, new MyCartesianCoordinate(100, 300), 120, 80, new Color(144, 238, 144)));  // light green
        obstacles.add(new Obstacle(canvas, new MyCartesianCoordinate(350, 200), 80, 150, new Color(144, 238, 144)));
        obstacles.add(new Obstacle(canvas, new MyCartesianCoordinate(550, 100), 150, 120, new Color(144, 238, 144)));
        for (Obstacle o : obstacles) o.draw();
    }

    private void setupBoids() {
        for (int i = 0; i < population; i++) spawnBoid();
        for (MyBoid b : boids) b.turn(Utils.randomInt(-180, 180));
    }
    
 // Method to update boid speed
    public void updateBoidSpeed(int speed) {
        this.boidSpeed = speed;
        for (MyBoid b : boids) {
            b.setSpeed(boidSpeed);  //update speed of each boid
        }
    }

    //method to update predator speed
    public void updatePredatorSpeed(int speed) {
        this.predatorSpeed = speed;
        for (Predator p : predators) {
            p.setSpeed(predatorSpeed); // ✅ no casting or type-checking needed
        }
    }
    private void setupPredators() {
        for (int i = 0; i < predatorCount; i++) {
            int x = Utils.randomInt(0, canvasWidth);
            int y = Utils.randomInt(0, canvasHeight);
            predators.add(new MyHawk(canvas, new MyCartesianCoordinate(x, y), canvasWidth, canvasHeight));
        }
    }

    //method to add a predator to the simulation
    public void addPredator() {
        if (predators.size() < 5) {
            int x = Utils.randomInt(0, canvasWidth);
            int y = Utils.randomInt(0, canvasHeight);
            predators.add(new MyHawk(canvas, new MyCartesianCoordinate(x, y), canvasWidth, canvasHeight));
            System.out.println("Predator added. Total predators: " + predators.size());
        }
    }

    //method to remove a predator from the simulation
    public void removePredator() {
        if (!predators.isEmpty()) {
            Predator predator = predators.remove(predators.size() - 1);
            predator.unDraw();  // ✅ remove its triangle from canvas
            System.out.println("Predator removed. Total predators: " + predators.size());
        } else {
            System.out.println("No predators to remove");
        }
    }

    //spawning a boid at random position
    public void spawnBoid() {
        MyCartesianCoordinate point = new MyCartesianCoordinate(
            Utils.randomInt(0, canvasWidth),
            Utils.randomInt(0, canvasHeight)
        );

        for (Obstacle obs : obstacles) {
            if (point.toroidDistance(obs.getCenter(), canvasWidth, canvasHeight) < obs.getRadius()) {
                point = new MyCartesianCoordinate(0, 0);
            }
        }

        synchronized (boids) {
            boids.add(new MyBoid(canvas, point, canvasWidth, canvasHeight, Color.CYAN));
        }
    }

    private void attachMouseHandlers() {
        //mouse handler for obstacles here
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                MyCartesianCoordinate m = new MyCartesianCoordinate(e.getX(), e.getY());
                synchronized (obstacles) {
                    if (obstacles.size() > 0 && obstacles.get(obstacles.size() - 1) instanceof MyMouseObstacle) {
                        obstacles.remove(obstacles.size() - 1); //remove the previous mouse obstacle
                    }
                    obstacles.add(new MyMouseObstacle(canvas, m, 100));  //add new mouse obstacle
                }
            }
        });

        canvas.addMouseListener(new MouseAdapter() {
            MyCartesianCoordinate start;
            boolean dragging = false;

            //right click to remove obstacle
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && obstacles.size() >= 5) {
                    synchronized (obstacles) {
                        obstacles.get(obstacles.size() - 2).unDraw();
                        obstacles.remove(obstacles.size() - 2);  //remove last obstacle
                        numObstacles--;
                        System.out.println("Obstacle removed. Total obstacles: " + numObstacles);
                    }
                }
            }

            //left click to start dragging to add obstacle
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    dragging = true;
                    start = new MyCartesianCoordinate(e.getX(), e.getY());
                    synchronized (obstacles) {
                        obstacles.remove(obstacles.size() - 1);  //remove current mouse obstacles if any
                    }
                }
            }

            //left click to finish obstacle and add it to the list
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && dragging) {
                    dragging = false;
                    int w = (int) (e.getX() - start.getX());
                    int h = (int) (e.getY() - start.getY());
                    synchronized (obstacles) {
                        obstacles.add(new Obstacle(canvas, start, w, h, Color.RED));  // Add new obstacle
                        obstacles.get(obstacles.size() - 1).draw();  // Draw the obstacle
                        start = new MyCartesianCoordinate(e.getX(), e.getY());
                        obstacles.add(new MyMouseObstacle(canvas, start, 0));  // Add mouse obstacle for visual feedback
                        numObstacles++;
                        System.out.println("Obstacle added. Total obstacles: " + numObstacles);
                    }
                }
            }

            // Mouse entered event - adds temporary mouse obstacle when mouse enters the canvas
            public void mouseEntered(MouseEvent e) {
                MyCartesianCoordinate p = new MyCartesianCoordinate(e.getX(), e.getY());
                synchronized (obstacles) {
                    obstacles.add(new MyMouseObstacle(canvas, p, 50));  // Add new mouse obstacle
                }
            }

            // Mouse exit event - removes temporary mouse obstacle
            public void mouseExited(MouseEvent e) {
                if (!dragging) {
                    synchronized (obstacles) {
                        obstacles.remove(obstacles.size() - 1);  // Remove last obstacle when mouse exits
                    }
                }
            }
        });
    }

    private void render() {
        for (MyBoid b : boids) b.draw();
        for (Obstacle o : obstacles) o.draw();
        for (Predator p : predators) p.draw();
    }

    private void update(int deltaTime) {
        // Temporary list to store boids to remove after iteration
        List<MyBoid> boidsToRemove = new ArrayList<>();

        // Process all boids first
        for (MyBoid b : boids) {
            b.fleeFrom(predators);
            b.wrapPosition(canvasWidth, canvasHeight);
            b.computeSteeringUpdate(boids, obstacles, cohesion, separation, alignment, range);
            b.enforceSpatialExclusion(obstacles);
            b.update(deltaTime);
        }

        // Process all predators (hawks) after boids
        for (Predator predator : predators) {
            predator.hunt(boids, deltaTime);  // just sets target + moves

            for (MyBoid b : boids) {
                if (predator.collisionCheck(b)) {
                    boidsToRemove.add(b);  // ✅ now based on proper proximity
                    System.out.println("Boid eaten by predator!");
                }
            }

            predator.avoidObstacles(obstacles);
            predator.wrapPosition(canvasWidth, canvasHeight);
            predator.update(deltaTime);
        }
        for (MyBoid b : boidsToRemove) {
            boids.remove(b);  // prey removal happens here
        }
    }
    public void resetSimulation() {
        synchronized (boids) {
            for (MyBoid boid : boids) boid.unDraw();
            boids.clear();
            population = 0;
        }
    }

    private void clearScreen() {
        for (MyBoid b : boids) b.unDraw();
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

    // Accessors and mutators
    public List<MyBoid> getBoids() { return boids; }
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
