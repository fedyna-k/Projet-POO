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
import java.awt.Rectangle;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import character.*;

import geometry.Vector2D;
import map.Map;


/**
 * @class Canvas
 * @author Kevin Fedyna
 * @date 16/11/2023
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
    private Timer timer;
    /** @brief The camera that follows the player. */
    private Camera camera;

    // TESTING PURPOSE
    private Map map;
    private Mage mage;
    private Crystal blueCrystal;

    private Crystal purpleCrystal;
    private Monster badguy;
    private KeyStack stack;
    private boolean wasReleasedO;
    private boolean wasReleasedSpace;
    private boolean wasReleasedI;

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
        this.mage = new Mage(0, 0);
        this.blueCrystal = new Crystal(40, 0, "blue");
        this.purpleCrystal = new Crystal(80, 0, "purple");
        this.badguy = new Monster(this, 400, 0);
        this.map = new Map("../src/main/resources/map/");
        this.stack = new KeyStack(this);
        this.wasReleasedO = true;
        this.wasReleasedSpace = true;
        this.wasReleasedI = true;

        stack.listenTo("Z");
        stack.listenTo("S");
        stack.listenTo("Q");
        stack.listenTo("D");
        stack.listenTo("O");
        stack.listenTo("SPACE");
        stack.listenTo("I");

        //this.camera.setFocusOn(player);
        this.camera.setFocusOn(mage);
        // ---------------

        timer = new Timer(0, event -> {
            // TESTING PURPOSE
            Vector2D movement = new Vector2D();
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
                if (wasReleasedO && !mage.isDodging() && !mage.isBlocking()) {
                    mage.attack();
                    wasReleasedO = false;
                }
            } else {
                wasReleasedO = true;
            }
            if (stack.isPressed("SPACE")) {
                if (wasReleasedSpace) {
                    mage.dodge();
                    wasReleasedSpace = false;
                }
            } else {
                wasReleasedSpace = true;
            }

            if (stack.isPressed("I") && wasReleasedI) {
                mage.block();
                wasReleasedI = false;
            }

            if (!stack.isPressed("I") && !wasReleasedI) {
                mage.stopBlocking();
                wasReleasedI = true;
            }

            // ---------------

            // movement

            // The minimum distance required between player and monster
            double minDistance = 70.0;

            // Cooldown time for monster attacks
            double cooldown = 60.0;

            // Check if there is no collision before moving entities
            if (!Collision.checkCollision(badguy, mage.getPosition())
                    && !Collision.checkCollision(mage, mage.getPosition())
                    && !Collision.checkCollisionWithEntities(mage, badguy, mage.getPosition(),
                            badguy.getPosition())) {

                // Save the current positions before movement to revert in case of collision
                Vector2D playerPositionBeforeMove = new Vector2D(mage.getPosition().x, mage.getPosition().y);
                Vector2D badguyPositionBeforeMove = new Vector2D(badguy.getPosition().x, badguy.getPosition().y);

                // Calculate the vector representing the distance between player and monster
                Vector2D difference = Vector2D.subtract(mage.getPosition(), badguy.getPosition());
                // Move the player if there is no collision
                mage.move(movement);

                // Compute monster movement based on aggro range
                if (difference.norm() < AGGRO_RANGE) {
                    if (difference.norm() > minDistance) {
                        // Normalize the vector to set the direction
                        difference.normalize();
                        badguy.move(difference);
                    } else if (difference.norm() <= minDistance) {
                        // Stop monster movement and attempt an attack
                        badguy.stopMoving();
                        Monster.tryAttack(badguy, mage, difference, PROBABILITY_OF_ATTACK, cooldown);

                        // Handle monster attack
                        if (Collision.checkMonsterAttack(badguy, mage, badguy.getPosition(), mage.getPosition())) {
                            Collision.handleMonsterAttack(badguy, mage, badguy.getPosition(), mage.getPosition());
                        }
                    }
                } else {
                    // If outside aggro range, make the monster move randomly
                    badguy.randMovement();
                }

                // Handle player attack
                if (Collision.checkPlayerAttack(mage, badguy, mage.getPosition(), badguy.getPosition())) {
                    Collision.handlePlayerAttack(mage, badguy, mage.getPosition(), badguy.getPosition());
                }

                // Check for collisions after movement
                if (Collision.checkCollision(badguy, mage.getPosition())
                        || Collision.checkCollision(mage, mage.getPosition())
                        || Collision.checkCollisionWithEntities(mage, badguy, mage.getPosition(),
                                badguy.getPosition())) {

                    // Collision detected, revert movements
                    mage.setPosition(playerPositionBeforeMove.x, playerPositionBeforeMove.y);
                    badguy.setPosition(badguyPositionBeforeMove.x, badguyPositionBeforeMove.y);
                }
            }
        });

        timer.addActionListener(e -> repaint());
        timer.start();
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
        int tileSize = map.getTileSize() * SCALE;

        // Get focused coordinates
        int focusX = this.camera.getFocused() != null ? (int) this.camera.getFocused().getPosition().x : 0;
        int focusY = this.camera.getFocused() != null ? (int) this.camera.getFocused().getPosition().y : 0;

        // Get tile infos for screen
        int width = getPreferredSize().width / (this.map.getTileSize() * SCALE);
        int height = getPreferredSize().height / (this.map.getTileSize() * SCALE);

        // Draw map based on coordinates
        for (int i = focusX / (this.map.getTileSize() * SCALE) - width / 2
                - 1; i < focusX / (this.map.getTileSize() * SCALE) + width / 2 + 2; i++) {
            for (int j = focusY / (this.map.getTileSize() * SCALE) - height / 2
                    - 1; j < focusY / (this.map.getTileSize() * SCALE) + height / 2 + 2; j++) {
                this.map.drawTile(this.camera, g, i, j, SCALE);
            }
        }

        this.camera.drawImage(g, this.badguy.getSprite(), this.badguy.getPosition().x,
                this.badguy.getPosition().y,
                SCALE, this.badguy.getOffset());
        this.camera.drawImage(g, this.mage.getSprite(), this.mage.getPosition().x, this.mage.getPosition().y,
                SCALE, this.mage.getOffset());
        this.camera.drawImage(g, this.blueCrystal.getSprite(), this.blueCrystal.getPosition().x, this.blueCrystal.getPosition().y,
                SCALE, this.blueCrystal.getOffset());
        this.camera.drawImage(g, this.purpleCrystal.getSprite(), this.purpleCrystal.getPosition().x, this.purpleCrystal.getPosition().y,
                SCALE, this.purpleCrystal.getOffset());

        /* Drawing Hitbox */
        // hitbox crystal
        Rectangle crystalBlueHitbox = Collision.getPlayerHitbox(blueCrystal, blueCrystal.getPosition());
        camera.drawRect(
                g, crystalBlueHitbox.x,
                crystalBlueHitbox.y,
                (int) crystalBlueHitbox.getWidth(),
                (int) crystalBlueHitbox.getHeight(),
                Color.BLUE
        );

        Rectangle crystalPurpleHitbox = Collision.getPlayerHitbox(blueCrystal, blueCrystal.getPosition());
        camera.drawRect(
                g, crystalPurpleHitbox.x,
                crystalPurpleHitbox.y,
                (int) crystalPurpleHitbox.getWidth(),
                (int) crystalPurpleHitbox.getHeight(),
                Color.BLUE
        );


        // hitbox mage
        Rectangle playerHitbox = Collision.getPlayerHitbox(mage, mage.getPosition());
        camera.drawRect(g, playerHitbox.x, playerHitbox.y,
                (int) playerHitbox.getWidth(), (int) playerHitbox.getHeight(), Color.RED);

        // sword hitbox for mage
        Rectangle swordHitboxPlayer = Collision.getSwordHitbox(mage);
        if (mage.isAttacking()) {
            camera.drawRect(g, swordHitboxPlayer.x, swordHitboxPlayer.y,
                    (int) swordHitboxPlayer.getWidth(), (int) swordHitboxPlayer.getHeight(), Color.RED);
        }

        // hitbox bad guy
        Rectangle monsterHitbox = Collision.getMonsterHitbox(badguy, badguy.getPosition());
        if (monsterHitbox != null) {
            camera.drawRect(g, monsterHitbox.x, monsterHitbox.y,
                    (int) monsterHitbox.getWidth(), (int) monsterHitbox.getHeight(), Color.GREEN);
        }

        // sword hitbox for bad guy
        Rectangle swordHitboxMonster = Collision.getSwordHitboxMonster(badguy);
        if (badguy.isAttacking()) {
            camera.drawRect(g, swordHitboxMonster.x, swordHitboxMonster.y,
                    (int) swordHitboxMonster.getWidth(), (int) swordHitboxMonster.getHeight(), Color.GREEN);
        }

        // hitbox walls
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                // draw rectangle around walls
                if (map.isWall(i, j)) {
                    Rectangle tileHitbox = Collision.getTileHitbox(i, j, tileSize);
                    if (tileHitbox != null) {
                        camera.drawRect(g, tileHitbox.x, tileHitbox.y,
                                (int) tileHitbox.getWidth(), (int) tileHitbox.getHeight(), Color.RED);
                    }
                }
            }
        }

        /* End of Drawing Hitbox */

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
