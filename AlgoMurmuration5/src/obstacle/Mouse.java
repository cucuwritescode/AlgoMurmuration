package obstacle;

import java.awt.Color;

import drawing.Canvas;
import geometry.CartesianCoordinate;
import main.SimEntity;

/**
 *A dynamic, invisible obstacle used to track mouse movement in the simulation.
 *This subclass overrides visibility and interaction radius without drawing anything.
 */
public class Mouse extends Obstacle implements SimEntity {

    private final double mouseRadius;

    public Mouse(Canvas canvas, CartesianCoordinate mousePos, double effectiveRadius) {
        //pass dummy size and color (this obstacle won’t be rendered)
        super(canvas, mousePos, 0, 0, new Color(0, 0, 0, 0));
        this.mouseRadius = effectiveRadius;
    }

    @Override
    public double getCollisionRadius() {
        return mouseRadius;
    }

    @Override
    public void draw() {
        //intentionally left blank to make the obstacle invisible
    }

    @Override
    public void unDraw() {
        //no operation required for visual clearing
    }

    @Override
    public String toString() {
        return "MouseObstacle [center=" + getCenter() + ", radius=" + mouseRadius + "]";
    }
}
