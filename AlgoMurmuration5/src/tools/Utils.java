package tools;
import java.util.Random;
public class Utils {

    private static final Random random = new Random();  //single instance of Random

    /**
     * Generates a random integer between the given min (inclusive) and max (exclusive).
     * 
     * @param min Minimum output value (inclusive).
     * @param max Maximum output value (exclusive).
     * @return Random integer between min and max.
     */
    public static int generateInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min.");
        }
        return random.nextInt(max - min) + min;
    }

    /**
     * Pauses the current thread for a set period of time.
     * 
     * @param time the time to pause, in milliseconds.
     */
    public static void pause(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
