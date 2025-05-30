package obstacle;

import java.awt.Color;

import drawing.Canvas;
import geometry.MyCartesianCoordinate;
import main.SimulationObject;

public class Obstacle implements SimulationObject {

	private Canvas canvas;
	private Color color;
	private MyCartesianCoordinate topLeftCorner;
	private int xLength;
	private int yLength;
	protected double radius;
	private MyCartesianCoordinate center;
	private MyCartesianCoordinate topRightCorner;
	private MyCartesianCoordinate bottomRightCorner;
	private MyCartesianCoordinate bottomLeftCorner;

	public Obstacle(Canvas canvas, MyCartesianCoordinate topLeftCorner, int xLength, int yLength, Color color) {
		this.canvas = canvas;
		this.color = color;
		this.topLeftCorner = topLeftCorner;
		this.xLength = xLength;
		this.yLength = yLength;
		this.radius = Math.hypot(xLength / 2, yLength / 2);
		this.center = new MyCartesianCoordinate(topLeftCorner.getX() + (xLength / 2),
				topLeftCorner.getY() + (yLength / 2));
		topRightCorner = new MyCartesianCoordinate(topLeftCorner.getX() + xLength, topLeftCorner.getY());
		bottomRightCorner = new MyCartesianCoordinate(topLeftCorner.getX() + xLength, topLeftCorner.getY() + yLength);
		bottomLeftCorner = new MyCartesianCoordinate(topLeftCorner.getX(), topLeftCorner.getY() + yLength);
	}

	@Override
	public void draw() {
		canvas.drawLineBetweenPoints(topLeftCorner, topRightCorner, color);
		canvas.drawLineBetweenPoints(topRightCorner, bottomRightCorner, color);
		canvas.drawLineBetweenPoints(bottomRightCorner, bottomLeftCorner, color);
		canvas.drawLineBetweenPoints(bottomLeftCorner, topLeftCorner, color);
	}

	@Override
	public void update(int deltaTime) {
		// do nothing
	}

	@Override
	public boolean collisionCheck(SimulationObject object) {
		MyCartesianCoordinate point = object.getPosition();
		if (point.getX() >= topLeftCorner.getX() && point.getX() <= topRightCorner.getX()
				&& point.getY() >= topLeftCorner.getY() && point.getY() <= bottomLeftCorner.getY()) {
			return true;
		}
		return false;
	}

	@Override
	public void unDraw() {
		canvas.removeMostRecentLine();
		canvas.removeMostRecentLine();
		canvas.removeMostRecentLine();
		canvas.removeMostRecentLine();

	}
	
	public MyCartesianCoordinate getCenter() {
		return center;
	}

	public int getxLength() {
		return xLength;
	}

	public int getyLength() {
		return yLength;
	}

	@Override
	public MyCartesianCoordinate getPosition() {
		return getCenter();
	}

	public String toString() {
		return "Top left corner: " + topLeftCorner + " X length: " + xLength + " Y length: " + yLength;
	}

	public double getRadius() {
		return radius;
	}

	@Override
	public double getCollisionRadius() {
		return getRadius();
	}

}
