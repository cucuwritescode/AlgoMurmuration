package main;

import java.util.List;

public interface Prey extends SimulationObject {
	void fleeFrom(List<Predator> predators);
	void setSpeed(double speed);

}
