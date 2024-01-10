/**
 * @brief This file contains the public class Player.
 * 
 * @file Player.java
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * Part of the `character` package.
 * It contains a class that represents the player.
 */

package character;

import geometry.Vector2D;

/**
 * @class Player
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * @brief This class represents the player. It extends Entity.
 * 
 * @see character.Entity
 */
public class Player extends Entity {
    /**
     * @brief Default constructor.
     * 
     * Create a Player instance at coordinates (0, 0).
     */
    public Player() {
        this(0, 0);
    }

    /**
     * @brief Main constructor.
     * 
     * Create a Player instance at coordinates (x, y).
     * 
     * @param x The starting x coordinate.
     * @param y The starting y corrdinate.
     */
    public Player(double x, double y) {
        this.setAnimations("player/");
        this.coordinates = new Vector2D(x, y);
        this.isFacingLeft = false;
        this.stats = new EntityStats(100, 100, 10, 1, 1, 1);
    }

    /**
     * @brief Gets the offset depending on animation.
     * @return A Vector2D that will be added when image is drawn.
     */
    public Vector2D getOffset() {
        if (isAttacking) {
            return new Vector2D(isFacingLeft ? 16 : -16, 0);
        }

        return new Vector2D();
    }

}
