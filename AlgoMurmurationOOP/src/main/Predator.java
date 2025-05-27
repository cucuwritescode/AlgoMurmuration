package main;

import geometry.MyCartesianCoordinate;
import obstacle.Obstacle;
import turtle.MyBoid;
import java.util.List;

public interface Predator extends SimulationObject {
    void avoidObstacles(List<Obstacle> obstacles);
    void hunt(List<MyBoid> prey, int deltaTime);
    void wrapPosition(int width, int height);
    void setSpeed(double speed);
}
