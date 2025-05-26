package turtle;

import java.awt.Color;

import drawing.Canvas;
import geometry.MyCartesianCoordinate;

public class Turtle {
	protected Canvas canvas;
	protected MyCartesianCoordinate currentPosition;
	protected double currentAngle;
	private boolean penStatus;
	protected Color color;
	protected final static double PIXELS_PER_MS = 0.01;

	public Turtle(Canvas canvas, MyCartesianCoordinate startingPoint) {
		this.canvas = canvas;
		this.currentPosition = new MyCartesianCoordinate(startingPoint.getX(), startingPoint.getY());
		currentAngle = 0;
		draw();
	}

	public Turtle(Canvas canvas, MyCartesianCoordinate startingPoint, Color color) {
		this.canvas = canvas;
		this.currentPosition = new MyCartesianCoordinate(startingPoint.getX(), startingPoint.getY());
		this.color = color;
		currentAngle = 0;
		draw();
	}

	/**
	 * The turtle is moved in its current direction for the given number of pixels.
	 * If the pen is down when the robot moves, a line will be drawn on the floor.
	 * An angle of 0 corresponds to facing 'right' on the canvas, 90 degrees
	 * corresponds to facing 'up' etc.
	 * 
	 * @param distance The number of pixels to move.
	 */
	public void move(int distance) {
		double xDistance = distance * Math.cos(Math.toRadians(currentAngle));
		double yDistance = distance * -Math.sin(Math.toRadians(currentAngle));
		MyCartesianCoordinate newPosition = new MyCartesianCoordinate(currentPosition.getX() + xDistance,
				currentPosition.getY() + yDistance);
		//draw line if pen is down
		if (penStatus == true) {
			canvas.drawLineBetweenPoints(currentPosition, newPosition);
		}
		//update position
		currentPosition = newPosition;
	}

	/**
	 * prevents turtles from moving outside of the window
	 */
	public void wrapPosition(int xLimit, int yLimit) {

		if (currentPosition.getX() < 0 || currentPosition.getX() > xLimit) {
			currentPosition = currentPosition.getX() > 0 ? new MyCartesianCoordinate(0, currentPosition.getY())
					: new MyCartesianCoordinate(xLimit, currentPosition.getY());
		}
		if (currentPosition.getY() < 0 || currentPosition.getY() > yLimit) {
			currentPosition = currentPosition.getY() > 0 ? new MyCartesianCoordinate(currentPosition.getX(), 0)
					: new MyCartesianCoordinate(currentPosition.getX(), yLimit);
		}
	}

	/**
	 * 
	 * Draws an equilateral triangle with the 'top' corner at the turtle's location
	 * 
	 */

	public void draw() {
		MyCartesianCoordinate corner1 = new MyCartesianCoordinate(
				currentPosition.getX() - 10 * Math.sin(Math.toRadians(70 - currentAngle)),
				currentPosition.getY() + 10 * Math.cos(Math.toRadians(70 - currentAngle)));
		MyCartesianCoordinate corner2 = new MyCartesianCoordinate(
				currentPosition.getX() - (10 * Math.sin(Math.toRadians(70 + currentAngle))),
				currentPosition.getY() - (10 * Math.cos(Math.toRadians(70 + currentAngle))));

		canvas.drawLineBetweenPoints(currentPosition, corner1, color);
		canvas.drawLineBetweenPoints(corner1, corner2, color);
		canvas.drawLineBetweenPoints(corner2, currentPosition, color);
	}

	/**
	 * Removes the three lines drawn by the draw() method
	 */
	public void unDraw() {
		canvas.removeMostRecentLine();
		canvas.removeMostRecentLine();
		canvas.removeMostRecentLine();
	}

	/**
	 * Rotates the turtle clockwise by the specified angle in degrees.
	 * 
	 * @param angleToTurn The number of degrees to turn.
	 */
	public void turn(int angleToTurn) {
		//limits angle to +/-180 degrees
		while (angleToTurn > 180) {
			angleToTurn -= 360;
		}
		while (angleToTurn < -180) {
			angleToTurn += 360;
		}
		currentAngle += angleToTurn;
	}

	/**
	 * Moves the pen off the canvas so that the turtle's route isn't drawn for any
	 * subsequent movements.
	 */
	public void putPenUp() {
		penStatus = false;
	}

	/**
	 * Lowers the pen onto the canvas so that the turtle's route is drawn.
	 */
	public void putPenDown() {
		penStatus = true;
	}

	public MyCartesianCoordinate getCurrentPosition() {
		return currentPosition;
	}

	public int getPositionX() {
		return (int) currentPosition.getX();
	}

	public int getPositionY() {
		return (int) currentPosition.getY();
	}

	public double getCurrentAngle() {
		return currentAngle;
	}
}
