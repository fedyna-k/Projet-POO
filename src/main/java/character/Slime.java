package character;

import geometry.Vector2D;

// Class representing a small Slime monster in the game
public class Slime extends Entity {
    // Default name of the Slime
    private String name = "Poisonous slime";

    // Constructor to initialize a Slime with a given name, hit points, and force
    public Slime(String name, int hitPoints, int force) {
        this.name = name;
        setHitPoints(hitPoints); // Using the method from the Entity class to initialize hit points
        setForce(force); // Using the method from the Entity class to initialize force
    }

    // Method to simulate the Slime's attack
    public void attack() {
        System.out.println("The Slime attacks with a force of " + getForce());
    }

    // Method to simulate the damage received by the Slime and manage its state accordingly
    public void receiveDamage(int damage) {
        int remainingHitPoints = getHitPoints() - damage;
        if (remainingHitPoints <= 0) {
            setHitPoints(0); // If the damage exceeds the hit points, the hit points are set to 0
            System.out.println("The Slime is defeated!");
        } else {
            setHitPoints(remainingHitPoints); // Reduce the hit points based on the received damage
            System.out.println("The Slime now has " + getHitPoints() + " hit points");
        }
    }

    // Abstract method to implement to get the offset of the Slime
    @Override
    public Vector2D getOffset() {
        return null;
    }
}
