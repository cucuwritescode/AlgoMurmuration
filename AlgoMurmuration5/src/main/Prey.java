package main;

import java.util.List;

/**
 * the prey interface represents any entity that behaves like prey in the simulation.
 * A prey can interact with predators by fleeing and can also have a set speed for movement.
 */
public interface Prey extends SimEntity {

    /**
     * This method defines how a prey should flee from predators in the simulation.
     * prey should try to avoid predators that are nearby by moving away from them.
     * 
     * @param predators, list of predators that are chasing or near the prey.
     */
    void fleeFrom(List<Predator> predators);

    /**
     * this method allows the speed of the prey to be set.
     * it defines how fast the prey can move in the simulation.
     * 
     * @param speed The speed at which the prey moves (in pixels per frame).
     */
    void setSpeed(double speed);
}
