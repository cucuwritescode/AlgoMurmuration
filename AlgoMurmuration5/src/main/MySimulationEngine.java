package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import drawing.Canvas;
import geometry.CartesianCoordinate;
import obstacle.Obstacle;
import tools.Utils;
import turtle.Boid;
import turtle.MyHawk;

/**
 * Simulation manager controls the main simulation: spawning boids, handling obstacles,
 * and managing predator behavior. This engine updates and renders the scene each frame.
 */
public class MySimulationEngine {

    //simulation parameters
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

    private final List<Boid> boids = Collections.synchronizedList(new ArrayList<>());
    private final List<Obstacle> obstacles = Collections.synchronizedList(new ArrayList<>());
    private final List<Predator> predators = Collections.synchronizedList(new ArrayList<>());

    private final Canvas canvas = new Canvas();
    private final MySimulationGUI gui;
    private MouseEventHandler mouseHandler;
    
    //initialisation of simulation
    public MySimulationEngine() {
        gui = new MySimulationGUI(canvas, this);
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        canvas.setBackground(Color.blue);
        
        setupObstacles();    //setup obstacles for boids to avoid
        setupBoids();        //setup initial boid population
        setupPredators();    //setup initial predator(s)
        //set up MouseEventHandler
        mouseHandler = new MouseEventHandler(canvas, obstacles);
        mouseHandler.attachListeners();  //attach listeners for mouse events
    }

    // ***Setup Methods ***

    /**
     * Setup the obstacles that Boids will avoid.
     * Adds predefined obstacles and draws them.
     */
    private void setupObstacles() {
        obstacles.add(new Obstacle(canvas, new CartesianCoordinate(100, 300), 120, 80, new Color(144, 238, 144)));
        obstacles.add(new Obstacle(canvas, new CartesianCoordinate(350, 200), 80, 150, new Color(144, 238, 144)));
        obstacles.add(new Obstacle(canvas, new CartesianCoordinate(550, 100), 150, 120, new Color(144, 238, 144)));
        
        obstacles.forEach(Obstacle::draw); // Drawing each obstacle
    }

    /**
     *Setup boids by spawning them and making sure they are not inside obstacles.
     */
    private void setupBoids() {
        for (int i = 0; i < population; i++) spawnBoid();
        boids.forEach(b -> b.turn(Utils.generateInt(-180, 180))); // Assign random angles to each boid
    }

    /**
     * Setup predators by creating initial predator(s).
     */
    private void setupPredators() {
        for (int i = 0; i < predatorCount; i++) {
            int x = Utils.generateInt(0, canvasWidth);
            int y = Utils.generateInt(0, canvasHeight);
            predators.add(new MyHawk(canvas, new CartesianCoordinate(x, y), canvasWidth, canvasHeight));
        }
    }

    // *** Predator Methods ***

    /**
     * Updates predator speed by modifying the speed of each predator.
     * @param speed The new predator speed value.
     */
    public void updatePredatorSpeed(int speed) {
        this.predatorSpeed = speed;
        predators.forEach(p -> p.setSpeed(predatorSpeed));
    }

    /**
     * Adds a new predator to the simulation, limited to a max of 5 predators.
     */
    public void addPredator() {
        if (predators.size() < 5) {
            int x = Utils.generateInt(0, canvasWidth);
            int y = Utils.generateInt(0, canvasHeight);
            predators.add(new MyHawk(canvas, new CartesianCoordinate(x, y), canvasWidth, canvasHeight));
            System.out.println("predator added. total predators: " + predators.size());
        }
    }

    /**
     * Removes the last predator from the simulation.
     */
    public void removePredator() {
        if (!predators.isEmpty()) {
            Predator predator = predators.remove(predators.size() - 1);
            predator.unDraw(); // un-draw first
            System.out.println("predator removed. total predators: " + predators.size());
        } else {
            System.out.println("no predators to remove");
        }
    }

    // *** Boid Methods ***

    /**
     * Spawns a new boid at a random location, ensuring it does not overlap with obstacles.
     */
    public void spawnBoid() {
        CartesianCoordinate point = new CartesianCoordinate(
                Utils.generateInt(0, canvasWidth),
                Utils.generateInt(0, canvasHeight)
        );

        // Ensure the boid does not spawn inside an obstacle
        for (Obstacle obs : obstacles) {
            if (point.toroidDistance(obs.getCenter(), canvasWidth, canvasHeight) < obs.getRadius()) {
                point = new CartesianCoordinate(0, 0); // reset position if inside an obstacle
            }
        }

        synchronized (boids) {
            boids.add(new Boid(canvas, point, canvasWidth, canvasHeight, Color.CYAN));
        }
    }

    /**
     * Removes the last boid from the simulation and undraws it.
     */
    public void removeBoid() {
        synchronized (boids) {
            // Check if the list is empty before trying to access an element
            if (!boids.isEmpty()) {
                try {
                    Boid lastBoid = boids.remove(boids.size() - 1);  // Remove the last boid
                    lastBoid.unDraw();  // Safely remove and undraw boid
                    population = Math.max(0, population - 1);  // Decrease population safely
                    System.out.println("Boid removed. Total boids: " + population);
                } catch (Exception e) {
                    System.out.println("Error removing boid: " + e.getMessage());
                }
            } else {
                System.out.println("No boids left to remove.");
            }
        }
    }



    // ***rendering & updating simulation ***

    /**
     * Renders all boids, obstacles, and predators.
     */
    private void render() {
        for (Boid b : boids) b.draw();
        for (Obstacle o : obstacles) o.draw();
        for (Predator p : predators) p.draw();
    }

    /**
     * Updates all boids and predators based on simulation parameters.
     */
    private void update(int deltaTime) {
        List<Boid> boidsToRemove = new ArrayList<>();

        for (Boid b : boids) {
            b.fleeFrom(predators);
            b.wrapPosition(canvasWidth, canvasHeight);
            b.computeSteeringUpdate(boids, obstacles, cohesion, separation, alignment, range);
            b.enforceSpatialExclusion(obstacles);
            b.update(deltaTime);
        }

        //predator updates
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

    // *** Utility Methods ***

    /**
     * Resets the simulation to its initial state, removing all boids, predators, and obstacles.
     */
    public void resetSimulation() {
        synchronized (boids) {
            boids.forEach(Boid::unDraw);
            boids.clear();
            population = 0;
        }

        synchronized (predators) {
            predators.forEach(Predator::unDraw);
            predators.clear();
            predatorCount = 0;
        }

        synchronized (obstacles) {
            while (obstacles.size() > 3) {
                obstacles.get(obstacles.size() - 1).unDraw();
                obstacles.remove(obstacles.size() - 1);
            }
            numObstacles = 3;
        }

        System.out.println("simulation reset to default obstacles.");
    }

    /**
     * Clears all visual elements off the canvas.
     */
    private void clearScreen() {
        for (Boid b : boids) b.unDraw();
        for (Obstacle o : obstacles) o.unDraw();
        for (Predator p : predators) p.unDraw();
    }

    /**
     * Starts the simulation, continuously updating and rendering until stopped.
     */
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

    // *** Accessors & Mutators ***

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
