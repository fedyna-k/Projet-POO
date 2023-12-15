package graphics;

import character.Entity;
import character.Entity.EntityState;
import character.Monster;
import character.Player;
import geometry.Vector2D;
import map.Map;

import java.awt.Rectangle;
import java.util.Random;

/**
 * @brief Represents a collision handling utility.
 *
 *        This class provides methods for handling collisions between entities
 *        in the game.
 */
public class Collision {
    /**
     * @brief The map used for collision detection.
     */
    private static Map map = new Map("../src/main/resources/map/");

    /**
     * @brief The current state of the player entity.
     */
    private static EntityState currentState;

    /**
     * @brief The current state of the monster entity.
     */
    private static EntityState currentStateMonster;

    /**
     * @brief The cooldown for player attacks.
     */
    private static int attackCooldown = 0;

    /**
     * @brief The cooldown for damage application.
     */
    private static int damageCooldown = 0;

    /**
     * @brief The cooldown for player attacks.
     */
    private static int monsterattackCooldown = 0;

    /**
     * @brief The cooldown for damage application.
     */
    private static int monsterdamageCooldown = 0;

    /**
     * @brief Flag indicating whether entities are colliding.
     */
    static boolean entitiesCollision = false;

    /**
     * @brief The scaling factor for collision calculations.
     */
    static int SCALE = 4;

    /**
     * @brief A random number generator for various uses.
     */
    static Random random = new Random();

    /**
     * @brief Retrieves the hitbox for the player at the specified position.
     *
     *        This method calculates and returns the hitbox for the player based on
     *        the given position.
     *
     * @param entity      The player entity.
     * @param newPosition The position at which to calculate the player's hitbox.
     * @return A Rectangle representing the player's hitbox at the specified
     *         position.
     */
    public static Rectangle getPlayerHitbox(Entity entity, Vector2D newPosition) {
        int rectWidth = (int) (64 * SCALE / 4.5);
        int rectHeight = (int) (64 * SCALE / 3.2);
        return new Rectangle((int) newPosition.x, (int) newPosition.y, rectWidth, rectHeight);
    }

    /**
     * @brief Retrieves the hitbox for the monster at the specified position.
     *
     *        This method calculates and returns the hitbox for the monster based on
     *        the given position.
     *
     * @param entity      The monster entity.
     * @param newPosition The position at which to calculate the monster's hitbox.
     * @return A Rectangle representing the monster's hitbox at the specified
     *         position.
     */
    public static Rectangle getMonsterHitbox(Entity entity, Vector2D newPosition) {
        int rectWidth = (int) (64 * SCALE / 4.5);
        int rectHeight = (int) (64 * SCALE / 3.2);
        return new Rectangle((int) newPosition.x, (int) newPosition.y, rectWidth, rectHeight);
    }

    /**
     * @brief Retrieves the hitbox for the entity's sword during an attack.
     *
     *        This method calculates and returns the hitbox for the entity's sword
     *        during an attack. The hitbox dimensions are determined by scaling the
     *        entity's sprite and positioning the sword based on the entity's facing
     *        direction.
     *
     * @param entity The entity initiating the attack.
     * @return A Rectangle representing the hitbox of the entity's sword during an
     *         attack, or null if the entity is not currently attacking.
     */
    public static Rectangle getSwordHitbox(Entity entity) {

        Vector2D offset = entity.getOffset();
        double directionMultiplier = entity.isFacingLeft() ? -1 : 1;
        int spriteWidth = entity.getSprite().getWidth();

        int swordHeight = (int) (entity.getSprite().getHeight() * SCALE / 4);

        if (entity.isAttacking()) {
            double centerswordY = entity.getPosition().y - offset.y * SCALE + 10;
            double centerswordX = entity.getPosition().x - offset.x + directionMultiplier * 64;
            int swordWidth = (int) (spriteWidth);
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
    public static Rectangle getSwordHitboxMonster(Entity entity) {

        Vector2D offset = entity.getOffset();
        double directionMultiplier = entity.isFacingLeft() ? -1 : 1;
        int spriteWidth = 64;

        int swordHeight = (int) (entity.getSprite().getHeight() * SCALE / 4);

        if (entity.isAttacking()) {
            double centerswordY = entity.getPosition().y - offset.y * SCALE + 10;
            double centerswordX = entity.getPosition().x - offset.x + directionMultiplier * 45;
            int swordWidth = (int) (spriteWidth / 2);
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
    public static Rectangle getTileHitbox(int i, int j, int tileSize) {
        int tileX = i * tileSize;
        int tileY = j * tileSize;
        return new Rectangle(tileX, tileY, tileSize, tileSize);
    }

    /**
     * @brief Checks for collisions of an entity with walls and other entities.
     *
     *        This method determines if an entity, represented by its hitbox at the
     *        specified new position, collides with walls on the game map or other
     *        entities.
     *        It considers the entity type (player or monster) to calculate the
     *        correct hitbox.
     *        The method iterates through the map's tiles, checking for collisions
     *        with walls
     *        using the checkCollisionWithWalls function.
     *
     * @param entity      The entity for which to check collisions.
     * @param newPosition The intended new position of the entity.
     * @return True if a collision is detected with walls or other entities;
     *         otherwise, false.
     */
    public static boolean checkCollision(Entity entity, Vector2D newPosition) {

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
     * @brief Checks for collisions between the player and a wall tile.
     *
     *        This method determines if a collision occurs between the player and a
     *        wall tile, represented by the given player and tile rectangles. If a
     *        collision is detected and the player is currently dodging, the
     *        player's position is reset to the previous valid coordinates, and the
     *        dodging state is reset.
     *
     * @param entityRect  The rectangle representing the hitbox of the entity.
     * @param tileRect    The rectangle representing the hitbox of the wall tile.
     * @param entity      The entity object involved in the collision.
     * @param newPosition The intended new position of the player.
     * @return True if a collision is detected and handled, indicating the player
     *         cannot move to the new position; otherwise, false.
     */
    public static boolean checkCollisionWithWalls(Rectangle entityRect, Rectangle tileRect, Entity entity,
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
    public static boolean checkCollisionWithEntities(Entity entity1, Entity entity2, Vector2D newPosition1,
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

    /**
     * @brief Checks if the player's attack collides with a monster.
     *
     *        This method determines if the player's sword, represented by its
     *        hitbox,
     *        intersects with the hitbox of a monster. It is used to check if the
     *        player's attack successfully collides with a monster during the game.
     *
     * @param player             The player initiating the attack.
     * @param monster            The monster being attacked.
     * @param newPositionPlayer  The intended new position of the player.
     * @param newPositionMonster The intended new position of the monster.
     * @return True if the player's attack collides with the monster; otherwise,
     *         false.
     */
    public static boolean checkPlayerAttack(Player player, Monster monster, Vector2D newPositionPlayer,
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
     *        monster's attack successfully collides with the player during the
     *        game.
     *
     * @param monster            The monster initiating the attack.
     * @param player             The player being attacked.
     * @param newPositionMonster The intended new position of the monster.
     * @param newPositionPlayer  The intended new position of the player.
     * @return True if the monster's attack collides with the player; otherwise,
     *         false.
     */
    public static boolean checkMonsterAttack(Monster monster, Player player, Vector2D newPositionMonster,
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
    public static void handlePlayerAttack(Player player, Monster monster, Vector2D newPositionPlayer,
            Vector2D newPositionMonster) {
        attackCooldown++;
        damageCooldown++;

        if (currentState != EntityState.HITSTUN) {
            if (getSwordHitbox(player) != null) {
                if (player.isAttacking() && !player.isBeingHit() && !monster.isBlocking() && !monster.isDodging()
                        && !monster.isAttacking()) {
                    monster.getDamage();
                    if (attackCooldown >= 60) {
                        player.stopAttacking();
                        attackCooldown = 0;
                    }
                    if (damageCooldown >= 60) {
                        monster.stopGettingDamage();
                        damageCooldown = 0;
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
    public static void handleMonsterAttack(Monster monster, Player player, Vector2D newPositionMonster,
            Vector2D newPositionPlayer) {
        monsterattackCooldown++;
        monsterdamageCooldown++;

        if (currentStateMonster != EntityState.HITSTUN) {
            if (getSwordHitboxMonster(monster) != null) {
                if (monster.isAttacking() && !monster.isBeingHit() && !player.isBlocking() && !player.isDodging()
                        && !player.isAttacking()) {
                    player.getDamage();
                    if (monsterattackCooldown >= 60) {
                        monster.stopAttacking();
                        monsterattackCooldown = 0;
                    }
                    if (monsterdamageCooldown >= 60) {
                        player.stopGettingDamage();
                        monsterdamageCooldown = 0;
                    }
                }
            }
        }
    }
}