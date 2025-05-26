package main;
import java.util.List;
import obstacle.Obstacle;

public interface Predator extends SimulationObject {
    void avoidObstacles(List<Obstacle> obstacles); 
}