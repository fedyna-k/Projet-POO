package character;


import java.awt.image.BufferedImage;

import geometry.Vector2D;

import graphics.Animation;


public abstract class Entity {
    public enum AnimationIndex {
        STANDING, LEFTRUN, RIGHTRUN, ATTACK, DODGE
    };

    // Hit points of the entity
    private int hitPoints;

    // Force of the entity
    private int force;

    // Damage taken by the entity
    private int damage;

    public Vector2D coordinates;
    public int height;
    public int width;
    public String name;
    protected boolean isAttacking;
    protected boolean isFacingLeft;
    protected boolean isDodging;
    protected Vector2D bufferedMovement;

    protected Animation current;

    protected Animation standing;
    protected Animation leftRun;
    protected Animation rightRun;
    protected Animation leftAttack;
    protected Animation rightAttack;
    protected Animation rightDodge;
    protected Animation leftDodge;


    /**
     * Get the current hit points of the entity.
     *
     * @return the hit points of the entity.
     */
    public int getHitPoints() {
        return hitPoints;
    }

    /**
     * Set the hit points of the entity.
     *
     * @param hitPoints the new hit points of the entity.
     */
    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    // Method to get the force of the entity
    public int getForce() {
        return force;
    }

    // Method to set the force of the entity
    public void setForce(int force) {
        this.force = force;
    }

    // Method to get the damage taken by the entity
    public int getDamage() {
        return damage;
    }

    // Method to set the damage taken by the entity
    public void setDamage(int damage) {
        this.damage = damage;
    }


    /**
     * Get the Vector2D representation of entity position
     * @return The position vector of the entity
     */
    public Vector2D getPosition() {
        return this.coordinates;
    }

    /**
     * Use this function to return offsets depending on contexts and sprites
     * @return A Vector2D representing offset to apply (specific to base sprite)
     */
    abstract public Vector2D getOffset();

    /**
     * Move Entity by a given vector
     * @param dx x coordinate of vector
     * @param dy y coordinate of vector
     */
    public void move(double dx, double dy) {
        // Dodge state setter
        if (isDodging && !current.isPlaying()) {
            isDodging = false;
        }

        // Attack state setter
        if (isAttacking && !current.isPlaying()) {
            isAttacking = false;
        }

        if (isDodging) {
            // Apply default movement
            if (bufferedMovement.isNull()) {
                dx = isFacingLeft ? -4 : 4;
            } else {
                dx = bufferedMovement.x;
                dy = bufferedMovement.y;
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
        this.coordinates.x += isDodging ? dx * 3 : dx;
        this.coordinates.y += isDodging ? dy * 3 : dy;
    }

    /**
     * Move Entity by a given vector
     * @param movement The [dx, dy] vector
     */
    public void move(Vector2D movement) {
        this.move(movement.x, movement.y);
    }

    /**
     * Put the entity into attack state
     */
    public void attack() {
        if (!this.isAttacking && !this.isDodging) {
            isAttacking = true;
            swapAnimation(AnimationIndex.ATTACK);
        }
    }

    /**
     * Get the attacking state of the entity
     * @return The attacking state
     */
    public boolean isAttacking() {
        return this.isAttacking;
    }

    /**
     * Put the entity into dodge state
     */
    public void dodge() {
        if (!this.isDodging && !this.isAttacking) {
            isDodging = true;
            swapAnimation(AnimationIndex.DODGE);
        }
    }

    /**
     * Get the dodging state of the entity
     * @return The dodging state
     */
    public boolean isDodging() {
        return this.isDodging;
    }

    /**
     * Get the entity's orientation
     * @return true if facing left
     */
    public boolean isFacingLeft() {
        return this.isFacingLeft;
    }

    /**
     * Go through all basic animations and load them
     * @param dir The folder contaning all frames
     */
    protected void setAnimations(String dir) {
        standing = Animation.load("standing", Animation.RESOURCES_FOLDER + dir, 10);
        leftRun = Animation.load("leftrun", Animation.RESOURCES_FOLDER + dir, 10);
        rightRun = Animation.load("rightrun", Animation.RESOURCES_FOLDER + dir, 10);
        leftAttack = Animation.load("leftattack", Animation.RESOURCES_FOLDER + dir, 30);
        rightAttack = Animation.load("rightattack", Animation.RESOURCES_FOLDER + dir, 30);
        rightDodge = Animation.load("rightdodge", Animation.RESOURCES_FOLDER + dir, 20);
        leftDodge = Animation.load("leftdodge", Animation.RESOURCES_FOLDER + dir, 20);

        current = standing;
        current.play();
    }

    /**
     * Get entity sprite to display
     * @return Current sprite
     */
    public BufferedImage getSprite() {
        return current.getCurrentFrame();
    }

    /**
     * Get entity sprite size
     * @return The size in the form of {width, height}
     */
    public int[] getSpriteSize() {
        return current.getSize();
    }

    /**
     * Change the animation to display
     * If the animation is the same than before, no change is made
     * @param animationIndex A constant index that describes the type of animation
     */
    public void swapAnimation(AnimationIndex animationIndex) {
        if (animationIndex == AnimationIndex.STANDING && this.current != this.standing && !isAttacking && !isDodging) {
            this.current.stop();
            this.current = this.standing;
            this.current.play();
        } else if (animationIndex == AnimationIndex.LEFTRUN && this.current != this.leftRun && !isAttacking && !isDodging) {
            this.isFacingLeft = true;
            this.current.stop();
            this.current = this.leftRun;
            this.current.play();
        } else if (animationIndex == AnimationIndex.RIGHTRUN && this.current != this.rightRun && !isAttacking && !isDodging) {
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
        }
    }
}