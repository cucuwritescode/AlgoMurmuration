package turtle;

import java.awt.Color;
import drawing.Canvas;
import geometry.CartesianCoordinate;

/**
 * Turtle class simulates a movable agent that draws on a canvas.
 * Movement is directional, bounded, and optionally traced.
 */
public class Turtle {

    protected Canvas canvas;
    protected CartesianCoordinate currentPosition;
    protected double currentAngle;
    protected Color color;
    private boolean penStatus;
    protected static final double PIXELS_PER_MS = 0.01;

    //constructor with default black colour
    public Turtle(Canvas canvas, CartesianCoordinate start) {
        this(canvas, start, Color.BLACK);
    }

    //constructor with custom color
    public Turtle(Canvas canvas, CartesianCoordinate start, Color color) {
        this.canvas = canvas;
        this.color = color;
        this.currentAngle = 0;
        this.currentPosition = new CartesianCoordinate(start.getX(), start.getY());
        draw();
    }

    //disable drawing while moving
    public void putPenUp() {
        penStatus = false;
    }

    //enable drawing while moving
    public void putPenDown() {
        penStatus = true;
    }

    //rotate clockwise by given angle (degrees)
    public void turn(int angle) {
        currentAngle = normalizeAngle(currentAngle + angle);
    }

    //move forward in current heading
    public void move(int distance) {
        CartesianCoordinate displacement = computeDisplacement(distance); //compute displacement first
        CartesianCoordinate target = computeTarget(displacement.getX(), displacement.getY()); //then compute the target
        maybeDraw(currentPosition, target); //draw if necessary
        currentPosition = target; //update position last
    }

    //helper method to compute the new target position based on displacement
    private CartesianCoordinate computeTarget(double dx, double dy) {
        return new CartesianCoordinate(currentPosition.getX() + dx, currentPosition.getY() + dy);
    }

    //helper method to compute displacement (dx, dy) based on angle and distance
    private CartesianCoordinate computeDisplacement(int distance) {
        double dx = distance * Math.cos(Math.toRadians(currentAngle));
        double dy = -distance * Math.sin(Math.toRadians(currentAngle));
        return new CartesianCoordinate(dx, dy);
    }

    //draw the line if pen is down
    private void maybeDraw(CartesianCoordinate from, CartesianCoordinate to) {
        if (penStatus) canvas.drawLineBetweenPoints(from, to);
    }

    //wraps around canvas edges
    public void wrapPosition(int xLimit, int yLimit) {
        double x = currentPosition.getX();
        double y = currentPosition.getY();

        if (x < 0 || x > xLimit) x = (x > 0) ? 0 : xLimit; //condense logic here
        if (y < 0 || y > yLimit) y = (y > 0) ? 0 : yLimit; //same for y axis

        currentPosition = new CartesianCoordinate(x, y);
    }

    //draw triangular agent
    public void draw() {
        CartesianCoordinate cornerA = getTriangleCorner(70 - currentAngle); //get one corner
        CartesianCoordinate cornerB = getTriangleCorner(70 + currentAngle); //get the other corner

        //draw the three edges
        canvas.drawLineBetweenPoints(currentPosition, cornerA, color);
        canvas.drawLineBetweenPoints(cornerB, currentPosition, color);
        canvas.drawLineBetweenPoints(cornerA, cornerB, color);
    }

    //helper method to compute a corner position based on angle
    private CartesianCoordinate getTriangleCorner(double angle) {
        double dx = 10 * Math.sin(Math.toRadians(angle));
        double dy = 10 * Math.cos(Math.toRadians(angle));
        return new CartesianCoordinate(currentPosition.getX() - dx, currentPosition.getY() + dy);
    }

    //remove shape from canvas
    public void unDraw() {
        canvas.removeMostRecentLine();
        canvas.removeMostRecentLine();
        canvas.removeMostRecentLine();
    }

    //position accessors
    public CartesianCoordinate getCurrentPosition() {
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

    //helper method to normalise angle to [-180, 180]
    private int normalizeAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return (int) angle;
    }
}
