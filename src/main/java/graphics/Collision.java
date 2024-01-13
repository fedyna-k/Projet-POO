package graphics;

import character.Entity;
import character.Entity.EntityState;
import character.Monster;
import character.Player;
import geometry.Vector2D;
import map.Map;

import java.awt.Rectangle;
import java.util.ArrayList;
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
    private static Map map = new Map("resources/map/");

    /**
     * @brief The current state of the player entity.
     */
    private static EntityState currentState;

    /**
     * @brief The current state of the monster entity.
     */
    private static EntityState currentStateMonster;

    /**
     * @brief Flag indicating whether entities are colliding.
     */
    static boolean entitiesCollision = false;

    /**
     * @brief The scaling factor for collision calculations.
     */
    static int SCALE = 2;

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
        int rectWidth = (int) (64 * SCALE / 2.25);
        int rectHeight = (int) (64 * SCALE / 1.6);
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
        int rectWidth = (int) (64 * SCALE / 2.25);
        int rectHeight = (int) (64 * SCALE / 1.6);
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
    public static boolean checkCollision(Entity entity, Vector2D newPosition, ArrayList<Entity> others) {

        int tileSize = map.getTileSize() * SCALE;

        Rectangle rect = Entity.isMonster(entity) ? getMonsterHitbox(entity, newPosition)
                : getPlayerHitbox(entity, newPosition);


        int topLeftIndexX = ((int)newPosition.x - tileSize) / tileSize;        
        int topLeftIndexY = ((int)newPosition.y - tileSize) / tileSize;

        // Check collision with walls
        for (int i = topLeftIndexX; i < topLeftIndexX + 3; i++) {
            for (int j = topLeftIndexY; j < topLeftIndexY + 3; j++) {
                if (map.isWall(i, j)) {
                    Rectangle tileRect = getTileHitbox(i, j, tileSize);

                    if (rect.intersects(tileRect)) {
                        return true;
                    }
                }
            }
        }

        if (others != null) {
            for (Entity other : others) {
                if (other.equals(entity)) {
                    continue;
                }

                Rectangle otherHitbox = Entity.isMonster(other)
                    ? getMonsterHitbox(other, other.getPosition())
                    : getPlayerHitbox(other, other.getPosition());
    
                if (!entity.isDodging() && rect.intersects(otherHitbox)) {

                    return true;
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
        if (currentState != EntityState.HITSTUN) {
            if (getSwordHitbox(player) != null) {
                if (player.isAttacking() && !player.isBeingHit() && !monster.isBlocking() && !monster.isDodging()
                        && !monster.isAttacking()) {
                    
                    monster.getDamage(player);
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
        if (currentStateMonster != EntityState.HITSTUN) {
            if (getSwordHitboxMonster(monster) != null) {
                if (monster.isAttacking() && !monster.isBeingHit() && !player.isDodging() && !player.isAttacking()) {

                    player.getDamage(monster);
                }
            }
        }
    }
}