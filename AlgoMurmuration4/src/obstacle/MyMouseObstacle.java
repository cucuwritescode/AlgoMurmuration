package obstacle;

import java.awt.Color;

import drawing.Canvas;
import geometry.CartesianCoordinate;
import main.SimulationObject;

public class MyMouseObstacle extends Obstacle implements SimulationObject {

	// creates an obstacle at a point with size 0
	public MyMouseObstacle(Canvas canvas, CartesianCoordinate position, double radius) {
		super(canvas, position, 0, 0, Color.BLACK);
		this.radius = radius;
	}

	/*
	 * The mouse obstacle is invisible
	 */
	public void draw() {
		// do nothing
	}

	public void unDraw() {
		// do nothing
	}

}
