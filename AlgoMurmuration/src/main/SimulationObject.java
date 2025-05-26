package main;

import geometry.MyCartesianCoordinate;

public interface SimulationObject {
	

	public void draw();
	
	public void unDraw(); 
	
	public void update(int deltaTime);
	
	public boolean collisionCheck(SimulationObject object);
	
	public MyCartesianCoordinate getPosition();

	public double getCollisionRadius();
	
}
