package main;

import java.awt.event.*;
import geometry.CartesianCoordinate;
import obstacle.Mouse;
import obstacle.Obstacle;
import drawing.Canvas;
import java.awt.Color;
import java.util.List;

public class MouseEventHandler {

    private final Canvas canvas;  //stores the canvas object where drawing occurs
    private final List<Obstacle> obstacles;  //stores the list of obstacles present in the canvas
    private CartesianCoordinate mousePosition;  //keeps track of the current mouse position
    private boolean dragging = false;  //flag to track if the mouse is being dragged

    //constructor, which will initialise canvas and obstacles //initialises the MouseEventHandler with canvas and obstacles
    public MouseEventHandler(Canvas canvas, List<Obstacle> obstacles) {
        this.canvas = canvas;  //initialises the canvas where drawing happens
        this.obstacles = obstacles;  //initialises the list of obstacles
    }

    //attach mouse listeners to the canvas //attaches mouse event listeners to detect mouse movements and clicks
    public void attachListeners() {
        //mouse motion listener to update mouse obstacle position
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateMouseObstacle(e);  // updates the obstacle position when mouse moves
            }
        });

        //mouse listener for mouse clicks and dragging events
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleRightClickToRemoveObstacle(e);  // handles right-click to remove obstacle
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {  //checks if left button was pressed
                    dragging = true;  //sets dragging to true when mouse is pressed
                    mousePosition = new CartesianCoordinate(e.getX(), e.getY());  //updates mouse position
                    removeMouseObstacle();  //removes the previous mouse obstacle
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {  //checks if left button was released
                    dragging = false;  //stops dragging when mouse is released
                    if (e.getY() < canvas.getHeight() && e.getX() < canvas.getWidth()) {  //checks if mouse is inside canvas
                        createObstacle(e);  //creates an obstacle if within the canvas bounds
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mousePosition = new CartesianCoordinate(e.getX(), e.getY());  //updates mouse position when it enters the canvas
                synchronized (obstacles) {  // thread safety while modifying obstacles list
                    obstacles.add(new Mouse(canvas, mousePosition, 50));  //adds a new mouse obstacle
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!dragging) {  //only remove mouse obstacle if not dragging
                    removeMouseObstacle();  //removes the mouse obstacle when the mouse exits the canvas
                }
            }
        });
    }

    //method to update mouse obstacle position //updates the mouse obstacle's position as the mouse moves
    private void updateMouseObstacle(MouseEvent e) {
        mousePosition = new CartesianCoordinate(e.getX(), e.getY());  //updates the mouse position
        synchronized (obstacles) {  //thread safety while modifying obstacles list
            obstacles.remove(obstacles.size() - 1);  //removes the last mouse obstacle
            obstacles.add(new Mouse(canvas, mousePosition, 100));  //adds a new mouse obstacle at the updated position
        }
    }

    //method to handle right-click to remove obstacles //removes an obstacle if right-clicked
    private void handleRightClickToRemoveObstacle(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {  //checks if the right mouse button was clicked
            if (obstacles.size() < 5) {  //prevents removal if there are fewer than 5 obstacles
                System.out.println("Cannot remove that obstacle!");  //prints message if removal is not allowed
            } else {
                synchronized (obstacles) {  //thread safety while modifying obstacles list
                    obstacles.get(obstacles.size() - 2).unDraw();  //undraws the obstacle to be removed
                    obstacles.remove(obstacles.size() - 2);  //removes the obstacle
                    System.out.println("Obstacle removed.");  //confirms removal
                }
            }
        }
    }

    //method to create an obstacle when the mouse is released //creates a new obstacle when the mouse is released
    private void createObstacle(MouseEvent e) {
        int xLength = (int) (e.getX() - mousePosition.getX());  //calculates the x-length of the obstacle
        int yLength = (int) (e.getY() - mousePosition.getY());  //calculates the y-length of the obstacle
        synchronized (obstacles) {  //thread safety while modifying obstacles list
        	obstacles.add(new Obstacle(canvas, mousePosition, xLength, yLength, new Color(220, 20, 60)));  //creates and adds the obstacle
            obstacles.get(obstacles.size() - 1).draw();  //draws the newly created obstacle
            mousePosition = new CartesianCoordinate(e.getX(), e.getY());  //updates the mouse position
            obstacles.add(new Mouse(canvas, mousePosition, 0));  //adds a new mouse obstacle at the release position
        }
    }

    //method to remove the mouse obstacle //removes the current mouse obstacle
    private void removeMouseObstacle() {
        synchronized (obstacles) {  //thread safety while modifying obstacles list
            if (!obstacles.isEmpty()) {  //checks if the obstacles list is not empty
                obstacles.remove(obstacles.size() - 1);  //removes the last obstacle
            }
        }
    }
}
