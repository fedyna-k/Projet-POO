package character;

import geometry.Vector2D;

// Class representing a character : mage in the game
public class Mage extends Entity {

    /**
     * Default constructor for Mage. Initializes the Mage at the origin (0, 0).
     */
    public Mage() {
        this(0, 0);
    }

    /**
     * Constructor to create a Mage with specified coordinates.
     * Initializes Mage animations and sets its position.
     *
     * @param x The x-coordinate of the Mage.
     * @param y The y-coordinate of the Mage.
     */
    public Mage(double x, double y) {
        this.setAnimations("mage/");
        this.coordinates = new Vector2D(x, y);
        this.isFacingLeft = false;
    }

    /**
     * Abstract method to implement to get the offset of the mage.
     * Adjusts the offset based on whether the mage is attacking and facing left.
     *
     * @return A Vector2D representing the offset of the mage.
     */
    public Vector2D getOffset() {
        if (isAttacking && isFacingLeft) {
            return new Vector2D(-32, 0);
        }

        return new Vector2D();
    }
}
