package main;
/**
 * represents any object in the simulation that can move, interact, and be rendered.
 */
import geometry.CartesianCoordinate;

/**
 * defines the contract for simulation entities.
 * entities are expected to render themselves, respond to updates,
 * and interact via basic collision detection.
 */
public interface SimEntity {

	/**
	 * renders the entity to the canvas.
	 */
	public void draw();

	/**
	 * removes the entity's visual representation from the canvas.
	 */
	public void unDraw(); 

	/**
	 * advances the internal state of the entity by a given time increment.
	 * @param deltaTime the time step in milliseconds
	 */
	public void update(int deltaTime);

	/**
	 * determines whether a collision has occurred with another entity.
	 * @param object the entity to test against
	 * @return true if a collision is detected; false otherwise
	 */
	public boolean collisionCheck(SimEntity object);

	/**
	 * provides the current position of the entity.
	 * @return the entity's location as a cartesian coordinate
	 */
	public CartesianCoordinate getPosition();

	/**
	 * returns the effective radius for collision detection purposes.
	 * @return the collision radius
	 */
	public double getCollisionRadius();
}
