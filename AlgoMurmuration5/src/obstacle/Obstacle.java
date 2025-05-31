package obstacle;

import java.awt.Color;

import drawing.Canvas;
import geometry.CartesianCoordinate;
import main.SimEntity;

/**
 * Represents a rectangular static obstacle in the simulation.
 * Drawn as a quadrilateral and participates in collision detection.
 */
public class Obstacle implements SimEntity {

    private final Canvas canvas;
    private final Color color;
    private final CartesianCoordinate origin;
    private final int width;
    private final int height;
    private final double radius;
    private final CartesianCoordinate center;

    private final CartesianCoordinate topRight;
    private final CartesianCoordinate bottomRight;
    private final CartesianCoordinate bottomLeft;

    /**
     *Creates a rectangular obstacle given top-left coordinate and dimensions.
     */
    public Obstacle(Canvas canvas, CartesianCoordinate origin, int width, int height, Color color) {
        this.canvas = canvas;
        this.color = color;
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.radius = Math.hypot(width / 2.0, height / 2.0);
        this.center = new CartesianCoordinate(origin.getX() + width / 2.0, origin.getY() + height / 2.0);

        this.topRight = new CartesianCoordinate(origin.getX() + width, origin.getY());
        this.bottomRight = new CartesianCoordinate(origin.getX() + width, origin.getY() + height);
        this.bottomLeft = new CartesianCoordinate(origin.getX(), origin.getY() + height);
    }

    /**
     *Draws the rectangle on the canvas using its four sides.
     */
    @Override
    public void draw() {
        canvas.drawLineBetweenPoints(origin, topRight, color);
        canvas.drawLineBetweenPoints(topRight, bottomRight, color);
        canvas.drawLineBetweenPoints(bottomRight, bottomLeft, color);
        canvas.drawLineBetweenPoints(bottomLeft, origin, color);
    }

    /**
     *No state update needed for static obstacles.
     */
    @Override
    public void update(int deltaTime) {
        // do nothing
    }

    /**
     *Checks whether a given object's position lies within the obstacle bounds.
     */
    @Override
    public boolean collisionCheck(SimEntity object) {
        CartesianCoordinate point = object.getPosition();
        return point.getX() >= origin.getX() && point.getX() <= topRight.getX()
            && point.getY() >= origin.getY() && point.getY() <= bottomLeft.getY();
    }

    /**
     *Removes the obstacle's lines from the canvas.
     */
    @Override
    public void unDraw() {
        for (int i = 0; i < 4; i++) {
            canvas.removeMostRecentLine();
        }
    }

    public CartesianCoordinate getCenter() {
        return center;
    }

    public int getxLength() {
        return width;
    }

    public int getyLength() {
        return height;
    }

    @Override
    public CartesianCoordinate getPosition() {
        return getCenter();
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public double getCollisionRadius() {
        return getRadius();
    }

    @Override
    public String toString() {
        return "top-left: " + origin + ", width: " + width + ", height: " + height;
    }
}
