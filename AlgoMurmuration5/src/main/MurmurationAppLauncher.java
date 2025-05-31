package main;

/**
 * MurmurationAppLauncher serves as the entry point for launching the boid simulation.
 * It initialises and starts the simulation engine.
 */
public class MurmurationAppLauncher {

    private MySimulationEngine engine;  //reference to the simulation engine, handles the entire flocking behaviour.

    /**
     * Constructor initialises the simulation engine.
     */
    public MurmurationAppLauncher() {
        engine = new MySimulationEngine();  //create and initialise a new instance of the simulation engine.
    }

    /**
     * Starts the simulation by calling the start method on the engine.
     */
    private void start() {
        engine.start(10);  //start the simulation with a delta time of 10 ms per frame.
    }

    /**
     * The main method is the entry point for the simulation application.
     * It creates an instance of the launcher and begins the simulation.
     *
     * @param args command line arguments, unused in this case.
     */
    public static void main(String[] args) {
        MurmurationAppLauncher simulation = new MurmurationAppLauncher();  //initialise the simulation app launcher.
        simulation.start();  //start the simulation.
    }
}
