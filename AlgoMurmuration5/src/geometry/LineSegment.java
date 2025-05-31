package geometry;

public class LineSegment {

    private final CartesianCoordinate startPoint;
    private final CartesianCoordinate endPoint;
    private double length;
    private double angle;

    //constructor - directly assigns the points and calculates length/angle
    public LineSegment(CartesianCoordinate startPoint, CartesianCoordinate endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.length = calculateLength();
        this.angle = calculateAngle();
    }

    //method to calculate the length of the line segment using Euclidean distance
    private double calculateLength() {
        double dx = endPoint.getX() - startPoint.getX();
        double dy = endPoint.getY() - startPoint.getY();
        return Math.hypot(dx, dy);  //hypotenuse of the right-angled triangle formed by dx and dy
    }

    //method to calculate the angle between the two points (counterclockwise from the x-axis)
    private double calculateAngle() {
        double dx = endPoint.getX() - startPoint.getX();
        double dy = endPoint.getY() - startPoint.getY();
        return Math.toDegrees(Math.atan2(dy, dx));  //angle in degrees
    }

    //getter for the start point
    public CartesianCoordinate getStartPoint() {
        return startPoint;
    }

    //getter for the end point
    public CartesianCoordinate getEndPoint() {
        return endPoint;
    }

    //getter for the length of the line segment
    public double getLength() {
        return length;
    }

    //getter for the angle of the line segment
    public double getAngle() {
        return angle;
    }

    @Override
    public String toString() {
        return String.format("Line from %s to %s | Length: %.2f | Angle: %.2f°", startPoint, endPoint, length, angle);
    }
}
