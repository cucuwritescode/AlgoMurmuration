package geometry;

public class MyLineSegment {

	private final MyCartesianCoordinate startPoint;
	private final MyCartesianCoordinate endPoint;
	private final double xDistance;
	private final double yDistance;
	private final double length;
	private final double angle;

	public MyLineSegment(MyCartesianCoordinate startPoint, MyCartesianCoordinate endPoint) {
		super();
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.xDistance = endPoint.getX() - startPoint.getX();
		this.yDistance = endPoint.getY() - startPoint.getY();
		this.length = Math.hypot(Math.abs(xDistance), Math.abs(yDistance));
		// angle counterclockwise from the x axis
		this.angle = -Math.toDegrees((Math.atan2(yDistance, xDistance)));
	}

	public MyCartesianCoordinate getStartPoint() {
		return startPoint;
	}

	public MyCartesianCoordinate getEndPoint() {
		return endPoint;
	}

	public double getLength() {
		return length;
	}

	public double getAngle() {
		return angle;
	}

}
