package ex5.utils;

/**
 * A counter class.
 * <p>
 *     Counter is an object that counts.
 *     It can be only increased.
 * </p>
 * <p>
 *     Counter adds functionality to int (can be changed using methods), this it is an int decorator).
 * </p>
 *
 * @author Noam Kimhi
 * @author Or Forshmit
 */
public class Counter {

    private int count;

    /**
     * Creates a counter with value 0.
     */
    public Counter() {
        this.count = 0;
    }

    /**
     * Increases the counter by 1.
     */
    public void increase() {
        this.count++;
    }

    /**
     * Returns the counter value.
     * @return The counter value.
     */
    public int getCount() {
        return count;
    }

}
