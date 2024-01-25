/**
 * @brief This file contains the public class Entity.
 * 
 * @file Entity.java
 * @author Kevin Fedyna
 * @author Imene Bousmaha
 * @date 16/11/2023
 * 
 * Part of the `character` package.
 * It contains a class that represents an arbitrary entity.
 */

package character;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Timer;

import geometry.Vector2D;

import graphics.Animation;
import graphics.Collision;

/**
 * @class Entity
 * @author Kevin Fedyna
 * @author Imene Bousmaha
 * @date 16/11/2023
 * 
 * @brief This class represents an arbitrary entity. It should be a base for all
 *        entities.
 * 
 * @see character.Player
 */
public abstract class Entity {
    /** @brief All possible animations */
    public enum AnimationIndex {
        STANDING, LEFTRUN, RIGHTRUN, ATTACK, DODGE, BLOCK, BLOCKWALK, BLOCKSTAND, DAMAGE
    };

    // Life Points of the entity
    private int lifePoints;

    // Force of the entity
    private int force;

    // Damage taken by the entity
    private int damage;

    /** @brief The coordinates in absolute positions */
    public Vector2D coordinates;
    /** @brief The height of the entity */
    public int height;
    /** @brief The width of the entity */
    public int width;
    /** @brief The name of the entity */
    public String name;
    /** @brief The entity stats */
    protected EntityStats stats;
    /** @brief State if is attacking */
    protected boolean isAttacking;
    /** @brief State if is attacking */
    protected boolean canAttack = true;
    /** @brief State if is attacking */
    public int attackCooldown = 0;
    /** @brief State if is attacking */
    protected Timer attackTimer;
    /** @brief State if is attacking */
    protected int hitstunCooldown = 0;
    /** @brief State if is attacking */
    protected Timer hitstunTimer;
    /** @brief State if is facing left */
    protected boolean isFacingLeft;
    /** @brief State if is dodging */
    protected boolean isDodging;
    /** @brief State if is blocking */
    protected boolean isBlocking;
    public boolean isActive = true;
    /** @brief Used in block loading */
    protected boolean isInitiatingBlock;
    /** @brief Used for damage taking */
    protected boolean isBeingHit = false;

    /** @brief The last registered movement before dodging */
    protected Vector2D bufferedMovement;

    /** @brief The Animation currently playing */
    public Animation current;

    /** @brief The idle animation */
    protected Animation standing;
    /** @brief The animation when running left */
    protected Animation leftRun;
    /** @brief The animation when running right */
    protected Animation rightRun;

    /** @brief The animation when attacking left */
    protected Animation leftAttack;
    /** @brief The animation when attacking right */
    protected Animation rightAttack;

    /** @brief The animation when dodging left */
    protected Animation rightDodge;
    /** @brief The animation when dodging right */
    protected Animation leftDodge;

    protected Animation rightBlock;
    protected Animation leftBlock;
    protected Animation rightBlockStand;
    protected Animation leftBlockStand;
    protected Animation rightBlockWalk;
    protected Animation leftBlockWalk;

    protected Animation leftTakesDamage;
    protected Animation rightTakesDamage;

    /**
     * @brief Checks if the given entity is an instance of the Monster class.
     *
     *        This method determines whether the provided entity is a Monster by
     *        checking
     *        its type using the instanceof operator.
     *
     * @param entity The entity to be checked.
     * @return True if the entity is a Monster; otherwise, false.
     */
    public static boolean isMonster(Entity entity) {
        return entity instanceof Monster;
    }

    /**
     * @brief Enumeration representing the different states of an entity.
     * 
     *        This enum defines the possible states an entity can be in, such as
     *        NORMAL and HITSTUN.
     */
    public enum EntityState {
        /**
         * The normal state of an entity.
         */
        NORMAL,

        /**
         * The hit stun state of an entity.
         */
        HITSTUN,
    }

    protected EntityState currentState;

    /**
     * @brief Get the Vector2D representation of entity position.
     * @return The position vector of the entity.
     */
    public Vector2D getPosition() {
        return this.coordinates;
    }

    /**
     * @brief Use this function to return offsets depending on contexts and sprites.
     * @return A Vector2D representing offset to apply (specific to base sprite).
     */
    abstract public Vector2D getOffset();

    /**
     * @brief Move Entity by a given vector.
     * @param dx x coordinate of vector.
     * @param dy y coordinate of vector.
     * @param speed Set a custom speed to the character.
     * @param others Set of other entities that will be collided.
     */
    public void move(double dx, double dy, double speed, ArrayList<Entity> others) {
        // Dodge state setter
        if (isDodging && !current.isPlaying()) {
            isDodging = false;
        }

        // Attack state setter
        if (isAttacking && !current.isPlaying()) {
            isAttacking = false;
        }

        // Block state setter
        if (isBlocking && !current.isPlaying() && !isInitiatingBlock) {
            isBlocking = false;
        }

        // Block state setter
        if (isInitiatingBlock && !current.isPlaying()) {
            isInitiatingBlock = false;
        }

        if (isDodging) {
            // Apply default movement
            if (bufferedMovement.isNull()) {
                dx = isFacingLeft ? -1 : 1;
            } else {
                dx = bufferedMovement.x;
                dy = bufferedMovement.y;
            }
        } else if (isBlocking && !isInitiatingBlock) {
            if (dx != 0 || dy != 0) {
                swapAnimation(AnimationIndex.BLOCKWALK);
            } else {
                swapAnimation(AnimationIndex.BLOCKSTAND);
            }
        } else {
            if (dx > 0 || dx == 0 && dy != 0 && !isFacingLeft) {
                swapAnimation(AnimationIndex.RIGHTRUN);
            } else if (dx < 0 || dx == 0 && dy != 0 && isFacingLeft) {
                swapAnimation(AnimationIndex.LEFTRUN);
            } else {
                swapAnimation(AnimationIndex.STANDING);
            }
        }

        bufferedMovement = new Vector2D(dx, dy);
        bufferedMovement.normalize();
        bufferedMovement = Vector2D.scale(bufferedMovement, speed);

        Vector2D horizontalMovement = new Vector2D(coordinates.x + (isDodging ? bufferedMovement.x * 3 : bufferedMovement.x), coordinates.y);
        Vector2D verticalMovement = new Vector2D(coordinates.x, coordinates.y + (isDodging ? bufferedMovement.y * 3 : bufferedMovement.y));

        if (Collision.checkCollision(this, horizontalMovement, others)) {
            bufferedMovement.x = 0;
        }

        if (Collision.checkCollision(this, verticalMovement, others)) {
            bufferedMovement.y = 0;
        }

        this.coordinates.x += isDodging ? bufferedMovement.x * 3 : bufferedMovement.x;
        this.coordinates.y += isDodging ? bufferedMovement.y * 3 : bufferedMovement.y;
    }

    /**
     * @brief Move Entity by a given vector.
     * @param movement The [dx, dy] vector.
     * @param others Set of other entities that will be collided.
     */
    public void move(Vector2D movement, ArrayList<Entity> others) {
        this.move(movement.x, movement.y, 1, others);
    }

    /**
     * @brief Move Entity by a given vector.
     * @param movement The [dx, dy] vector.
     * @param speed Set a custom speed to the character.
     * @param others Set of other entities that will be collided.
     */
    public void move(Vector2D movement, double speed, ArrayList<Entity> others) {
        this.move(movement.x, movement.y, speed, others);
    }

    /**
     * @brief Move Entity by a given vector.
     * @param dx x coordinate of vector.
     * @param dy y coordinate of vector.
     * @param others Set of other entities that will be collided.
     */
    public void move(double dx, double dy, ArrayList<Entity> others) {
        this.move(dx, dy, 1, others);
    }

    /**
     * @brief Tells if the entity is dead or not
     * @return true if dead.
     */
    public boolean isDead() {
        return stats.isDead();
    }

    /**
     * @brief Get all entity stats
     * @return The stats
     */
    public EntityStats getStats() {
        return stats;
    }

    /**
     * @brief Stops the moving animation
     */
    public void stopMoving() {
        move(new Vector2D(0, 0), null);
        swapAnimation(AnimationIndex.STANDING);
    }

    /**
     * @brief Put the entity into attack state.
     */
    public void attack() {
        if (!this.isAttacking && !this.isDodging && this.canAttack && !this.isBeingHit) {
            attackTimer = new Timer(0, e -> {
                this.attackCooldown++;
    
                if (this.attackCooldown > (1000 - this.stats.getSpeed() * 80)) { // 50 * (1000 - this.stats.getSpeed() * 80)) {  // <-- Linux
                    attackCooldown = 0;
                    this.canAttack = true;
                    this.attackTimer.stop();
                }
            });
            isAttacking = true;
            this.canAttack = false;
            attackTimer.start();
            swapAnimation(AnimationIndex.ATTACK);
        }
    }

    /**
     * @brief Get the attacking state of the entity.
     * @return The attacking state.
     */
    public boolean isAttacking() {
        return this.isAttacking;
    }

    /**
     * @brief Put the entity into dodge state.
     */
    public void dodge() {
        if (!this.isDodging && !this.isAttacking && !isBeingHit) {
            isDodging = true;
            swapAnimation(AnimationIndex.DODGE);
        }
    }

    /**
     * @brief Get the dodging state of the entity.
     * @return The dodging state.
     */
    public boolean isDodging() {
        return this.isDodging;
    }

    /**
     * @brief Set the dodging state of the player.
     *
     *        This method sets the dodging state of the player to the specified
     *        value.
     *        When the player is in a dodging state, certain actions or behaviors
     *        may be
     *        affected in the game.
     *
     * @param dodging The new dodging state for the player.
     *                - true if the player is in a dodging state.
     *                - false if the player is not in a dodging state.
     *
     *
     */
    public void setDodging(boolean dodging) {
        isDodging = dodging;
    }

    /**
     * Goes back to normal state from dodging state
     */
    public void stopDodging() {
        isDodging = false;
        swapAnimation(AnimationIndex.STANDING);
    }

    /**
     * Put the entity into block state
     */
    public void block() {
        if (!this.isBlocking) {
            isInitiatingBlock = true;
            isBlocking = true;
            swapAnimation(AnimationIndex.BLOCK);
        }
    }

    /**
     * Get the blocking state of the entity
     * 
     * @return The blocking state
     */
    public boolean isBlocking() {
        return this.isBlocking;
    }

    /**
     * Goes back to normal state from blocking state
     */
    public void stopBlocking() {
        isBlocking = false;
    }

    /**
     * @brief Get the entity's orientation.
     * @return true if facing left.
     */
    public boolean isFacingLeft() {
        return this.isFacingLeft;
    }

    /**
     * Inflicts damage on the entity, putting it in a hit stun state.
     * This method changes the entity's state to EntityState.HITSTUN and
     * swaps its animation to a damage animation.
     */
    public void getDamage(Entity attacker) {
        if (this.isBeingHit == false) {
            this.isBeingHit = true;
            int amount = EntityStats.computeDamage(attacker.stats.getAttack(), stats.getDefence());
            this.stats.takeDamage(isBlocking ? amount / 2 : amount);

            this.hitstunTimer = new Timer(0, e -> {
                this.hitstunCooldown++;

                if (this.hitstunCooldown > 80) { // * 120) {  // <-- Linux
                    this.hitstunCooldown = 0;
                    this.isBeingHit = false;
                    this.currentState = EntityState.NORMAL;
                    this.hitstunTimer.stop();
                }
            });
            this.hitstunTimer.start();
        }

        this.currentState = EntityState.HITSTUN;
        swapAnimation(AnimationIndex.DAMAGE);
    }

    /**
     * Checks if the entity is currently in a hit stun state.
     *
     * @return True if the entity is being hit (in hit stun); otherwise, false.
     */
    public boolean isBeingHit() {
        return this.isBeingHit;
    }

    /**
     * @brief Set the position of the entity.
     * 
     * @param x The new x-coordinate.
     * @param y The new y-coordinate.
     */
    public void setPosition(double x, double y) {
        this.coordinates = new Vector2D(x, y);
    }

    /**
     * @brief Go through all basic animations and load them.
     * @param dir The folder contaning all frames.
     */
    protected void setAnimations(String dir) {
        standing = Animation.load("standing", Animation.RESOURCES_FOLDER + dir, 10);
        leftRun = Animation.load("leftrun", Animation.RESOURCES_FOLDER + dir, 10);
        rightRun = Animation.load("rightrun", Animation.RESOURCES_FOLDER + dir, 10);
        leftAttack = Animation.load("leftattack", Animation.RESOURCES_FOLDER + dir, 30);
        rightAttack = Animation.load("rightattack", Animation.RESOURCES_FOLDER + dir, 30);
        rightDodge = Animation.load("rightdodge", Animation.RESOURCES_FOLDER + dir, 20);
        leftDodge = Animation.load("leftdodge", Animation.RESOURCES_FOLDER + dir, 20);
        rightBlock = Animation.load("rightblock", Animation.RESOURCES_FOLDER + dir, 30);
        leftBlock = Animation.load("leftblock", Animation.RESOURCES_FOLDER + dir, 30);
        rightBlockStand = Animation.load("rightstandblock", Animation.RESOURCES_FOLDER + dir, 10);
        leftBlockStand = Animation.load("leftstandblock", Animation.RESOURCES_FOLDER + dir, 10);
        rightBlockWalk = Animation.load("rightwalkblock", Animation.RESOURCES_FOLDER + dir, 10);
        leftBlockWalk = Animation.load("leftwalkblock", Animation.RESOURCES_FOLDER + dir, 10);
        leftTakesDamage = Animation.load("righttakesdamage", Animation.RESOURCES_FOLDER + dir, 60);
        rightTakesDamage = Animation.load("lefttakesdamage", Animation.RESOURCES_FOLDER + dir, 60);
        current = standing;
        current.play();
    }

    protected void setAnimations(String dir, String value) {
        if(value.equals("purple")) {
            standing = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            leftRun = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            rightRun = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            leftAttack = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            rightAttack = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            rightDodge = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            leftDodge = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            leftBlock = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            rightBlockStand = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            leftBlockStand = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            rightBlockWalk = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            leftBlockWalk = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            leftTakesDamage = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            rightTakesDamage = Animation.load("crystal_purple", Animation.RESOURCES_FOLDER + dir, 10);
            current = standing;
            current.play();
        }

        if(value.equals("blue")) {
            standing = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            leftRun = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            rightRun = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            leftAttack = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            rightAttack = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            rightDodge = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            leftDodge = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            leftBlock = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            rightBlockStand = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            leftBlockStand = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            rightBlockWalk = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            leftBlockWalk = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            leftTakesDamage = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            rightTakesDamage = Animation.load("crystal_blue", Animation.RESOURCES_FOLDER + dir, 10);
            current = standing;
            current.play();
        }

    }

    /**
     * @brief Get entity sprite to display.
     * @return Current sprite.
     */
    public BufferedImage getSprite() {
        return current.getCurrentFrame();
    }

    /**
     * @brief Get entity sprite size.
     * @return The size in the form of {width, height}.
     */
    public Vector2D getSpriteSize() {
        return current.getSize();
    }

    /**
     * @brief Change the animation to display.
     * 
     *        If the animation is the same than before, no change is made.
     * 
     * @param animationIndex A constant index that describes the type of animation.
     */
    public void swapAnimation(AnimationIndex animationIndex) {
        if (!isActive || (isBeingHit && animationIndex != AnimationIndex.DAMAGE)) {
            return;
        }

        if (animationIndex == AnimationIndex.STANDING && this.current != this.standing && !isAttacking && !isDodging
                && !isBlocking) {
            this.current.stop();
            this.current = this.standing;
            this.current.play();
        } else if (animationIndex == AnimationIndex.LEFTRUN && this.current != this.leftRun && !isAttacking
                && !isDodging && !isBlocking) {
            this.isFacingLeft = true;
            this.current.stop();
            this.current = this.leftRun;
            this.current.play();
        } else if (animationIndex == AnimationIndex.RIGHTRUN && this.current != this.rightRun && !isAttacking
                && !isDodging && !isBlocking) {
            this.isFacingLeft = false;
            this.current.stop();
            this.current = this.rightRun;
            this.current.play();
        } else if (animationIndex == AnimationIndex.ATTACK) {
            this.current.stop();
            this.current = this.isFacingLeft ? this.leftAttack : this.rightAttack;
            this.current.playOnce();
        } else if (animationIndex == AnimationIndex.DODGE) {
            this.current.stop();
            this.current = this.isFacingLeft ? this.leftDodge : this.rightDodge;
            this.current.playOnce();
        } else if (animationIndex == AnimationIndex.BLOCK) {
            this.current.stop();
            this.current = this.isFacingLeft ? this.leftBlock : this.rightBlock;
            this.current.playOnce();
        } else if (animationIndex == AnimationIndex.BLOCKSTAND && this.current != this.leftBlockStand
                && this.current != this.rightBlockStand) {
            this.current.stop();
            this.current = this.isFacingLeft ? this.leftBlockStand : this.rightBlockStand;
            this.current.play();
        } else if (animationIndex == AnimationIndex.BLOCKWALK && this.current != this.leftBlockWalk
                && this.current != this.rightBlockWalk) {
            this.current.stop();
            this.current = this.isFacingLeft ? this.leftBlockWalk : this.rightBlockWalk;
            this.current.play();
        } else if (animationIndex == AnimationIndex.DAMAGE) {
            this.current.stop();
            this.current = this.isFacingLeft ? this.leftTakesDamage : this.rightTakesDamage;
            this.current.play();
        }
    }

}