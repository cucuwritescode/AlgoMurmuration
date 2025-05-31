package drawing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import geometry.CartesianCoordinate;
import geometry.LineSegment;

/**
 * <h2>Canvas</h2> This class represents a canvas object that can be drawn to
 * with various line segments.
 * 
 * <P>
 * The list of LineSegment's is stored in a collection within the implementation
 * of the class. This collection is now synchronised to deal with concurrent
 * accesses to the collection.
 * 
 * <p>
 * WARNING: This class should NOT be modified in ANY way.
 */
public final class Canvas extends JPanel {
	private static final long serialVersionUID = 1L;
	private int xSize, ySize;
	private final List<LineSegmentInColor> lines;
	private static final int DEFAULT_X = 800;
	private static final int DEFAULT_Y = 600;
	public static final int DEFAULT_LINE_WIDTH = 3;
	private int lineWidth = DEFAULT_LINE_WIDTH;
	public static final Color DEFAULT_COLOR = Color.BLACK;

	/**
	 * Default constructor which produces a canvas of the default size of 800 x 600.
	 */
	public Canvas() {
		this(DEFAULT_X, DEFAULT_Y);
	}

	/**
	 * Constructor which produces a canvas of a specified size.
	 * 
	 * @param x Width of the canvas.
	 * @param y Height of the canvas.
	 */
	public Canvas(int x, int y) {
		xSize = x;
		ySize = y;
		setupCanvas();
		lines = Collections.synchronizedList(new ArrayList<LineSegmentInColor>());
	}

	private void setupCanvas() {
		setSize(xSize, ySize);
		setVisible(true);
		repaint();
	}

	/**
	 * <b>NB: You never need to call this method yourself.</b> It handles the
	 * drawing but is called automatically each time a line segment is drawn.
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		// Smoother lines.
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(lineWidth));

		synchronized (lines) {
			LineSegment line;
			CartesianCoordinate startPoint, endPoint;
			for (LineSegmentInColor lineInColor : lines) {
				g2.setColor(lineInColor.getColor());
				line = lineInColor.getLine();
				startPoint = line.getStartPoint();
				endPoint = line.getEndPoint();
				g2.draw(new Line2D.Double(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY()));
			}
		}
	}

	/**
	 * Draws a line between two CartesianCoordinates to the canvas.
	 * 
	 * @param startPoint Starting coordinate.
	 * @param endPoint   Ending coordinate.
	 */
	public void drawLineBetweenPoints(CartesianCoordinate startPoint, CartesianCoordinate endPoint) {
		LineSegmentInColor line = new LineSegmentInColor(new LineSegment(startPoint, endPoint));
		synchronized (lines) {
			lines.add(line);
		}
		repaint();
	}

	/**
	 * Overloaded method that now adds color. Draws a line between two
	 * CartesianCoordinates to the canvas with a specified line color.
	 * 
	 * @param startPoint Starting coordinate.
	 * @param endPoint   Ending coordinate.
	 * @param color      Color to use for line.
	 */
	public void drawLineBetweenPoints(CartesianCoordinate startPoint, CartesianCoordinate endPoint, Color color) {
		LineSegmentInColor line = new LineSegmentInColor(new LineSegment(startPoint, endPoint), color);
		synchronized (lines) {
			lines.add(line);
		}
		repaint();
	}

	/**
	 * Draws a line segment to the canvas.
	 * 
	 * @param lineSegment The LineSegment to draw.
	 */
	public void drawLineSegment(LineSegment lineSegment) {
		LineSegmentInColor line = new LineSegmentInColor(lineSegment);
		synchronized (lines) {
			lines.add(line);
		}
		repaint();
	}

	/**
	 * Overloaded method that now adds color. Draws a line segment to the canvas
	 * with a specified line color.
	 * 
	 * @param lineSegment The LineSegment to draw.
	 * @param color       Color to use for line.
	 */
	public void drawLineSegment(LineSegment lineSegment, Color color) {
		LineSegmentInColor line = new LineSegmentInColor(lineSegment, color);
		synchronized (lines) {
			lines.add(line);
		}
		repaint();
	}

	/**
	 * Draws multiple line segments to the canvas.
	 * 
	 * @param lineSegments An array of LineSegment.
	 */
	public void drawLineSegments(LineSegment[] lineSegments) {
		for (LineSegment thisLineSegment : lineSegments) {
			LineSegmentInColor line = new LineSegmentInColor(thisLineSegment);
			synchronized (lines) {
				lines.add(line);
			}
		}
		repaint();
	}

	/**
	 * Overloaded method that now adds color. Draws multiple line segments to the
	 * canvas with a specified line color.
	 * 
	 * @param lineSegments An array of LineSegment.
	 * @param color        Color to use for lines.
	 */
	public void drawLineSegments(LineSegment[] lineSegments, Color color) {
		for (LineSegment thisLineSegment : lineSegments) {
			LineSegmentInColor line = new LineSegmentInColor(thisLineSegment, color);
			synchronized (lines) {
				lines.add(line);
			}
		}
		repaint();
	}

	/**
	 * Removes the most recently added line from the drawing.
	 */
	public void removeMostRecentLine() {
		synchronized (lines) {
			lines.remove(lines.size() - 1);
		}
	}

	/**
	 * Clears the canvas of all drawing.
	 */
	public void clear() {
		synchronized (lines) {
			lines.clear();
		}
		repaint();
	}

	/**
	 * Get the currently set width of drawn lines.
	 * 
	 * @return the current width.
	 */
	public int getLineWidth() {
		return lineWidth;
	}

	/**
	 * Set the width of all drawn lines.
	 * 
	 * @param lineWidth the width of all drawn lines.
	 */
	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}

	/**
	 * Wrapper class to add color ability to line segments. This helps with the
	 * implementation of color and so should not be exposed or subclassed.
	 */
	private final class LineSegmentInColor {
		private final LineSegment line;
		private final Color color;

		public LineSegmentInColor(LineSegment line) {
			this.line = line;
			this.color = DEFAULT_COLOR;
		}

		public LineSegmentInColor(LineSegment line, Color color) {
			this.line = line;
			this.color = color;
		}

		public LineSegment getLine() {
			return line;
		}

		public Color getColor() {
			return color;
		}
	}
}
