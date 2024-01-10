/**
 * @brief This file contains the public class Window.
 * 
 * @file Window.java
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * Part of the `graphics` package. It is the main wrapper of the program GUI.
 */

package graphics;

import javax.swing.JFrame;

/**
 * @class Window
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * @brief This class is the main class for the GUI.
 * 
 * It should only be instancied once.
 * 
 * @note It can be a standalone for the game.
 * @see javax.swing.JFrame
 */
public class Window extends JFrame {
    /**
     * @brief The Canvas where everything is drawn.
     * 
     * @see graphics.Canvas
     */
    public Canvas canvas;

    /**
     * @brief Default constructor.
     * 
     * Creates a new Window with fullscreen turned off using the full constructor.
     */
    public Window() {
        this(true);
    }

    /**
     * @brief Full constructor.
     * 
     * Create a new Window based on JFrame.
     * 
     * @param isFullscreen Should the window be fullscreen ?
     * @see javax.swing.JFrame
     */
    public Window(boolean isFullscreen) {
        // Basic needs
        setSize(600, 600);
        setTitle("Les chevaliers d'Ether");
        setResizable(false);
        setUndecorated(isFullscreen);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set fullscreen
        if (isFullscreen) {
            setExtendedState(MAXIMIZED_BOTH);
        }

        this.canvas = new Canvas(isFullscreen);

        add(canvas);
        pack();
    }
}
