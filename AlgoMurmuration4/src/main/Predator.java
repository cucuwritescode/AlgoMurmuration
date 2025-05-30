package main;
import obstacle.Obstacle;
import turtle.Boid;
import java.util.List;

public interface Predator extends SimulationObject {
    void avoidObstacles(List<Obstacle> obstacles);
    void hunt(List<Boid> prey, int deltaTime);
    void wrapPosition(int width, int height);
    void setSpeed(double speed);
}
