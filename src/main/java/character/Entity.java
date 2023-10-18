package character;


import java.awt.image.BufferedImage;

import geometry.Vector2D;

import graphics.Animation;


public abstract class Entity {
    public enum AnimationIndex {
        STANDING, LEFTRUN, RIGHTRUN
    };

    public Vector2D coordinates;
    public int height;
    public int width;
    public String name;

    protected Animation current;
    protected Animation standing;
    protected Animation leftRun;
    protected Animation rightRun;

    /**
     * Get the Vector2D representation of entity position
     * @return The position vector of the entity
     */
    public Vector2D getPosition() {
        return this.coordinates;
    }

    /**
     * Move Entity by a given vector
     * @param dx x coordinate of vector
     * @param dy y coordinate of vector
     */
    public void move(double dx, double dy) {
        if (dx > 0 || dx == 0 && dy != 0) {
            swapAnimation(AnimationIndex.RIGHTRUN);
        } else if (dx < 0) {
            swapAnimation(AnimationIndex.LEFTRUN);
        } else {
            swapAnimation(AnimationIndex.STANDING);
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
     * Go through all basic animations and load them
     * @param dir The folder contaning all frames
     */
    protected void setAnimations(String dir) {
        standing = Animation.load("standing", Animation.RESOURCES_FOLDER + dir, 10);
        leftRun = Animation.load("leftrun", Animation.RESOURCES_FOLDER + dir, 10);
        rightRun = Animation.load("rightrun", Animation.RESOURCES_FOLDER + dir, 10);
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
     * Change the animation to display
     * If the animation is the same than before, no change is made
     * @param animationIndex A constant index that describes the type of animation
     */
    public void swapAnimation(AnimationIndex animationIndex) {
        if (animationIndex == AnimationIndex.STANDING && this.current != this.standing) {
            this.current.stop();
            this.current = this.standing;
            this.current.play();
        } else if (animationIndex == AnimationIndex.LEFTRUN && this.current != this.leftRun) {
            this.current.stop();
            this.current = this.leftRun;
            this.current.play();
        } else if (animationIndex == AnimationIndex.RIGHTRUN && this.current != this.rightRun) {
            this.current.stop();
            this.current = this.rightRun;
            this.current.play();
        }
    }
}