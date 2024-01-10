/**
 * @brief This file contains the public class Range.
 * 
 * @file Range.java
 * @author Kevin Fedyna
 * @date 9/1/2024
 * 
 * Part of the `geometry` package.
 * It contains a class that allows to manipulate values inside range.
 */

package geometry;

/**
 * @class Range
 * @author Kevin Fedyna
 * @date 9/1/2024
 * 
 * @brief This class allows to manipulate values inside range.
 */
public class Range {
    /** @brief The maximum value possible (included) */
    private int max;
    /** @brief The minimum value possible (included) */
    private int min;
    /** @brief The current value possible (included) */
    private int current;


    /**
     * @brief Creates a new Range object.
     * 
     * Will be in the form :
     * \f[
     *      [0, M]
     * \f]
     * 
     * Current defaults to max.
     * 
     * @param max The upper bound of the range.
     */
    public Range(int max) {
        this(0, max, max);
    }

    /**
     * @brief Creates a new Range object.
     * 
     * Will be in the form :
     * \f[
     *      [m, M]
     * \f]
     * 
     * Current defaults to max.
     * 
     * @param max The upper bound of the range.
     * @param min The lower bound of the range.
     */
    public Range(int min, int max) {
        this(min, max, max);
    }

    /**
     * @brief Creates a new Range object.
     * 
     * Will be in the form :
     * \f[
     *      [m, M]
     * \f]
     * 
     * @param max The upper bound of the range.
     * @param min The lower bound of the range.
     * @param current The current value in the interval.
     */
    public Range(int min, int max, int current) {
        this.max = max;
        this.min = min;
        this.current = current;
    }

    /**
     * @brief Sets the new value for stored current.
     * 
     * If value is out of bounds, clip it to bounds.
     * 
     * @param value The new value.
     */
    public void set(int value) {
        if (value < min) {
            this.current = min;
        } else if (value > max) {
            this.current = max;
        } else {
            this.current = value;
        }
    }

    /**
     * @brief Gets the stored current.
     * @return The stored current.
     */
    public int get() {
        return this.current;
    }

    /**
     * @brief Gets the ratio of current over max.
     * 
     * \f[
     *      \mathrm{getPercent} := \frac{c}{M}
     * \f]
     * @return The ratio.
     */
    public double getPercent() {
        return (1d * current) / max;
    }

    /**
     * @brief Checks if current is equal to min.
     * @return true if c = m.
     */
    public boolean isMin() {
        return this.current == this.min;
    }

    /**
     * @brief Checks if current is equal to max.
     * @return true if c = M.
     */
    public boolean isMax() {
        return this.current == this.min;
    }

    /**
     * @brief Helper function used to shorten and enhance code readability.
     * @param min The lower boundary.
     * @param max The upper boudary.
     * @param current The value to check.
     * @return true if min <= current <= max
     */
    static public boolean isIn(int min, int max, int current) {
        return min <= current && current <= max;
    }
}