package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import java.awt.Toolkit;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

import character.Player;
import character.Entity;
import character.Monster;
import geometry.Vector2D;
import map.Map;

public class Canvas extends JPanel {
    private boolean isFullscreen;
    private Timer timer;
    private Camera camera;

    // TESTING PURPOSE
    private Player player;
    private Monster badguy;
    private KeyStack stack;
    private boolean wasReleasedO;
    private boolean wasReleasedSpace;
    private boolean wasReleasedI;

    private Map map;

    // ---------------

    public Canvas() {
        this(false);
    }

    public Canvas(boolean isFullscreen) {
        super(true);
        this.isFullscreen = isFullscreen;

        this.camera = Camera.getCamera(this);
        setBackground(new Color(42, 42, 42, 255));

        // TESTING PURPOSE
        this.player = new Player(0, 0);
        this.badguy = new Monster(100, 300);
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

        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
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
                    if (wasReleasedO && !player.isDodging() && !player.isBlocking()) {
                        player.attack();
                        wasReleasedO = true;
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

                repaint();

                // collision
                Vector2D playerNewPosition = Vector2D.add(player.getPosition(), movement);

                if (!checkCollision(player, playerNewPosition)) {
                    player.move(movement);
                }

                if (checkCollisionWithEntities(player, badguy, playerNewPosition)) {
                    if (player.isAttacking() && !badguy.isBlocking()) {
                        System.out.println("Collision detected during attack");
                        badguy.getDamage();
                    } else if (badguy.isAttacking() && !player.isBlocking()) {
                        player.getDamage();
                    }
                }

                // ---------------

                // movement monster & aggro
                Vector2D difference = Vector2D.subtract(player.getPosition(), badguy.getPosition());
                Vector2D newPositionMonster;

                double aggroRange = 500;

                if (difference.norm() < aggroRange) {
                    difference.normalize();
                    newPositionMonster = Vector2D.add(badguy.getPosition(), Vector2D.scale(difference, 3));
                } else {
                    // Generate a new random movement
                    newPositionMonster = badguy.RandomMovement(badguy.getPosition());
                }

                // Check for collision
                if (!checkCollision(badguy, newPositionMonster)) {
                    badguy.move(Vector2D.subtract(newPositionMonster, badguy.getPosition()));
                }

            }
        });

        timer.addActionListener(e -> repaint());
        timer.start();
    }

    @Override
    public Dimension getPreferredSize() {
        if (isFullscreen) {
            return Toolkit.getDefaultToolkit().getScreenSize();
        } else {
            return new Dimension(800, 600);
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // TESTING PURPOSE
        // g.setColor(new Color(56, 56, 56));
        // for (int i = -this.getWidth() / 256 ; i < 3 * this.getWidth() / 256 ; i ++) {
        // for (int j = -this.getHeight() / 256 ; j < 3 * this.getHeight() / 256 ; j++)
        // {
        // if ((i + j) % 2 == 0) {
        // g.fillRect(i * 128 - (int)this.player.getPosition().x, j * 128 -
        // (int)this.player.getPosition().y, 128, 128);
        // }
        // }
        // }

        int SCALE = isFullscreen ? 4 : 2;
        int tileSize = map.getTileSize() * SCALE;
        Vector2D movement = new Vector2D();
        Vector2D newPositionPlayer = Vector2D.add(player.getPosition(), movement);
        Vector2D newPositionMonster = Vector2D.add(badguy.getPosition(), movement);

        for (int i = (int) this.player.getPosition().x / (32 * SCALE) - 9; i < (int) this.player.getPosition().x
                / (32 * SCALE) + 10; i++) {
            for (int j = (int) this.player.getPosition().y / (32 * SCALE) - 6; j < (int) this.player.getPosition().y
                    / (32 * SCALE) + 7; j++) {
                BufferedImage tile = map.getTile(i, j);

                if (tile != null) {
                    this.camera.drawImage(g, map.getTile(i, j), i * 32 * SCALE, j * 32 * SCALE, SCALE);
                }
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
            camera.drawRect(g, playerHitbox.getX(), playerHitbox.getY(),
                    (int) playerHitbox.getWidth(), (int) playerHitbox.getHeight(), Color.RED);
        }

        // sword hitbox for the player
        Rectangle swordHitboxPlayer = getSwordHitbox(player);
        if (swordHitboxPlayer != null) {
            camera.drawRect(g, swordHitboxPlayer.getX(), swordHitboxPlayer.getY(),
                    (int) swordHitboxPlayer.getWidth(), (int) swordHitboxPlayer.getHeight(), Color.RED);
        }

        // hitbox bad guy
        Rectangle monsterHitbox = getMonsterHitbox(badguy, newPositionMonster);
        if (monsterHitbox != null) {
            camera.drawRect(g, monsterHitbox.getX(), monsterHitbox.getY(),
                    (int) monsterHitbox.getWidth(), (int) monsterHitbox.getHeight(), Color.RED);
        }

        // hitbox walls
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                // draw rectangle around walls
                if (map.isWall(i, j)) {
                    Rectangle tileHitbox = getTileHitbox(i, j, tileSize);
                    if (tileHitbox != null) {
                        camera.drawRect(g, tileHitbox.getX(), tileHitbox.getY(),
                                (int) tileHitbox.getWidth(), (int) tileHitbox.getHeight(), Color.RED);
                    }
                }
            }
        }

        /* End of Drawing Hitbox */

        // this.camera.showCam(g, badguy, player);
        // ---------------
    }

    /**
     * Get Canvas center point
     * 
     * @return A Vector2D containing the point
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

    private Rectangle getMonsterHitbox(Entity entity, Vector2D newPosition) {
        int SCALE = isFullscreen ? 4 : 2;
        int rectWidth = (int) (badguy.getSpriteSize().x * SCALE / 1.9);
        int rectHeight = (int) (badguy.getSpriteSize().y * SCALE / 1.5);
        return new Rectangle((int) newPosition.x, (int) newPosition.y, rectWidth, rectHeight);
    }

    private Rectangle getSwordHitbox(Entity entity) {
        int SCALE = isFullscreen ? 4 : 2;
        Vector2D offset = entity.getOffset();
        double centerswordX;
        double centerswordY = entity.getPosition().y - offset.y * SCALE + 30;

        int spriteWidth = entity.getSprite().getWidth();

        int swordHeight = (int) (entity.getSprite().getHeight() * SCALE / 2);

        if (entity.isAttacking()) {
            double directionMultiplier = entity.isFacingLeft() ? -1 : 1;
            centerswordX = entity.getPosition().x - offset.x * SCALE + directionMultiplier * 96;
            int swordWidth = (int) (spriteWidth * 2);

            return new Rectangle((int) centerswordX, (int) centerswordY, swordWidth, swordHeight);
        }

        return null;
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
                entity.move(-10, 0);
                return true;
            }
            return true;
        }
        return false;
    }

    private boolean checkCollision(Entity entity, Vector2D newPosition) {
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

    private boolean checkCollisionWithEntities(Entity entity1, Entity entity2, Vector2D newPosition) {
        Rectangle rect1 = Entity.isMonster(entity1) ? getMonsterHitbox(entity1, newPosition)
                : getPlayerHitbox(entity1, newPosition);
        Rectangle rect2 = Entity.isMonster(entity2) ? getMonsterHitbox(entity2, newPosition)
                : getPlayerHitbox(entity2, newPosition);

        Rectangle swordEntity1 = getSwordHitbox(entity1);
        Rectangle swordEntity2 = getSwordHitbox(entity2);

        if ((swordEntity1 != null && rect2 != null && swordEntity1.intersects(rect2)) ||
                (swordEntity2 != null && rect1 != null && swordEntity2.intersects(rect1))) {
            return true;
        }

        return rect1 != null && rect2 != null && rect1.intersects(rect2);
    }

    // private void preventOverlap(Entity entity1, Entity entity2, Rectangle rect1,
    // Rectangle rect2) {
    // double overlapX = (rect1.width + rect2.width) / 2.0 -
    // Math.abs(rect1.getCenterX() - rect2.getCenterX());
    // double overlapY = (rect1.height + rect2.height) / 2.0 -
    // Math.abs(rect1.getCenterY() - rect2.getCenterY());

    // double signX = Math.signum(entity1.getPosition().x -
    // entity2.getPosition().x);
    // double signY = Math.signum(entity1.getPosition().y -
    // entity2.getPosition().y);

    // entity1.move(new Vector2D(signX * overlapX, signY * overlapY));
    // entity2.move(new Vector2D(-signX * overlapX, -signY * overlapY));
    // }

}
