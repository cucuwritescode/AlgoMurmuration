package main;
/**
 * represents any object in the simulation that can move, interact, and be rendered.
 */

import geometry.CartesianCoordinate;

public interface SimulationObject {
	

	public void draw();
	
	public void unDraw(); 
	
	public void update(int deltaTime);
	
	public boolean collisionCheck(SimulationObject object);
	
	public CartesianCoordinate getPosition();

	public double getCollisionRadius();
	
}
