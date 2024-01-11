/**
 * @brief This file contains the public class Crystal.
 *
 * @file Crystal.java
 * @author ARAB Ryan
 * @date 11/01/2024
 *
 * Part of the `character` package.
 * It contains a class that represents the crystal.
 */

package character;

import geometry.Vector2D;

/**
 * @class Crystal
 * @author ARAB Ryan
 * @date 11/01/2024
 *
 * @brief This class represents the crystal. It extends Entity.
 *
 * @see character.Entity
 */
public class Crystal extends Entity {
    /**
     * @brief Main constructor.
     *
     * Create a Crystal instance at coordinates (x, y).
     *
     * @param x The starting x coordinate.
     * @param y The starting y coordinate.
     */
    public Crystal(double x, double y, String color) {
        if (color.equals("blue")) {
            this.setAnimations("crystal/", "blue");
            this.coordinates = new Vector2D(x, y);
            this.isFacingLeft = false;
        }

        if (color.equals("purple")) {
            this.setAnimations("crystal/", "purple");
            this.coordinates = new Vector2D(x, y);
            this.isFacingLeft = false;
        }
    }

    /**
     * @brief Gets the offset for the crystal.
     *
     * @return A Vector2D instance, currently returning null.
     * It can be overridden to provide specific offset logic for the crystal.
     */
    @Override
    public Vector2D getOffset() {
         return new Vector2D(0,0 );
    }
}
