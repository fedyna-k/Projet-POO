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



    /*
        // Constructor to initialize a mage with a given name, hit points, and force
        public mage(String name, int lifePoints, int force) {
            this.name = name;
            setLifePoints(lifePoints); // Using the method from the Entity class to initialize hit points
            setForce(force); // Using the method from the Entity class to initialize force
        }

        // Method to simulate the mage's attack
        public void attack() {
            System.out.println("The mage attacks with a force of " + getForce());
        }

        // Method to simulate the damage received by the mage and manage its state accordingly
        public void receiveDamage(int damage) {
            int remainingLifePoints = getLifePoints() - damage;
            if (remainingLifePoints <= 0) {
                setLifePoints(0); // If the damage exceeds the hit points, the hit points are set to 0
                System.out.println("The mage is defeated!");
            } else {
                setLifePoints(remainingLifePoints); // Reduce the hit points based on the received damage
                System.out.println("The mage now has " + getLifePoints() + " hit points");
            }
        }
    */
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
