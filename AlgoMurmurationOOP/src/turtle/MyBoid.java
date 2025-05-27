package turtle;

import java.awt.Color;
import main.Predator;
import main.Prey;
import java.util.List;

import drawing.Canvas;
import geometry.MyCartesianCoordinate;
import main.SimulationObject;
import obstacle.Obstacle;

//controls a single agent within the simulation
//combines flocking rules to compute direction
//includes proximity-based modulation and obstacle escape
public class MyBoid extends Turtle implements SimulationObject, Prey {

	//flocking defaults
	public static final double BASE_SPEED = 24.0;
	public static final double PEAK_SPEED = 40.0;
	public static final double COHESION_WEIGHT = 0.015;
	public static final double ALIGNMENT_WEIGHT = 0.11;
	public static final double SEPARATION_WEIGHT = 0.18;
	public static final double SENSING_RADIUS = 100.0;

	//state vars
	private double speed = BASE_SPEED;
	private int canvasWidth;
	private int canvasHeight;
	private double collisionRadius = 10;

	//frames to pause normal flocking after obstacle hit
	private int obstacleCooldown = 999;
	private static final int COOLDOWN_DURATION = 8;

	//constructor with coordinate
	public MyBoid(Canvas canvas, MyCartesianCoordinate start, int canvasWidth, int canvasHeight, Color color) {
		super(canvas, start, color);
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
	}

	//constructor with raw x y
	public MyBoid(Canvas canvas, double x, double y, int canvasWidth, int canvasHeight, Color color) {
		super(canvas, new MyCartesianCoordinate(x, y), color);
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
	}
	
	public void setSpeed(double speed) { // Add setSpeed method here to update boid speed
        this.speed = speed;
    }

	//main flocking step for this frame
	public void computeSteeringUpdate(List<MyBoid> neighbours, List<Obstacle> obstacles,
	                                  double cohesion, double separation, double alignment, double radius) {

		if (obstacleCooldown < COOLDOWN_DURATION) {
			obstacleCooldown++;
			return;
		}

		boolean spatialStress = obstacleNearby(obstacles);

		double modCohesion = spatialStress ? cohesion * 0.25 : cohesion;
		double modAlignment = spatialStress ? alignment * 0.2 : alignment;

		double turn = 0;
		turn += computeSeparationForce(neighbours, separation);
		turn += computeCohesionForce(neighbours, modCohesion, radius);
		turn += computeAlignmentForce(neighbours, modAlignment, radius);

		while (turn > 180) turn -= 360;
		while (turn < -180) turn += 360;

		int turnCap = 20;
		if (turn > turnCap) turn = turnCap;
		if (turn < -turnCap) turn = -turnCap;

		rotate((int) turn);
	}

	//checks if obstacle is near enough to affect flocking weights
	private boolean obstacleNearby(List<Obstacle> obstacles) {
		for (Obstacle obstacle : obstacles) {
			double dist = currentPosition.distance(obstacle.getCenter());
			if (dist < obstacle.getCollisionRadius() + 30) return true;
		}
		return false;
	}

	//turns sharply away from the closest neighbour
	private double computeSeparationForce(List<MyBoid> flock, double weight) {
		MyBoid closest = findNearestNeighbour(flock);
		double escapeAngle = (currentPosition.angle(closest.getCurrentPosition()) + 180 - currentAngle);
		return escapeAngle * weight;
	}

	//steers gently toward centre of mass of local group
	private double computeCohesionForce(List<MyBoid> flock, double weight, double radius) {
		MyCartesianCoordinate centre = computeGroupCentroid(flock, radius);
		double angleToCentre = currentPosition.angle(centre) - currentAngle;
		return angleToCentre * weight;
	}

	//aligns heading to average vector of neighbours
	private double computeAlignmentForce(List<MyBoid> flock, double weight, double radius) {
		double meanAngle = computeAverageHeading(flock, radius);
		double diff = meanAngle - currentAngle;
		while (diff > 180) diff -= 360;
		while (diff < -180) diff += 360;
		if (Math.abs(diff) > 90) return 0;
		return diff * weight;
	}

	//physics-based obstacle bounce-back
	public void enforceSpatialExclusion(List<Obstacle> obstacles) {
		for (Obstacle obstacle : obstacles) {
			double dx = currentPosition.getX() - obstacle.getCenter().getX();
			double dy = currentPosition.getY() - obstacle.getCenter().getY();
			double dist = Math.sqrt(dx * dx + dy * dy);
			double requiredDistance = obstacle.getCollisionRadius() + 1;

			if (dist < requiredDistance) {
				double normX = dx / dist;
				double normY = dy / dist;
				double pushOut = requiredDistance - dist;

				currentPosition = new MyCartesianCoordinate(
					currentPosition.getX() + normX * pushOut * 0.5,
					currentPosition.getY() + normY * pushOut * 0.5
				);

				currentAngle = Math.toDegrees(Math.atan2(normY, normX));
				//speed = PEAK_SPEED * 0.5;
				obstacleCooldown = 0;
				return;
			}
		}
	}

	//finds closest flockmate
	private MyBoid findNearestNeighbour(List<MyBoid> flock) {
		MyBoid nearest = flock.get(0);
		double min = currentPosition.toroidDistance(nearest.currentPosition, canvasWidth, canvasHeight);
		for (MyBoid b : flock) {
			double d = currentPosition.toroidDistance(b.currentPosition, canvasWidth, canvasHeight);
			if (d < min) {
				min = d;
				nearest = b;
			}
		}
		return nearest;
	}

	//average position of neighbours
	private MyCartesianCoordinate computeGroupCentroid(List<MyBoid> flock, double range) {
		int sumX = 0;
		int sumY = 0;
		int count = 0;
		for (MyBoid b : flock) {
			if (currentPosition.distance(b.getCurrentPosition()) <= range) {
				count++;
				sumX += b.getPositionX();
				sumY += b.getPositionY();
			}
		}
		if (count == 0) return currentPosition;
		return new MyCartesianCoordinate(sumX / count, sumY / count);
	}

	//average heading vector
	private double computeAverageHeading(List<MyBoid> flock, double range) {
		double x = 0;
		double y = 0;
		int count = 0;
		for (MyBoid b : flock) {
			if (currentPosition.distance(b.getCurrentPosition()) < range) {
				double angle = Math.toRadians(b.getCurrentAngle());
				x += Math.cos(angle);
				y += Math.sin(angle);
				count++;
			}
		}
		if (count == 0) return currentAngle;
		return Math.toDegrees(Math.atan2(y, x));
	}

	//wraps toroidal position
	public double boidDistance(MyBoid other, int width, int height) {
		return currentPosition.toroidDistance(other.currentPosition, width, height);
	}

	//advance forward based on speed
	public void update(int deltaTime) {
		move((int) (speed * PIXELS_PER_MS * deltaTime));
	}

	//turns agent and scales speed based on angle
	public void rotate(int angleToTurn) {
		while (angleToTurn > 180) angleToTurn -= 360;
		while (angleToTurn < -180) angleToTurn += 360;

		int max = 20;
		if (angleToTurn > max) angleToTurn = max;
		if (angleToTurn < -max) angleToTurn = -max;

		speed = PEAK_SPEED * (1 - Math.abs(angleToTurn) / 45.0);
		currentAngle += angleToTurn;
	}

	//draws a triangle representing the agent
	@Override
	public void draw() {
		MyCartesianCoordinate a = new MyCartesianCoordinate(
			currentPosition.getX() - 10 * Math.sin(Math.toRadians(70 - currentAngle)),
			currentPosition.getY() + 10 * Math.cos(Math.toRadians(70 - currentAngle)));
		MyCartesianCoordinate b = new MyCartesianCoordinate(
			currentPosition.getX() - 10 * Math.sin(Math.toRadians(70 + currentAngle)),
			currentPosition.getY() - 10 * Math.cos(Math.toRadians(70 + currentAngle)));

		canvas.drawLineBetweenPoints(currentPosition, a, color);
		canvas.drawLineBetweenPoints(a, b, color);
		canvas.drawLineBetweenPoints(b, currentPosition, color);
	}

	@Override
	public boolean collisionCheck(SimulationObject object) {
		return currentPosition.toroidDistance(object.getPosition(), canvasWidth, canvasHeight) < collisionRadius;
	}

	@Override
	public MyCartesianCoordinate getPosition() {
		return currentPosition;
	}

	@Override
	public double getCollisionRadius() {
		return collisionRadius;
	}
	
	
	@Override
	public void fleeFrom(List<Predator> predators) {
		double fleeForceX = 0;
		double fleeForceY = 0;
		double fleeRange = 100.0;
		double fleePower = 0.2;

		for (Predator predator : predators) {
			MyCartesianCoordinate predatorPos = predator.getPosition();
			double dist = currentPosition.toroidDistance(predatorPos, canvasWidth, canvasHeight);

			if (dist < fleeRange && dist > 0) {
				//compute vector away from predator, scaled by closeness
				double dx = currentPosition.getX() - predatorPos.getX();
				double dy = currentPosition.getY() - predatorPos.getY();

				//toroidal adjustments
				if (Math.abs(dx) > canvasWidth / 2) dx -= Math.signum(dx) * canvasWidth;
				if (Math.abs(dy) > canvasHeight / 2) dy -= Math.signum(dy) * canvasHeight;

				double closeness = fleeRange - dist;
				fleeForceX += dx * closeness * fleePower;
				fleeForceY += dy * closeness * fleePower;
			}
		}

		//add fleeing force into the boid’s direction
		if (fleeForceX != 0 || fleeForceY != 0) {
			double fleeAngle = Math.toDegrees(Math.atan2(fleeForceY, fleeForceX));
			double angleDiff = fleeAngle - currentAngle;

			while (angleDiff > 180) angleDiff -= 360;
			while (angleDiff < -180) angleDiff += 360;

			//blend flee steering with current angle
			int maxTurn = 15;
			if (angleDiff > maxTurn) angleDiff = maxTurn;
			if (angleDiff < -maxTurn) angleDiff = -maxTurn;

			turn((int) angleDiff);
		}
	}


	}





