package character;

import geometry.Vector2D;

/**
 * @brief Represents a Monster entity in the game.
 *
 *        This class extends the Entity class and defines additional behaviors
 *        specific to
 *        Monster entities, such as random movement and attacking.
 */
public class Monster extends Entity {

    /**
     * @brief Default constructor for Monster.
     *
     *        Initializes a Monster at the origin (0, 0).
     */
    public Monster() {
        this(0, 0);
    }

    /**
     * @brief Constructor for Monster with specified coordinates.
     *
     *        Initializes a Monster at the specified coordinates with default
     *        animations
     *        and facing direction.
     *
     * @param x      The x-coordinate of the Monster.
     * @param y      The y-coordinate of the Monster.
     */
    public Monster(double x, double y) {
        this.setAnimations("monster/");
        this.coordinates = new Vector2D(x, y);
        this.isFacingLeft = false;
        this.stats = new EntityStats(100, 100, 1, 1, 1, 1);
    }


    /**
     * @brief Gets the offset for rendering based on the attack state.
     *
     *        If the Monster is attacking, the offset is adjusted based on its
     *        facing
     *        direction.
     *
     * @return The offset vector for rendering.
     */
    public Vector2D getOffset() {
        if (isAttacking) {
            return new Vector2D(isFacingLeft ? 0 : 0, 0);
        }

        return new Vector2D();
    }

    /**
     * @brief Performs random movement for the Monster.
     *
     *        Generates a random movement vector and applies it to the Monster's
     *        position.
     */
    public void randMovement() {
        double randomX = Math.random() - 0.5;
        double randomY = Math.random() - 0.5;

        Vector2D delta = new Vector2D(randomX * 0.25, randomY * 0.25);
        Vector2D randomMovement = Vector2D.add(bufferedMovement, delta);
        
        move(randomMovement, 0.5);
    }

    private static double attackCooldownTimer;

    /**
     * @brief Tries to perform an attack based on specified conditions.
     *
     *        This method attempts to perform an attack for a Monster towards a
     *        player based on
     *        specified conditions such as attack radius, attack probability, and
     *        cooldown.
     *
     * @param monster           The Monster attempting to attack.
     * @param player            The player character being attacked.
     * @param difference        The vector representing the difference in positions.
     * @param attackProbability The probability of a successful attack.
     * @param cooldown          The cooldown period for the attack.
     */
    public static void tryAttack(Monster monster, Player player, Vector2D difference, double attackProbability,
            double cooldown) {
        double attackRadius = 150.0;

        if (attackCooldownTimer <= 0 && difference.norm() <= attackRadius) {
            double randomValue = Math.random() * 100;
            if (randomValue < attackProbability) {
                monster.attack();
                attackCooldownTimer = cooldown;
            }
        }
        if (attackCooldownTimer > 0) {
            attackCooldownTimer--;
        }
    }
}
