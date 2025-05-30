package geometry;

public class LineSegment {

	private final CartesianCoordinate startPoint;
	private final CartesianCoordinate endPoint;
	private final double xDistance;
	private final double yDistance;
	private final double length;
	private final double angle;

	public LineSegment(CartesianCoordinate startPoint, CartesianCoordinate endPoint) {
		super();
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.xDistance = endPoint.getX() - startPoint.getX();
		this.yDistance = endPoint.getY() - startPoint.getY();
		this.length = Math.hypot(Math.abs(xDistance), Math.abs(yDistance));
		// angle counterclockwise from the x axis
		this.angle = -Math.toDegrees((Math.atan2(yDistance, xDistance)));
	}

	public CartesianCoordinate getStartPoint() {
		return startPoint;
	}

	public CartesianCoordinate getEndPoint() {
		return endPoint;
	}

	public double getLength() {
		return length;
	}

	public double getAngle() {
		return angle;
	}

}
