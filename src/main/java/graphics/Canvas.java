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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import character.Player;
import character.Entity;
import character.Entity.EntityState;
import character.Monster;
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
    private Monster badguy;
    private KeyStack stack;
    private EntityState currentState;
    private EntityState currentStateMonster;
    private boolean wasReleasedO;
    private boolean wasReleasedSpace;
    private boolean wasReleasedI;

    boolean entitiesCollision = false;

    private int attackCooldown = 0;
    private int damageCooldown = 0;
    static final double PROBABILITY_OF_ATTACK = 5.0;
    static final double AGGRO_RANGE = 500.0;
    Random random = new Random();

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
        this.player = new Player(10500, 3000);
        this.badguy = new Monster(this, 11000, 3000);
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

        this.camera.setFocusOn(player);
        // ---------------

        timer = new Timer(0, event -> {
            // TESTING PURPOSE
            Vector2D movement = new Vector2D();
            Vector2D movementMonster = new Vector2D();
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
            Vector2D difference = Vector2D.subtract(player.getPosition(), badguy.getPosition());
            Vector2D newPositionMonster = Vector2D.add(badguy.getPosition(), movementMonster);
            Vector2D playerNewPosition = Vector2D.add(player.getPosition(), movement);

            double attackRadius = 20.0;

            if (!checkCollision(badguy, newPositionMonster) && !checkCollision(player, playerNewPosition)
                    && !checkCollisionWithEntities(player, badguy, playerNewPosition, newPositionMonster)) {
                // Move if not colliding
                player.move(movement);

                // Compute monster movement based on aggro range
                if (difference.norm() < AGGRO_RANGE) {
                    difference.normalize();
                    badguy.move(difference);

                    // Probabilité d'attaque
                    double randomValue = random.nextDouble() * 100;
                    if (randomValue < PROBABILITY_OF_ATTACK && difference.norm() <= attackRadius) {
                        badguy.attack();
                    }
                } else {
                    badguy.randMovement();
                }

                // Handle player attack
                if (checkPlayerAttack(player, badguy, playerNewPosition, newPositionMonster)) {
                    handlePlayerAttack(player, badguy, playerNewPosition, newPositionMonster);
                }

                // Handle monster attack
                if (checkMonsterAttack(badguy, player, newPositionMonster, playerNewPosition)) {
                    handleMonsterAttack(badguy, player, newPositionMonster, playerNewPosition);
                }
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
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // TESTING PURPOSE

        int SCALE = 2;
        int tileSize = map.getTileSize() * SCALE;
        Vector2D movement = new Vector2D();
        Vector2D movementMonster = new Vector2D();
        Vector2D newPositionPlayer = Vector2D.add(player.getPosition(), movement);
        Vector2D newPositionMonster = Vector2D.add(badguy.getPosition(), movementMonster);

        // Get focused coordinates
        int focusX = this.camera.getFocused() != null ? (int)this.camera.getFocused().getPosition().x : 0;
        int focusY = this.camera.getFocused() != null ? (int)this.camera.getFocused().getPosition().y : 0;

        // Get tile infos for screen
        int width = getPreferredSize().width / (this.map.getTileSize() * SCALE);
        int height = getPreferredSize().height / (this.map.getTileSize() * SCALE);

        // Draw map based on coordinates
        for (int i = focusX / (this.map.getTileSize() * SCALE) - width / 2 - 1 ; i < focusX / (this.map.getTileSize() * SCALE) + width / 2 + 2 ; i++) {
            for (int j = focusY / (this.map.getTileSize() * SCALE) - height / 2 - 1 ; j < focusY / (this.map.getTileSize() * SCALE) + height / 2 + 2 ; j++) {
                this.map.drawTile(this.camera, g, i, j, SCALE);
            }
        }

        this.camera.drawImage(g, this.badguy.getSprite(), this.badguy.getPosition().x,
                this.badguy.getPosition().y,
                SCALE, this.badguy.getOffset());
        this.camera.drawImage(g, this.player.getSprite(), this.player.getPosition().x, this.player.getPosition().y,
                SCALE, this.player.getOffset());

        /* Drawing Hitbox */

        // hitbox player
        Rectangle playerHitbox = getPlayerHitbox(player, newPositionPlayer);
        if (playerHitbox != null) {
            camera.drawRect(g, playerHitbox.x, playerHitbox.y,
                    (int) playerHitbox.getWidth(), (int) playerHitbox.getHeight(), Color.RED);
        }

        // sword hitbox for player
        Rectangle swordHitboxPlayer = getSwordHitbox(player);
        if (swordHitboxPlayer != null) {
            camera.drawRect(g, swordHitboxPlayer.x, swordHitboxPlayer.y,
                    (int) swordHitboxPlayer.getWidth(), (int) swordHitboxPlayer.getHeight(), Color.RED);
        }

        // hitbox bad guy
        Rectangle monsterHitbox = getMonsterHitbox(badguy, newPositionMonster);
        if (monsterHitbox != null) {
            camera.drawRect(g, monsterHitbox.x, monsterHitbox.y,
                    (int) monsterHitbox.getWidth(), (int) monsterHitbox.getHeight(), Color.GREEN);
        }

        // sword hitbox for bad guy
        Rectangle swordHitboxMonster = getSwordHitboxMonster(badguy);
        if (swordHitboxMonster != null) {
            camera.drawRect(g, swordHitboxMonster.x, swordHitboxMonster.y,
                    (int) swordHitboxMonster.getWidth(), (int) swordHitboxMonster.getHeight(), Color.GREEN);
        }

        // hitbox walls
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                // draw rectangle around walls
                if (map.isWall(i, j)) {
                    Rectangle tileHitbox = getTileHitbox(i, j, tileSize);
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

    /**
     * @brief Retrieves the hitbox for the player at the specified position.
     *
     *        This method calculates and returns the hitbox for the player based on
     *        the given position.
     *
     * @param newPosition The position at which to calculate the player's hitbox.
     * @return A Rectangle representing the player's hitbox at the specified
     *         position.
     */
    private Rectangle getPlayerHitbox(Entity entity, Vector2D newPosition) {
        int SCALE = isFullscreen ? 4 : 2;
        int rectWidth = (int) (64 * SCALE / 1.9);
        int rectHeight = (int) (64 * SCALE / 1.5);
        return new Rectangle((int) newPosition.x, (int) newPosition.y, rectWidth, rectHeight);
    }

    /**
     * @brief Retrieves the hitbox for the monster at the specified position.
     *
     *        This method calculates and returns the hitbox for the monster based on
     *        the given position.
     *
     * @param newPosition The position at which to calculate the monster's hitbox.
     * @return A Rectangle representing the monster's hitbox at the specified
     *         position.
     */
    private Rectangle getMonsterHitbox(Entity entity, Vector2D newPosition) {
        int SCALE = isFullscreen ? 4 : 2;
        int rectWidth = (int) (64 * SCALE / 1.9);
        int rectHeight = (int) (64 * SCALE / 1.5);
        return new Rectangle((int) newPosition.x, (int) newPosition.y, rectWidth, rectHeight);
    }

    /**
     * @brief Retrieves the hitbox for the entity's sword during an attack.
     *
     *        This method calculates and returns the hitbox for the entity's sword
     *        during
     *        an attack. The hitbox dimensions are determined by scaling the
     *        entity's sprite
     *        and positioning the sword based on the entity's facing direction.
     *
     * @param entity The entity initiating the attack.
     * @return A Rectangle representing the hitbox of the entity's sword during an
     *         attack,
     *         or null if the entity is not currently attacking.
     */
    private Rectangle getSwordHitbox(Entity entity) {
        int SCALE = isFullscreen ? 4 : 2;
        Vector2D offset = entity.getOffset();
        double directionMultiplier = entity.isFacingLeft() ? -1 : 1;
        int spriteWidth = entity.getSprite().getWidth();

        int swordHeight = (int) (entity.getSprite().getHeight() * SCALE / 2);

        if (entity.isAttacking()) {
            double centerswordY = entity.getPosition().y - offset.y * SCALE + 30;
            double centerswordX = entity.getPosition().x - offset.x * SCALE + directionMultiplier * 96;
            int swordWidth = (int) (spriteWidth * 2);
            return new Rectangle((int) centerswordX, (int) centerswordY, swordWidth, swordHeight);
        }

        return null;
    }

    /**
     * @brief Retrieves the hitbox for a monster entity's sword during an attack.
     *
     *        This method calculates and returns the hitbox for a monster entity's
     *        sword
     *        during an attack. The hitbox dimensions are determined by scaling a
     *        fixed
     *        sprite width for monsters and positioning the sword based on the
     *        entity's
     *        facing direction.
     *
     * @param entity The monster entity initiating the attack.
     * @return A Rectangle representing the hitbox of the monster entity's sword
     *         during
     *         an attack, or null if the entity is not currently attacking.
     */
    private Rectangle getSwordHitboxMonster(Entity entity) {
        int SCALE = isFullscreen ? 4 : 2;
        Vector2D offset = entity.getOffset();
        double directionMultiplier = entity.isFacingLeft() ? -1 : 1;
        int spriteWidth = 64;

        int swordHeight = (int) (entity.getSprite().getHeight() * SCALE / 2);

        if (entity.isAttacking()) {
            double centerswordY = entity.getPosition().y - offset.y * SCALE + 30;
            double centerswordX = entity.getPosition().x - offset.x * SCALE + directionMultiplier * 96;
            int swordWidth = (int) (spriteWidth * 2);
            return new Rectangle((int) centerswordX, (int) centerswordY, swordWidth, swordHeight);
        } else {
            return null;
        }

    }

    /**
     * @brief Retrieves the hitbox for a tile at the specified grid coordinates.
     *
     *        This method calculates and returns the hitbox for a tile based on the
     *        specified grid coordinates and the size of each tile.
     *
     * @param i        The horizontal grid coordinate of the tile.
     * @param j        The vertical grid coordinate of the tile.
     * @param tileSize The size of each side of the tile.
     * @return A Rectangle representing the hitbox of the tile at the specified
     *         grid coordinates.
     */
    private Rectangle getTileHitbox(int i, int j, int tileSize) {
        int tileX = i * tileSize;
        int tileY = j * tileSize;
        return new Rectangle(tileX, tileY, tileSize, tileSize);
    }

    /**
     * @brief Checks for collisions between the player and a wall tile.
     *
     *        This method determines if a collision occurs between the player and a
     *        wall tile, represented by the given player and tile rectangles. If a
     *        collision is detected and the player is currently dodging, the
     *        player's position is
     *        reset to the previous valid coordinates, and the dodging state is
     *        reset.
     *
     * @param entityRect  The rectangle representing the hitbox of the entity.
     * @param tileRect    The rectangle representing the hitbox of the wall tile.
     * @param entity      The entity object involved in the collision.
     * @param newPosition The intended new position of the player.
     * @return True if a collision is detected and handled, indicating the player
     *         cannot move to the new position; otherwise, false.
     */
    private boolean checkCollisionWithWalls(Rectangle entityRect, Rectangle tileRect, Entity entity,
            Vector2D newPosition) {
        if (entityRect.intersects(tileRect)) {
            if (entity.isDodging()) {
                entity.stopDodging();
                if (entity.isFacingLeft()) {
                    entity.move(10, 0);
                } else {
                    entity.move(-10, 0);
                }
                return true;
            }
            return true;
        }
        return false;
    }

    /**
     * @brief Checks for collisions of an entity with walls and other entities.
     *
     *        This method determines if an entity, represented by its hitbox at the
     *        specified
     *        new position, collides with walls on the game map or other entities.
     *        It considers
     *        the entity type (player or monster) to calculate the correct hitbox.
     *        The method
     *        iterates through the map's tiles, checking for collisions with walls
     *        using the
     *        checkCollisionWithWalls function.
     *
     * @param entity      The entity for which to check collisions.
     * @param newPosition The intended new position of the entity.
     * @return True if a collision is detected with walls or other entities;
     *         otherwise, false.
     */
    public boolean checkCollision(Entity entity, Vector2D newPosition) {
        int SCALE = isFullscreen ? 4 : 2;
        int tileSize = map.getTileSize() * SCALE;

        Rectangle rect = Entity.isMonster(entity) ? getMonsterHitbox(entity, newPosition)
                : getPlayerHitbox(entity, newPosition);

        // Check collision with walls
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                if (map.isWall(i, j)) {
                    Rectangle tileRect = getTileHitbox(i, j, tileSize);

                    if (checkCollisionWithWalls(rect, tileRect, entity, newPosition)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * @brief Checks if the player's attack collides with a monster.
     *
     *        This method determines if the player's sword, represented by its
     *        hitbox,
     *        intersects with the hitbox of a monster. It is used to check if the
     *        player's
     *        attack successfully collides with a monster during the game.
     *
     * @param player             The player initiating the attack.
     * @param monster            The monster being attacked.
     * @param newPositionPlayer  The intended new position of the player.
     * @param newPositionMonster The intended new position of the monster.
     * @return True if the player's attack collides with the monster; otherwise,
     *         false.
     */
    private boolean checkPlayerAttack(Player player, Monster monster, Vector2D newPositionPlayer,
            Vector2D newPositionMonster) {
        Rectangle monsterHitbox = getMonsterHitbox(monster, newPositionMonster);
        Rectangle playerSwordHitbox = getSwordHitbox(player);

        return playerSwordHitbox != null && monsterHitbox != null && playerSwordHitbox.intersects(monsterHitbox);
    }

    /**
     * @brief Checks if the monster's attack collides with the player.
     *
     *        This method determines if the monster's sword, represented by its
     *        hitbox,
     *        intersects with the hitbox of the player. It is used to check if the
     *        monster's
     *        attack successfully collides with the player during the game.
     *
     * @param monster            The monster initiating the attack.
     * @param player             The player being attacked.
     * @param newPositionMonster The intended new position of the monster.
     * @param newPositionPlayer  The intended new position of the player.
     * @return True if the monster's attack collides with the player; otherwise,
     *         false.
     */
    private boolean checkMonsterAttack(Monster monster, Player player, Vector2D newPositionMonster,
            Vector2D newPositionPlayer) {
        Rectangle playerHitbox = getPlayerHitbox(player, newPositionPlayer);
        Rectangle monsterSwordHitbox = getSwordHitboxMonster(monster);

        return monsterSwordHitbox != null && playerHitbox != null && monsterSwordHitbox.intersects(playerHitbox);
    }

    /**
     * @brief Handles the attack behavior of the player towards a monster.
     *
     *        This method manages the attack behavior of the player towards a
     *        monster. It
     *        includes tracking attack cooldown and damage cooldown, checking for
     *        valid attack
     *        conditions, and applying damage to the monster if a successful attack
     *        is detected.
     *        The method also ensures that the attack and damage cooldowns are
     *        properly managed.
     *
     * @param player             The player character initiating the attack.
     * @param monster            The monster being attacked.
     * @param newPositionPlayer  The intended new position of the player.
     * @param newPositionMonster The intended new position of the monster.
     */
    private void handlePlayerAttack(Player player, Monster monster, Vector2D newPositionPlayer,
            Vector2D newPositionMonster) {
        attackCooldown++;
        damageCooldown++;

        if (currentState != EntityState.HITSTUN) {
            if (getSwordHitbox(player) != null) {
                if (player.isAttacking() && !player.isBeingHit() && !monster.isBlocking() && !monster.isDodging()
                        && !monster.isAttacking()) {
                    monster.getDamage();
                    if (attackCooldown >= 300) {
                        player.stopAttacking();
                    }
                    if (damageCooldown >= 300) {
                        monster.stopGettingDamage();
                    }
                }
            }
        }
    }

    /**
     * @brief Handles the attack behavior of a monster towards the player.
     *
     *        This method manages the attack behavior of a monster towards the
     *        player. It
     *        includes tracking attack cooldown and damage cooldown, checking for
     *        valid attack
     *        conditions, and applying damage to the player if a successful attack
     *        is detected.
     *        The method also ensures that the attack and damage cooldowns are
     *        properly managed.
     *
     * @param monster            The monster initiating the attack.
     * @param player             The player character being attacked.
     * @param newPositionMonster The intended new position of the monster.
     * @param newPositionPlayer  The intended new position of the player.
     */
    private void handleMonsterAttack(Monster monster, Player player, Vector2D newPositionMonster,
            Vector2D newPositionPlayer) {
        attackCooldown++;
        damageCooldown++;

        if (currentStateMonster != EntityState.HITSTUN) {
            if (getSwordHitboxMonster(monster) != null) {
                if (monster.isAttacking() && !monster.isBeingHit() && !player.isBlocking() && !player.isDodging()
                        && !player.isAttacking()) {
                    player.getDamage();
                    if (attackCooldown >= 300) {
                        monster.stopAttacking();
                    }
                    if (damageCooldown >= 300) {
                        player.stopGettingDamage();
                    }
                }
            }
        }
    }

    /**
     * @brief Checks for collisions between two entities.
     *
     *        This method determines if a collision occurs between two entities,
     *        each
     *        represented by an entity object, their respective hitboxes, and
     *        intended
     *        new positions. The collision is detected by checking the intersection
     *        of
     *        the hitboxes of the two entities. If a collision is detected and the
     *        first
     *        entity is currently dodging, it stops dodging and adjusts its position
     *        to
     *        avoid the collision.
     *
     * @param entity1      The first entity involved in the collision.
     * @param entity2      The second entity involved in the collision.
     * @param newPosition1 The intended new position of the first entity.
     * @param newPosition2 The intended new position of the second entity.
     * @return True if a collision is detected and handled, indicating that the
     *         entities cannot move to their new positions; otherwise, false.
     */
    private boolean checkCollisionWithEntities(Entity entity1, Entity entity2, Vector2D newPosition1,
            Vector2D newPosition2) {
        Rectangle entityrect1 = Entity.isMonster(entity1) ? getMonsterHitbox(entity1, newPosition1)
                : getPlayerHitbox(entity1, newPosition1);
        Rectangle entityrect2 = Entity.isMonster(entity2) ? getMonsterHitbox(entity2, newPosition2)
                : getPlayerHitbox(entity2, newPosition2);

        boolean collisionDetected = entityrect1.intersects(entityrect2);

        if (collisionDetected && entity1.isDodging()) {
            entity1.stopDodging();
            if (entity1.isFacingLeft()) {
                entity1.move(10, 0);
            } else {
                entity1.move(-10, 0);
            }
            entitiesCollision = true;
        } else {
            entitiesCollision = collisionDetected;
        }

        return entitiesCollision;
    }

}
