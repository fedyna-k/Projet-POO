/**
 * @brief This file contains the public class EntityStat.
 * 
 * @file EntityStat.java
 * @author Kevin Fedyna
 * @author Imene Bousmaha
 * @date 9/1/2024
 * 
 * Part of the `character` package.
 * It contains stats for all entities such as max health, speed, attack damage, etc...
 */

package character;
import geometry.Range;

/**
 * @class Entity
 * @author Kevin Fedyna
 * @author Imene Bousmaha
 * @date 9/1/2024
 * 
 * @brief This class represents the entity stats such as health, speed, etc...
 */
public class EntityStats {
    /** @brief UP constant for stat upgrade */
    static private final double UP = 1;
    /** @brief DOWN constant for stat upgrade */
    static private final double DOWN = 0.25;

    /** @brief The entity health, if at 0, it dies */
    private Range health;
    /** @brief The entity ether, used for spells */
    private Range ether;
    /** @brief The speed used for movement and attack */
    private double speed;
    /** @brief The attack changes the amount of damage dealt by sword */
    private double attack;
    /** @brief The power changes the amount of damage dealt by spells */
    private double power;
    /** @brief The defence reduces the amout of damage taken */
    private double defence;

    /**
     * @brief Creates a new EntityStats with given stats
     * @param health The entity max health 
     * @param ether The entity max ether
     * @param speed The entity speed and attack speed
     * @param attack The entity physical damage
     * @param power The entity magical damage
     * @param defence The entity defence
     */
    public EntityStats(int health, int ether, double speed, double attack, double power, double defence) {
        this.health = new Range(health);
        this.ether = new Range(ether);
        this.speed = speed;
        this.attack = attack;
        this.power = power;
        this.defence = defence;
    }

    /**
     * @brief Getter for health
     * @return health
     */
    public Range getHealth() {
        return health;
    }

    /**
     * @brief Getter for ether
     * @return ether
     */
    public Range getEther() {
        return ether;
    }

    /**
     * @brief Getter for speed
     * @return speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @brief Getter for attack
     * @return attack
     */
    public double getAttack() {
        return attack;
    }

    /**
     * @brief Getter for power
     * @return power
     */
    public double getPower() {
        return power;
    }

    /**
     * @brief Getter for defence
     * @return defence
     */
    public double getDefence() {
        return defence;
    }

    /**
     * @brief Sets the entity new max health and keep the old health points.
     * @param newMax The new max health
     */
    public void setMaxHealth(int newMax) {
        int current = health.get();
        health = new Range(newMax);
        health.set(current);
    }

    /**
     * @brief Deal damage to entity
     * @param amount The amount of damage
     */
    public void takeDamage(int amount) {
        amount = Math.max(amount, 0);
        health.set(health.get() - amount);
    }

    /**
     * @brief Heal the entity
     * @param amount The amount of heal
     */
    public void heal(int amount) {
        health.set(health.get() + amount);
    }

    /**
     * @brief Tells if the entity's health is at 0
     * @return true if the entity is dead
     */
    public boolean isDead() {
        return health.isMin();
    }

    /**
     * @brief Sets the entity new max ether and keep the old ether points.
     * @param newMax The new max ether
     */
    public void setMaxEther(int newMax) {
        int current = ether.get();
        ether = new Range(newMax);
        ether.set(current);
    }

    /**
     * @brief Use ether points
     * @param amount The amount to use
     */
    public void spendEther(int amount) {
        ether.set(ether.get() - amount);
    }    

    /**
     * @brief Gain ether points
     * @param amount The amount to gain
     */
    public void retrieveEther(int amount) {
        ether.set(ether.get() + amount);
    }

    /**
     * @brief Checks if ether points are available
     * @param amount The amount to check
     * @return true if there is enough ether points
     */
    public boolean hasEtherFor(int amount) {
        return amount < ether.get();
    }

    /**
     * @brief Upgrade the speed and downgrade other stats.
     */
    public void upgradeSpeed() {
        speed += UP;
        attack -= DOWN;
        power -= DOWN;
        defence -= DOWN;

        setMaxHealth(70 + (int)(10 * defence));
    }

    /**
     * @brief Upgrade the attack and downgrade other stats.
     */
    public void upgradeAttack() {
        attack += UP;
        speed -= DOWN;
        power -= DOWN;
        defence -= DOWN;

        setMaxHealth(70 + (int)(10 * defence));
    }

    /**
     * @brief Upgrade the power and downgrade other stats.
     */
    public void upgradePower() {
        power += UP;
        attack -= DOWN;
        speed -= DOWN;
        defence -= DOWN;

        setMaxHealth(70 + (int)(10 * defence));
    }

    /**
     * @brief Upgrade the defence and downgrade other stats.
     */
    public void upgradeDefence() {
        defence += UP;
        attack -= DOWN;
        power -= DOWN;
        speed -= DOWN;

        setMaxHealth(70 + (int)(10 * defence));
    }

    /**
     * @brief Helper function that computes the damage to deal based on attack/power - defence
     * @param attack The attack/power stat
     * @param defence The defence stat
     * @return The damage to deal to the entity that defends
     */
    static public int computeDamage(double attack, double defence) {
        double result = 25 * (attack - defence) / (defence + 1) + 15;
        return (int)Math.floor(result);
    }
}