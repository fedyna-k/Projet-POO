/**
 * @brief This file contains the public class Canvas.
 * 
 * @file Canvas.java
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * Part of the `graphics` package. It contains a class that allow to draw on screen.
 */

package graphics;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.Timer;

import character.Player;
import geometry.Vector2D;
import map.Map;

/**
 * @class Canvas
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * @brief This class allows to draw on screen.
 * 
 * It should only be instancied once per Window.
 * 
 * The instanciation takes place inside the Window class.
 * 
 * @see graphics.Window
 */
public class Canvas extends JPanel {
    /** @brief Tells if the window is in fullscreen. */
    private boolean isFullscreen;
    /** @brief The main timer that refreshes the screen. */
    private Timer timer;
    /** @brief The camera that follows the player. */
    private Camera camera;

    // TESTING PURPOSE
    private Player player;
    private Player player2;
    private KeyStack stack;
    private boolean wasReleasedO;
    private boolean wasReleasedSpace;
    private Map map;
    // ---------------

    /**
     * @brief The default constructor.
     * 
     * Calls the main constructor with fullscreen set to false.
     */
    public Canvas() {
        this(false);
    }

    /**
     * @brief The main constructor.
     * 
     * First starts by constructing a JFrame with double buffer.
     * 
     * The timer is set here, so if you want to add things to the main loop
     * you should edit this.
     * 
     * @param isFullscreen Is the screen in fullscreen mode ?
     * @see javax.swing.JPanel
     */
    public Canvas(boolean isFullscreen) {
        super(true);
        this.isFullscreen = isFullscreen;
        
        this.camera = Camera.getCamera(this);
        setBackground(new Color(42, 42, 42, 255));

        // TESTING PURPOSE
        this.player = new Player(0, 0);
        this.player2 = new Player(0, 0);
        this.map = new Map("../src/main/resources/map/");
        this.stack = new KeyStack(this);
        this.wasReleasedO = true;
        this.wasReleasedSpace = true;
        stack.listenTo("Z");
        stack.listenTo("S");
        stack.listenTo("Q");
        stack.listenTo("D");
        stack.listenTo("O"); 
        stack.listenTo("SPACE");

        this.camera.setFocusOn(player);
        // ---------------

        timer = new Timer(0, event -> {
            // TESTING PURPOSE
            Vector2D movement = new Vector2D();
            if (stack.isPressed("Z")) {
                movement.y -= 4;
            }
            if (stack.isPressed("S")) {
                movement.y += 4;
            }
            if (stack.isPressed("Q")) {
                movement.x -= 4;
            }
            if (stack.isPressed("D")) {
                movement.x += 4;
            }
            if (stack.isPressed("O")) {
                if (wasReleasedO && !player.isDodging()) {
                    player.attack();
                    wasReleasedO = false;
                }
            } else {
                wasReleasedO = true;
            }
            if (stack.isPressed("SPACE")) {
                if (wasReleasedSpace) {
                    player.dodge();
                    wasReleasedSpace = false;
                }
            } else {
                wasReleasedSpace = true;
            }

            player.move(movement);
            // ---------------


            Vector2D difference = Vector2D.add(player.getPosition(), Vector2D.scale(player2.getPosition(), -1));

            if (difference.norm() > 120) {
                difference.normalize();
                player2.move(Vector2D.scale(difference, 3));
            } else {
                player2.move(0, 0);
            }
        });

        timer.addActionListener(e -> repaint());
        timer.start();
    }

    /**
     * @brief Return the size of the window for cpu/gpu handle.
     * 
     * The default size of the window is 800x600 (in pixels).
     * 
     * @return The size depending on the mode.
     * @warning It will take the dimensions of the main screen, the game could be on portrait mode and get glitchy.
     */
    @Override
    public Dimension getPreferredSize() {
        if (isFullscreen) {
            return Toolkit.getDefaultToolkit().getScreenSize();
        } else {
            return new Dimension(800, 600);
        }
    }

    /**
     * @brief Redefined for optimizing.
     * 
     * @param g The objects that stores informations that will be drawn.
     * @warning Do not edit this.
     */
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    /**
     * @brief Where we draw everything.
     * 
     * You should use the Camera class to draw as it computes all the evil maths
     * behind the conversion between absolute and canvas-relative positions.
     * 
     * @param g The objects that stores informations that will be drawn.
     * @warning If too much is drawn, it can lag quite much.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // TESTING PURPOSE

        int SCALE = isFullscreen ? 4 : 2;
    
        for (int i = (int)this.player.getPosition().x / (32 * SCALE) - 9 ; i < (int)this.player.getPosition().x / (32 * SCALE) + 10 ; i++) {
            for (int j = (int)this.player.getPosition().y / (32 * SCALE) - 6 ; j < (int)this.player.getPosition().y / (32 * SCALE) + 7 ; j++) {
                this.map.drawTile(this.camera, g, i, j, SCALE);
            }
        }

        this.camera.drawImage(g, this.player2.getSprite(), this.player2.getPosition().x, this.player2.getPosition().y, SCALE, this.player2.getOffset());
        this.camera.drawImage(g, this.player.getSprite(), this.player.getPosition().x, this.player.getPosition().y, SCALE, this.player.getOffset());
        
        // ---------------
    }

    /**
     * @brief Get Canvas center point.
     * @return A Vector2D containing the points coordinates.
     */
    public Vector2D getCenter() {
        return new Vector2D(this.getWidth() / 2, this.getHeight() / 2);
    }
}
