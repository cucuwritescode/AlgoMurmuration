package turtle;

import java.awt.Color;

import java.util.List;

import drawing.Canvas;
import geometry.CartesianCoordinate;
import main.Predator;
import main.SimEntity;
import obstacle.Obstacle;
import main.Prey;


public class MyHawk extends Turtle implements Predator {

    private int canvasWidth;
    private int canvasHeight;
    private double speed = 30; //default predator movement speed
    private double maxTurnRate = 5; //maxturning rate (degrees per update)
    private double huntingRadius = 150;  //radius for hunting

    public MyHawk(Canvas canvas, CartesianCoordinate start, int canvasWidth, int canvasHeight) {
        super(canvas, start, Color.ORANGE);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
    }

    //update predator position based on speed and turning
    @Override
    public void update(int deltaTime) {
        move((int) (speed * PIXELS_PER_MS * deltaTime));  //deltaTime here
    }

    //smooth avoidance behaviour for obstacles
    private void steerAroundObstacles(List<Obstacle> obstacles) {
        double avoidRadius = 70;
        double totalTurn = 0;

        for (Obstacle obs : obstacles) {
            double dx = currentPosition.getX() - obs.getCenter().getX();
            double dy = currentPosition.getY() - obs.getCenter().getY();

            if (Math.abs(dx) > canvasWidth / 2) dx -= Math.signum(dx) * canvasWidth;
            if (Math.abs(dy) > canvasHeight / 2) dy -= Math.signum(dy) * canvasHeight;

            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < obs.getCollisionRadius() + avoidRadius) {
                double angleAway = Math.toDegrees(Math.atan2(dy, dx));
                double angleDiff = angleAway - currentAngle;

                while (angleDiff > 180) angleDiff -= 360;
                while (angleDiff < -180) angleDiff += 360;

                double strength = (obs.getCollisionRadius() + avoidRadius - dist) / avoidRadius;
                totalTurn += -angleDiff * strength * 0.2;
            }
        }

        if (totalTurn > 15) totalTurn = 15;
        if (totalTurn < -15) totalTurn = -15;

        if (Math.abs(totalTurn) > 1) turn((int) totalTurn); //adjust turning based on avoidance
    }
    @Override
    public void draw() {
        int size = 20;  // increase from default 10
        CartesianCoordinate a = new CartesianCoordinate(
            currentPosition.getX() - size * Math.sin(Math.toRadians(70 - currentAngle)),
            currentPosition.getY() + size * Math.cos(Math.toRadians(70 - currentAngle)));
        CartesianCoordinate b = new CartesianCoordinate(
            currentPosition.getX() - size * Math.sin(Math.toRadians(70 + currentAngle)),
            currentPosition.getY() - size * Math.cos(Math.toRadians(70 + currentAngle)));

        canvas.drawLineBetweenPoints(currentPosition, a, Color.ORANGE);
        canvas.drawLineBetweenPoints(a, b, Color.ORANGE);
        canvas.drawLineBetweenPoints(b, currentPosition, Color.ORANGE);
    }

    

    //correction if the predator is inside an obstacle's radius
    private void hardPushback(List<Obstacle> obstacles) {
        for (Obstacle obs : obstacles) {
            double dx = currentPosition.getX() - obs.getCenter().getX();
            double dy = currentPosition.getY() - obs.getCenter().getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            double minDist = obs.getCollisionRadius() + getCollisionRadius();

            if (dist < minDist) {
                double nx = dx / dist;
                double ny = dy / dist;
                double push = minDist - dist;

                currentPosition = new CartesianCoordinate(
                    currentPosition.getX() + nx * push,
                    currentPosition.getY() + ny * push
                );

                currentAngle = Math.toDegrees(Math.atan2(ny, nx));
                return;
            }
        }
    }

    @Override
    public void hunt(List<Boid> prey, int deltaTime) {
        double closestDistance = Double.MAX_VALUE;
        Prey closestPrey = null;

        for (Prey p : prey) {
            double distance = currentPosition.toroidDistance(p.getPosition(), canvasWidth, canvasHeight);
            if (distance < huntingRadius && distance < closestDistance) {
                closestDistance = distance;
                closestPrey = p;
            }
        }

        if (closestPrey != null) {
            smoothMoveToTarget(closestPrey, deltaTime);      //move toward it
        } else {
        }
    }

    //method to smoothly move the predator toward its target (boid)
    public void smoothMoveToTarget(Prey target, int deltaTime) {
        //calculate the angle to the target (boid)
        double angleToTarget = currentPosition.angle(target.getPosition());
        double angleDifference = angleToTarget - currentAngle;

        //normalise angle difference
        while (angleDifference > 180) angleDifference -= 360;
        while (angleDifference < -180) angleDifference += 360;

        //gradual turning for smoother movement
        double turnSpeed = 5; //adjust for more or less smoothing
        if (Math.abs(angleDifference) > turnSpeed) {
            angleDifference = turnSpeed * Math.signum(angleDifference);
        }

        //apply the smooth turn and move towards the target
        turn((int) angleDifference);
        move((int)(speed * PIXELS_PER_MS * deltaTime));  //move with distance based on speed and time
    }

    // --- Interface Implementations ---
    @Override
    public CartesianCoordinate getPosition() {
        return currentPosition;
    }

    @Override
    public double getCollisionRadius() {
        return 15.0;
    }

    @Override
    public boolean collisionCheck(SimEntity other) {
        double distance = currentPosition.toroidDistance(other.getPosition(), canvasWidth, canvasHeight);
        return distance < getCollisionRadius() + other.getCollisionRadius();
    }

    //predator speed 
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setMaxTurnRate(double maxTurnRate) {
        this.maxTurnRate = maxTurnRate;
    }

    //add the method to avoid obstacles
    @Override
    public void avoidObstacles(List<Obstacle> obstacles) {
        steerAroundObstacles(obstacles);  //smooth avoidance
        hardPushback(obstacles);           //push back if inside obstacle
    }
}
