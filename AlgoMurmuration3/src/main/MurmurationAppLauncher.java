package main;

public class MurmurationAppLauncher {

	private MySimulationEngine engine;

	public MurmurationAppLauncher() {

		engine = new MySimulationEngine();
	}

	private void start() {
		engine.start(10);
	}

	public static void main(String[] args) {

		MurmurationAppLauncher simulation = new MurmurationAppLauncher();
		simulation.start();
	}

}
