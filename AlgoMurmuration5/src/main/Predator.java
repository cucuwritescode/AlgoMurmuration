package main;

import obstacle.Obstacle;
import turtle.Boid;
import java.util.List;

/**
 * the predator interface represents any entity that behaves like a predator in the simulation.
 * A predator can interact with obstacles, hunt prey (boids), and move within the simulation space.
 */
public interface Predator extends SimEntity {

    /**
     * This method defines how a predator should avoid obstacles in the simulation.
     * Predators may need to steer around obstacles while hunting or moving.
     * 
     * @param obstacles List of obstacles that the predator needs to avoid.
     */
    void avoidObstacles(List<Obstacle> obstacles);

    /**
     * This method defines how a predator should hunt for prey (boids).
     * it can be implemented to decide how the predator locates and chases boids in the simulation.
     * 
     * @param prey List of boids that the predator is hunting.
     * @param deltaTime The time step for the simulation update (used to calculate movement speed).
     */
    void hunt(List<Boid> prey, int deltaTime);

    /**
     * this method is responsible for making sure the predator stays within the simulation bounds.
     * It helps wrap the predator's position on a toroidal canvas (i.e., edges wrap around to the other side).
     * 
     * @param width The width of the canvas (for wrapping around horizontally).
     * @param height The height of the canvas (for wrapping around vertically).
     */
    void wrapPosition(int width, int height);

    /**
     * This method allows the speed of the predator to be set. It helps define how fast the predator can move.
     * 
     * @param speed The speed at which the predator moves in the simulation (in pixels per frame).
     */
    void setSpeed(double speed);
}
