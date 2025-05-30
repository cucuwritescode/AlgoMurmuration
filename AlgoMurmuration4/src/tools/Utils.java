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
	 * This method was added by me to extend the provided class.
	 * It offers a simple way to generate a random integer within a given range
	 *
	 */

	public static int generateInt(int min, int max) {
		return (int)((Math.random() * (max - min)) + min);
	}
}
