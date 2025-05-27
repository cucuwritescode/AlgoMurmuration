package tools;

public class Utils {
	
	/**
	 * Pauses the current thread for a set period of time.
	 * 
	 * @param time the time to pause, in milliseconds.
	 */
	public static void pause(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException ie) {
			// We are happy with interruptions, so may not need to report any exceptions.
			ie.printStackTrace();
		}
	}
	/**
	 * 
	 * @param min Minimum output value
	 * @param max Maximum output value
	 * @return random integer between min and max
	 */
	public static int randomInt(int min, int max) {
		return (int)((Math.random() * (max - min)) + min);
	}
}
