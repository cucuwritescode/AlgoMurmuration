package geometry;

public class CartesianCoordinate {

	private final double yPosition;
	private final double xPosition;

	public CartesianCoordinate(double xPosition, double yPosition) {
		super();
		this.yPosition = yPosition;
		this.xPosition = xPosition;
	}

	/**
	 * Returns distance between two points
	 * 
	 * @param point
	 * @return distance between this point and some other point
	 */
	public double distance(CartesianCoordinate point) {
		return new LineSegment(this, point).getLength();
	}

	/**
	 * 
	 * @param point
	 * @param xWindowSize
	 * @param yWindowSize
	 * @return
	 */
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
	
	/**
	 * Returns angle between two point
	 * 
	 * @param point
	 * @return angle between this point and some other point
	 */
	public double angle(CartesianCoordinate point) {
		return new LineSegment(this, point).getAngle();
	}

	public double distanceToLine(LineSegment line) {
		
		CartesianCoordinate closestCorner = new CartesianCoordinate(0,0);
		if(distance(line.getStartPoint()) < distance(line.getEndPoint())) {
			closestCorner = line.getStartPoint();
		} else if (distance(line.getStartPoint()) > distance(line.getEndPoint())) {
			closestCorner = line.getEndPoint();
		}
		System.out.println("closest corner is: " + closestCorner);
		return distance(closestCorner) * Math.sin(Math.toRadians(angle(closestCorner)));
	}

	public double getX() {
		return xPosition;
	}

	public double getY() {
		return yPosition;
	}

	public String toString() {
		return "X: " + getX() + " Y: " + getY();
	}

}
