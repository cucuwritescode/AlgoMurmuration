package turtle;

import java.awt.Color;
import java.util.List;

import main.Predator;
import main.Prey;
import drawing.Canvas;
import geometry.CartesianCoordinate;
import main.SimulationObject;
import obstacle.Obstacle;

/**
 * Represents a single autonomous boid in the flocking simulation.
 * Handles flocking behaviour, spatial avoidance, obstacle collisions,
 * and predator evasion using a combination of vector-based steering rules.
 */
public class Boid extends Turtle implements SimulationObject, Prey {

    // Default speed and flocking weights
    public static final double BASE_SPEED = 24.0;
    public static final double PEAK_SPEED = 40.0;
    public static final double COHESION_WEIGHT = 0.015;
    public static final double ALIGNMENT_WEIGHT = 0.11;
    public static final double SEPARATION_WEIGHT = 0.18;
    public static final double SENSING_RADIUS = 100.0;

    private double speed = BASE_SPEED;
    private int canvasWidth;
    private int canvasHeight;
    private final double collisionRadius = 10;
    private int obstacleCooldown = 999;
    private static final int COOLDOWN_DURATION = 8;

    /**
     * Constructs a Boid at a given starting coordinate.
     */
    public Boid(Canvas canvas, CartesianCoordinate start, int canvasWidth, int canvasHeight, Color color) {
        super(canvas, start, color);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    /**
     * Constructs a Boid at a specific (x, y) position.
     */
    public Boid(Canvas canvas, double x, double y, int canvasWidth, int canvasHeight, Color color) {
        this(canvas, new CartesianCoordinate(x, y), canvasWidth, canvasHeight, color);
    }

    /**
     * Updates the boid's internal speed setting.
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Main method to compute this boid's turn adjustment based on neighbors and obstacles.
     */
    public void computeSteeringUpdate(List<Boid> neighbours, List<Obstacle> obstacles,
                                      double cohesion, double separation, double alignment, double radius) {

        if (isInObstacleCooldown()) return;

        boolean underSpatialStress = isObstacleNearby(obstacles);
        double modCohesion = underSpatialStress ? cohesion * 0.25 : cohesion;
        double modAlignment = underSpatialStress ? alignment * 0.2 : alignment;

        double turn = 0;
        turn += computeSeparationForce(neighbours, separation);
        turn += computeCohesionForce(neighbours, modCohesion, radius);
        turn += computeAlignmentForce(neighbours, modAlignment, radius);

        rotate(limitTurnAngle(normalizeAngle(turn), 10));
    }

    /**
     * Returns true if the boid is currently in obstacle avoidance cooldown.
     */
    private boolean isInObstacleCooldown() {
        if (obstacleCooldown < COOLDOWN_DURATION) {
            obstacleCooldown++;
            return true;
        }
        return false;
    }

    /**
     * Checks whether the boid is close enough to any obstacle to trigger stress-based behavior modulation.
     */
    private boolean isObstacleNearby(List<Obstacle> obstacles) {
        return obstacles.stream()
                .anyMatch(ob -> currentPosition.distance(ob.getCenter()) < ob.getCollisionRadius() + 30);
    }

    /**
     * Calculates a force vector away from the closest neighbour to prevent crowding.
     */
    private double computeSeparationForce(List<Boid> flock, double weight) {
        Boid nearest = findNearestNeighbour(flock);
        double escapeAngle = currentPosition.angle(nearest.getCurrentPosition()) + 180 - currentAngle;
        return escapeAngle * weight;
    }

    /**
     * Calculates a force vector toward the center of nearby boids.
     */
    private double computeCohesionForce(List<Boid> flock, double weight, double radius) {
        CartesianCoordinate centre = computeGroupCentroid(flock, radius);
        return (currentPosition.angle(centre) - currentAngle) * weight;
    }

    /**
     * Aligns boid's heading with the average heading of its neighbours.
     */
    private double computeAlignmentForce(List<Boid> flock, double weight, double radius) {
        double avgHeading = computeAverageHeading(flock, radius);
        double diff = normalizeAngle(avgHeading - currentAngle);
        return Math.abs(diff) > 90 ? 0 : diff * weight;
    }

    /**
     * Pushes the boid away from overlapping with any obstacle and realigns its direction.
     */
    public void enforceSpatialExclusion(List<Obstacle> obstacles) {
        for (Obstacle ob : obstacles) {
            double dx = currentPosition.getX() - ob.getCenter().getX();
            double dy = currentPosition.getY() - ob.getCenter().getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            double threshold = ob.getCollisionRadius() + 1;

            if (dist < threshold) {
                double normX = dx / dist;
                double normY = dy / dist;
                double pushOut = threshold - dist;

                currentPosition = new CartesianCoordinate(
                        currentPosition.getX() + normX * pushOut * 0.5,
                        currentPosition.getY() + normY * pushOut * 0.5
                );

                currentAngle = Math.toDegrees(Math.atan2(normY, normX));
                obstacleCooldown = 0;
                return;
            }
        }
    }

    /**
     * Finds the closest boid in the local flock.
     */
    private Boid findNearestNeighbour(List<Boid> flock) {
        Boid nearest = flock.get(0);
        double min = currentPosition.toroidDistance(nearest.currentPosition, canvasWidth, canvasHeight);
        for (Boid b : flock) {
            double dist = currentPosition.toroidDistance(b.currentPosition, canvasWidth, canvasHeight);
            if (dist < min) {
                min = dist;
                nearest = b;
            }
        }
        return nearest;
    }

    /**
     * Computes the centroid of neighbouring boids within a certain range.
     */
    private CartesianCoordinate computeGroupCentroid(List<Boid> flock, double range) {
        int sumX = 0, sumY = 0, count = 0;
        for (Boid b : flock) {
            if (currentPosition.distance(b.getCurrentPosition()) <= range) {
                sumX += b.getPositionX();
                sumY += b.getPositionY();
                count++;
            }
        }
        return count == 0 ? currentPosition : new CartesianCoordinate(sumX / count, sumY / count);
    }

    /**
     * Calculates the average heading of neighbouring boids.
     */
    private double computeAverageHeading(List<Boid> flock, double range) {
        double x = 0, y = 0;
        int count = 0;
        for (Boid b : flock) {
            if (currentPosition.distance(b.getCurrentPosition()) < range) {
                double angle = Math.toRadians(b.getCurrentAngle());
                x += Math.cos(angle);
                y += Math.sin(angle);
                count++;
            }
        }
        return count == 0 ? currentAngle : Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Computes toroidal distance to another boid.
     */
    public double boidDistance(Boid other, int width, int height) {
        return currentPosition.toroidDistance(other.currentPosition, width, height);
    }

    /**
     * Advances the boid's position forward based on current speed.
     */
    public void update(int deltaTime) {
        move((int) (speed * PIXELS_PER_MS * deltaTime));
    }

    /**
     * Rotates the boid and adjusts its speed based on turn severity.
     */
    public void rotate(int angleToTurn) {
        angleToTurn = limitTurnAngle(normalizeAngle(angleToTurn), 20);
        speed = PEAK_SPEED * (1 - Math.abs(angleToTurn) / 45.0);
        currentAngle += angleToTurn;
    }

    /**
     * Normalises any angle to the [-180, 180] range.
     */
    private int normalizeAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return (int) angle;
    }

    /**
     * Constrains the angle to within ±maxTurn.
     */
    private int limitTurnAngle(int angle, int maxTurn) {
        return Math.max(-maxTurn, Math.min(maxTurn, angle));
    }

    /**
     * Draws the boid as a triangle facing the current direction.
     */
    @Override
    public void draw() {
        CartesianCoordinate a = new CartesianCoordinate(
                currentPosition.getX() - 10 * Math.sin(Math.toRadians(70 - currentAngle)),
                currentPosition.getY() + 10 * Math.cos(Math.toRadians(70 - currentAngle)));
        CartesianCoordinate b = new CartesianCoordinate(
                currentPosition.getX() - 10 * Math.sin(Math.toRadians(70 + currentAngle)),
                currentPosition.getY() - 10 * Math.cos(Math.toRadians(70 + currentAngle)));

        canvas.drawLineBetweenPoints(currentPosition, a, color);
        canvas.drawLineBetweenPoints(a, b, color);
        canvas.drawLineBetweenPoints(b, currentPosition, color);
    }

    /**
     * Checks whether this boid collides with another simulation object.
     */
    @Override
    public boolean collisionCheck(SimulationObject object) {
        return currentPosition.toroidDistance(object.getPosition(), canvasWidth, canvasHeight) < collisionRadius;
    }

    /**
     * Returns the boid's current position.
     */
    @Override
    public CartesianCoordinate getPosition() {
        return currentPosition;
    }

    /**
     * Returns the boid's collision radius.
     */
    @Override
    public double getCollisionRadius() {
        return collisionRadius;
    }

    /**
     * Applies predator evasion force based on proximity.
     */
    @Override
    public void fleeFrom(List<Predator> predators) {
        double fleeX = 0, fleeY = 0;
        double fleeRange = 100.0, fleePower = 0.2;

        for (Predator predator : predators) {
            CartesianCoordinate p = predator.getPosition();
            double dist = currentPosition.toroidDistance(p, canvasWidth, canvasHeight);

            if (dist < fleeRange && dist > 0) {
                double dx = currentPosition.getX() - p.getX();
                double dy = currentPosition.getY() - p.getY();

                // Wrap around for toroidal world
                if (Math.abs(dx) > canvasWidth / 2) dx -= Math.signum(dx) * canvasWidth;
                if (Math.abs(dy) > canvasHeight / 2) dy -= Math.signum(dy) * canvasHeight;

                double closeness = fleeRange - dist;
                fleeX += dx * closeness * fleePower;
                fleeY += dy * closeness * fleePower;
            }
        }

        if (fleeX != 0 || fleeY != 0) {
            double fleeAngle = Math.toDegrees(Math.atan2(fleeY, fleeX));
            int turn = limitTurnAngle(normalizeAngle(fleeAngle - currentAngle), 15);
            rotate(turn);
        }
    }
}


