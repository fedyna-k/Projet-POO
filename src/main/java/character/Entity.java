package character;


import java.awt.image.BufferedImage;

import geometry.Vector2D;

import graphics.Animation;


public abstract class Entity {
    public enum AnimationIndex {
        STANDING, LEFTRUN, RIGHTRUN, ATTACK, DODGE
    };

    public Vector2D coordinates;
    public int height;
    public int width;
    public String name;
    protected boolean isAttacking;
    protected boolean isFacingLeft;
    protected boolean isDodging;

    protected Animation current;
    protected Animation standing;
    protected Animation leftRun;
    protected Animation rightRun;

    protected Animation currentAttack;
    protected Animation leftAttack;
    protected Animation rightAttack;

    private Animation dodge;

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
        // Important ! Or else dodging animation won't ever work
        if (isDodging) {
        // Check if dodging animation ended
        if (!current.isPlaying()) {
            isDodging = false; // Change state
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

        this.coordinates.x += dx;
        this.coordinates.y += dy;
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
        if (!this.isAttacking) {
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
        if (!this.isDodging) {
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
     * Go through all basic animations and load them
     * @param dir The folder contaning all frames
     */
    protected void setAnimations(String dir) {
        standing = Animation.load("standing", Animation.RESOURCES_FOLDER + dir, 10);
        leftRun = Animation.load("leftrun", Animation.RESOURCES_FOLDER + dir, 10);
        rightRun = Animation.load("rightrun", Animation.RESOURCES_FOLDER + dir, 10);
        leftAttack = Animation.load("leftattack", Animation.RESOURCES_FOLDER + dir, 30);
        rightAttack = Animation.load("rightattack", Animation.RESOURCES_FOLDER + dir, 30);
        dodge = Animation.load("dodge", Animation.RESOURCES_FOLDER + dir, 30);
        current = standing;
        currentAttack = leftAttack;
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
        if (animationIndex == AnimationIndex.STANDING && this.current != this.standing && !this.currentAttack.isPlaying()) {
            this.isAttacking = false;
            this.current.stop();
            this.current = this.standing;
            this.current.play();
        } else if (animationIndex == AnimationIndex.LEFTRUN && this.current != this.leftRun && !this.currentAttack.isPlaying()) {
            this.isAttacking = false;
            this.isFacingLeft = true;
            this.current.stop();
            this.current = this.leftRun;
            this.current.play();
        } else if (animationIndex == AnimationIndex.RIGHTRUN && this.current != this.rightRun && !this.currentAttack.isPlaying()) {
            this.isAttacking = false;
            this.isFacingLeft = false;
            this.current.stop();
            this.current = this.rightRun;
            this.current.play();
        } else if (animationIndex == AnimationIndex.ATTACK && !this.currentAttack.isPlaying()) {
            this.isAttacking = true;
            this.current.stop();
            this.currentAttack = this.isFacingLeft ? this.leftAttack : this.rightAttack;
            this.current = this.currentAttack;
            this.current.playOnce();
        } else if (animationIndex == AnimationIndex.DODGE && !this.dodge.isPlaying() && !this.currentAttack.isPlaying()) {
            this.isAttacking = false;
            this.current.stop();
            this.isDodging = true;
            this.current = this.dodge;
            this.current.playOnce();
        }
    }
}