/**
 * @brief This file contains the public class TrueTimer.
 *
 * @file TrueTimer.java
 * @author Kevin Fedyna
 * @date 10/01/2024
 *
 * Part of the `graphics` package. It contains a class that allow to save my sanity.
 */

package graphics;

import java.util.function.Function;

import javax.swing.SwingWorker;

/**
 * @class TrueTimer
 * @author Kevin Fedyna
 * @date 10/01/2024
 * 
 * @brief This class allow to generate .
 * 
 * It should only be instancied once.
 * 
 * @note It can be a standalone for the game.
 * @see javax.swing.JFrame
 */
public class TrueTimer extends SwingWorker<Void, Void> {
    /** @brief The action to execute */
    private Function<Void, Void> action;
    /** @brief The delay between each actions */
    private int delay;

    /**
     * @brief Creates a new True Timer
     * @param delay The delay between each actions is milliseconds
     * @param action The action to perform
     */
    public TrueTimer(int delay, Function<Void, Void> action) {
        this.action = action;
        this.delay = delay;
    }


    @Override
    protected Void doInBackground() throws Exception {
        
        while (!isCancelled()) {
            action.apply(null);

            if (delay != 0) {
                Thread.sleep(delay);
            }
        }

        return null;
    }
}
