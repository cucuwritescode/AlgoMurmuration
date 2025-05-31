package geometry;

public class CartesianCoordinate {

    private final double xPosition;
    private final double yPosition;

    //constructor
    public CartesianCoordinate(double xPosition, double yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }

    //method to calculate the distance to another CartesianCoordinate
    public double distance(CartesianCoordinate point) {
        return new LineSegment(this, point).getLength();  //delegate to LineSegment for the calculation
    }

    //method to calculate the angle between this point and another
    public double angle(CartesianCoordinate point) {
        return new LineSegment(this, point).getAngle();  //delegate to LineSegment for angle
    }

    //method to calculate toroidal distance (with wrapping behavior for the grid)
    public double toroidDistance(CartesianCoordinate point, int xWindowSize, int yWindowSize) {
        double xDistance = Math.abs(this.getX() - point.getX());
        double yDistance = Math.abs(this.getY() - point.getY());
        if (xDistance > xWindowSize / 2) {
            xDistance = xWindowSize - xDistance;
        }
        if (yDistance > yWindowSize / 2) {
            yDistance = yWindowSize - yDistance;
        }
        return Math.hypot(xDistance, yDistance);
    }

    //getter for x position
    public double getX() {
        return xPosition;
    }

    //getter for y position
    public double getY() {
        return yPosition;
    }

    @Override
    public String toString() {
        return String.format("X: %.2f, Y: %.2f", getX(), getY());
    }
}
