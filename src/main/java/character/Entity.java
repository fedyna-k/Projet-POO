package character;

import java.awt.image.BufferedImage;

import geometry.Vector2D;

import graphics.Animation;

public abstract class Entity {
    public enum AnimationIndex {
        STANDING, LEFTRUN, RIGHTRUN, ATTACK, DODGE, BLOCK, BLOCKWALK, BLOCKSTAND
    };

    public Vector2D coordinates;
    public int height;
    public int width;
    public String name;
    protected boolean isAttacking;
    protected boolean isFacingLeft;
    protected boolean isDodging;
    protected boolean isBlocking;
    protected Vector2D bufferedMovement;

    protected Animation current;

    protected Animation standing;
    protected Animation leftRun;
    protected Animation rightRun;

    protected Animation leftAttack;
    protected Animation rightAttack;

    protected Animation rightDodge;
    protected Animation leftDodge;

    protected Animation rightBlock;
    protected Animation leftBlock;
    protected Animation rightBlockStand;
    protected Animation leftBlockStand;
    protected Animation rightBlockWalk;
    protected Animation leftBlockWalk;

    /**
     * Get the Vector2D representation of entity position
     * 
     * @return The position vector of the entity
     */
    public Vector2D getPosition() {
        return this.coordinates;
    }

    /**
     * Use this function to return offsets depending on contexts and sprites
     * 
     * @return A Vector2D representing offset to apply (specific to base sprite)
     */
    abstract public Vector2D getOffset();

    /**
     * Move Entity by a given vector
     * 
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

        // Block state setter
        if (isBlocking && !current.isPlaying()) {
            isBlocking = false;
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
     * 
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
     * 
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
     * 
     * @return The dodging state
     */
    public boolean isDodging() {
        return this.isDodging;
    }

    /**
     * Put the entity into block state
     */
    public void block() {
        if (!this.isBlocking) {
            isBlocking = true;
            swapAnimation(AnimationIndex.BLOCK);
        }
    }

    /**
     * Put the entity into block + walk state
     */
    public void blockwalk() {
        if (!this.isBlocking) {
            isBlocking = true;
            swapAnimation(AnimationIndex.BLOCKWALK);
        }
    }

    /**
     * Put the entity into block + stand state
     */
    public void blockstand() {
        if (!this.isBlocking) {
            isBlocking = true;
            swapAnimation(AnimationIndex.BLOCKSTAND);
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
        swapAnimation(AnimationIndex.STANDING);
    }

    /**
     * Get the entity's orientation
     * 
     * @return true if facing left
     */
    public boolean isFacingLeft() {
        return this.isFacingLeft;
    }

    /**
     * Go through all basic animations and load them
     * 
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
        rightBlock = Animation.load("rightblock", Animation.RESOURCES_FOLDER + dir, 30);
        leftBlock = Animation.load("leftblock", Animation.RESOURCES_FOLDER + dir, 30);
        rightBlockStand = Animation.load("rightstandblock", Animation.RESOURCES_FOLDER + dir, 30);
        leftBlockStand = Animation.load("leftstandblock", Animation.RESOURCES_FOLDER + dir, 30);
        rightBlockWalk = Animation.load("rightwalkblock", Animation.RESOURCES_FOLDER
                + dir, 30);
        leftBlockWalk = Animation.load("leftwalkblock", Animation.RESOURCES_FOLDER +
                dir, 30);

        current = standing;
        current.play();
    }

    /**
     * Get entity sprite to display
     * 
     * @return Current sprite
     */
    public BufferedImage getSprite() {
        return current.getCurrentFrame();
    }

    /**
     * Get entity sprite size
     * 
     * @return The size in the form of {width, height}
     */
    public Vector2D getSpriteSize() {
        return current.getSize();
    }

    /**
     * Change the animation to display
     * If the animation is the same than before, no change is made
     * 
     * @param animationIndex A constant index that describes the type of animation
     */
    public void swapAnimation(AnimationIndex animationIndex) {
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
        } else if (animationIndex == AnimationIndex.BLOCKSTAND) {
            this.current.stop();
            this.current = this.isFacingLeft ? this.leftBlockStand : this.rightBlockStand;
            this.current.play();
        } else if (animationIndex == AnimationIndex.BLOCKWALK) {
            this.current.stop();
            this.current = this.isFacingLeft ? this.leftBlockWalk : this.rightBlockWalk;
            this.current.play();
        }
    }
}