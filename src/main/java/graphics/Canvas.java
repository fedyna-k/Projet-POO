/**
 * @brief This file contains the public class Canvas.
 *
 * @file Canvas.java
 * @author Kevin Fedyna
 * @author Imene Bousmaha
 * @date 10/01/2024
 *
 * Part of the `graphics` package. It contains a class that allow to draw on screen.
 */

package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;

import javax.swing.JPanel;
import javax.swing.Timer;

import character.Player;
import character.Entity;
import character.Monster;
import geometry.Vector2D;
import map.Map;

/**
 * @class Canvas
 * @author Kevin Fedyna
 * @author Imene Bousmaha
 * @date 10/01/2024
 *
 * @brief This class allows to draw on screen.
 *
 *        It should only be instancied once per Window.
 *
 *        The instanciation takes place inside the Window class.
 *
 * @see graphics.Window
 */
public class Canvas extends JPanel {
    /** @brief Tells if the window is in fullscreen. */
    private boolean isFullscreen;
    /** @brief The main timer that refreshes the screen. */
    private Timer paintTimer;
    /** @brief Timer that does not depend on EDT, handles all computations. */
    private TrueTimer mainTimer;
    /** @brief The camera that follows the player. */
    private Camera camera;

    /** @brief The map object */
    private Map map;
    /** @brief The player */
    private Player player;
    /** @brief Monster pool */
    private ArrayList<Monster> badguys;
    /** @brief All entities */
    private ArrayList<Entity> allEntities;
    private KeyStack stack;
    private boolean wasReleasedO;
    private boolean wasReleasedSpace;
    private boolean wasReleasedI;
    private boolean wasReleasedP;

    boolean entitiesCollision = false;

    static final double PROBABILITY_OF_ATTACK = 0.8;
    static final double AGGRO_RANGE = 500.0;
    Random random = new Random();

    // ---------------

    /**
     * @brief The default constructor.
     *
     *        Calls the main constructor with fullscreen set to false.
     */
    public Canvas() {
        this(false);
    }

    /**
     * @brief The main constructor.
     *
     *        First starts by constructing a JFrame with double buffer.
     *
     *        The timer is set here, so if you want to add things to the main loop
     *        you should edit this.
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
        this.player = new Player(10, 10);
        this.allEntities = new ArrayList<>(); 
        this.badguys = new ArrayList<>();

        this.allEntities.add(player);

        this.map = new Map("../src/main/resources/map/");
        this.stack = new KeyStack(this);
        this.wasReleasedO = true;
        this.wasReleasedSpace = true;
        this.wasReleasedI = true;

        stack.listenTo("Z");
        stack.listenTo("S");
        stack.listenTo("Q");
        stack.listenTo("D");
        stack.listenTo("SPACE");
        stack.listenTo("O");
        stack.listenTo("I");
        stack.listenTo("P");

        this.camera.setFocusOn(player);
        // ---------------

        Function<Void, Void> loop = e -> {
            // TESTING PURPOSE
            Vector2D movement = new Vector2D();
            if (stack.isPressed("C")) {
                movement.y -= 1;
            }
            if (stack.isPressed("Z")) {
                movement.y -= 1;
            }
            if (stack.isPressed("S")) {
                movement.y += 1;
            }
            if (stack.isPressed("Q")) {
                movement.x -= 1;
            }
            if (stack.isPressed("D")) {
                movement.x += 1;
            }
            if (stack.isPressed("O")) {
                if (wasReleasedO && !player.isDodging() && !player.isBlocking()) {
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

            // ---- debug
            if (stack.isPressed("P")) {
                if (wasReleasedP) {
                    Monster newMonster = new Monster(player.getPosition().x + 100, player.getPosition().y);
                    this.allEntities.add(newMonster);
                    this.badguys.add(newMonster);
                    wasReleasedP = false;
                }
            } else {
                wasReleasedP = true;
            }
            // ---- debug

            if (stack.isPressed("I") && wasReleasedI) {
                player.block();
                wasReleasedI = false;
            }

            if (!stack.isPressed("I") && !wasReleasedI) {
                player.stopBlocking();
                wasReleasedI = true;
            }

            // ---------------

            // movement

            // The minimum distance required between player and monster
            double minDistance = 70.0;

            // Cooldown time for monster attacks
            double cooldown = 60.0;

            ArrayList<Monster> deadguys = new ArrayList<>();

            player.move(movement, player.getStats().getSpeed() / 10 + 0.5, allEntities);

            for (Monster badguy : badguys) {
                Vector2D difference = Vector2D.subtract(player.getPosition(), badguy.getPosition());

                if (difference.norm() < AGGRO_RANGE) {
                    if (difference.norm() > minDistance) {
                        // Normalize the vector to set the direction
                        difference.normalize();
                        badguy.move(difference, allEntities);
                    } else if (difference.norm() <= minDistance) {
                        // Stop monster movement and attempt an attack
                        badguy.stopMoving();
                        Monster.tryAttack(badguy, player, difference, PROBABILITY_OF_ATTACK, cooldown);

                        // Handle monster attack
                        if (Collision.checkMonsterAttack(badguy, player, badguy.getPosition(), player.getPosition())) {
                            Collision.handleMonsterAttack(badguy, player, badguy.getPosition(), player.getPosition());
                        }
                    }
                } else {
                    // If outside aggro range, make the monster move randomly
                    badguy.randMovement(allEntities);
                }

                // Handle player attack
                if (Collision.checkPlayerAttack(player, badguy, player.getPosition(), badguy.getPosition())) {
                    Collision.handlePlayerAttack(player, badguy, player.getPosition(), badguy.getPosition());
                }

                if (badguy.isDead()) {
                    deadguys.add(badguy);
                }
            }

            for (Monster deadguy : deadguys) {
                badguys.remove(deadguy);
                allEntities.remove(deadguy);
            }

            return null;
        };

        mainTimer = new TrueTimer(4, loop);
        mainTimer.execute();

        paintTimer = new Timer(4, e -> repaint());
        paintTimer.start();
    }

    /**
     * @brief Return the size of the window for cpu/gpu handle.
     *
     *        The default size of the window is 800x600 (in pixels).
     *
     * @return The size depending on the mode.
     * @warning It will take the dimensions of the main screen, the game could be on
     *          portrait mode and get glitchy.
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
     * @brief Where we draw everything.
     *
     *        You should use the Camera class to draw as it computes all the evil
     *        maths
     *        behind the conversion between absolute and canvas-relative positions.
     *
     * @param g The objects that stores informations that will be drawn.
     * @warning If too much is drawn, it can lag quite much.
     */
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // TESTING PURPOSE

        int SCALE = 2;

        // Get focused coordinates
        int focusX = this.camera.getFocused() != null ? (int) this.camera.getFocused().getPosition().x : 0;
        int focusY = this.camera.getFocused() != null ? (int) this.camera.getFocused().getPosition().y : 0;

        // Get tile infos for screen
        int width = getPreferredSize().width / (this.map.getTileSize() * SCALE);
        int height = getPreferredSize().height / (this.map.getTileSize() * SCALE);

        // Draw map based on coordinates and clamped

        int lowerTileIndexX = focusX / (this.map.getTileSize() * SCALE) - width / 2 - 1;
        int lowerTileIndexY = focusY / (this.map.getTileSize() * SCALE) - height / 2 - 1;

        int upperTileIndexX = focusX / (this.map.getTileSize() * SCALE) + width / 2 + 2;
        int upperTileIndexY = focusY / (this.map.getTileSize() * SCALE) + height / 2 + 2;

        if (lowerTileIndexX < 0) {
            upperTileIndexX -= lowerTileIndexX;
            lowerTileIndexX = 0;
        }

        if (lowerTileIndexY < 0) {
            upperTileIndexY -= lowerTileIndexY;
            lowerTileIndexY = 0;
        }

        if (upperTileIndexX >= map.getWidth()) {
            lowerTileIndexX -= upperTileIndexX - map.getWidth();
            upperTileIndexX -= upperTileIndexX - map.getWidth();
        }

        if (upperTileIndexY >= map.getHeight()) {
            lowerTileIndexY -= upperTileIndexY - map.getHeight();
            upperTileIndexY -= upperTileIndexY - map.getHeight();
        }

        for (int i = lowerTileIndexX; i < upperTileIndexX; i++) {
            for (int j = lowerTileIndexY; j < upperTileIndexY; j++) {
                this.map.drawTile(this.camera, g, i, j, SCALE);
            }
        }

        for (Entity badguy : badguys) {
            this.camera.drawImageClamped(g, this.map, badguy.getSprite(), badguy.getPosition().x,
                    badguy.getPosition().y,
                    SCALE, badguy.getOffset());

            int healthLength = (int)(badguy.getSpriteSize().x * badguy.getStats().getHealth().getPercent());
            int healthOffset = (int)(badguy.getSpriteSize().x * (1 - badguy.getStats().getHealth().getPercent()) / 2);
                        
            this.camera.fillRectClamped(g, this.map, badguy.getPosition().x, badguy.getPosition().y - (int)(badguy.getSpriteSize().y / 1.5), (int)badguy.getSpriteSize().x, 3 * SCALE, Color.red);
            this.camera.fillRectClamped(g, this.map, badguy.getPosition().x - healthOffset, badguy.getPosition().y - (int)(badguy.getSpriteSize().y / 1.5), healthLength, 3 * SCALE, Color.green);
            this.camera.drawRectClamped(g, this.map, badguy.getPosition().x, badguy.getPosition().y - (int)(badguy.getSpriteSize().y / 1.5), (int)badguy.getSpriteSize().x, 3 * SCALE, Color.black);
        }

        this.camera.drawImageClamped(g, this.map, this.player.getSprite(), this.player.getPosition().x, this.player.getPosition().y,
                SCALE, this.player.getOffset());

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
