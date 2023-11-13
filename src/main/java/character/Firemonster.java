package character;

import geometry.Vector2D;

// Class representing a small Fire monster in the game
public class Firemonster extends Entity {

    public Firemonster() {
        this(0, 0);
    }

    public Firemonster(double x, double y) {
        this.setAnimations("monster/firemonster/");  // Assurez-vous que vous avez des fichiers d'animation dans le dossier "firemonster"
        this.coordinates = new Vector2D(x, y);
    }


/*
    // Constructor to initialize a Fire monster with a given name, hit points, and force
    public FireMonster(String name, int hitPoints, int force) {
        this.name = name;
        setHitPoints(hitPoints); // Using the method from the Entity class to initialize hit points
        setForce(force); // Using the method from the Entity class to initialize force
    }

    // Method to simulate the Fire monster's attack
    public void attack() {
        System.out.println("The Firemonster attacks with a force of " + getForce());
    }

    // Method to simulate the damage received by the Firemonster and manage its state accordingly
    public void receiveDamage(int damage) {
        int remainingHitPoints = getHitPoints() - damage;
        if (remainingHitPoints <= 0) {
            setHitPoints(0); // If the damage exceeds the hit points, the hit points are set to 0
            System.out.println("The Firemonster is defeated!");
        } else {
            setHitPoints(remainingHitPoints); // Reduce the hit points based on the received damage
            System.out.println("The Firemonster now has " + getHitPoints() + " hit points");
        }
    }
*/
    // Abstract method to implement to get the offset of the Firemonster
    public Vector2D getOffset() {
        if (isAttacking && isFacingLeft) {
            return new Vector2D(-32, 0);
        }

        return new Vector2D();
    }
}
