package main;

public class MurmurationSimulationApp {

	private MySimulationEngine engine;

	public MurmurationSimulationApp() {

		engine = new MySimulationEngine();
	}

	private void start() {
		engine.start(10);
	}

	public static void main(String[] args) {

		MurmurationSimulationApp simulation = new MurmurationSimulationApp();
		simulation.start();
	}

}
