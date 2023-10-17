package character;


import java.awt.image.BufferedImage;

import geometry.Vector2D;

import graphics.Animation;


public abstract class Entity {
    public enum AnimationIndex {
        STANDING, LEFTRUN, RIGHTRUN
    };

    protected Vector2D coordinates;
    protected int height;
    protected int width;
    protected String name;

    protected Animation current;
    protected Animation standing;
    protected Animation leftRun;
    protected Animation rightRun;

    /**
     * Move Entity by a given vector
     * @param dx x coordinate of vector
     * @param dy y coordinate of vector
     */
    public void move(double dx, double dy) {
        this.coordinates.x += dx;
        this.coordinates.y += dy;
    }

    /**
     * Go through all basic animations and load them
     * @param dir The folder contaning all frames
     */
    protected void setAnimations(String dir) {
        standing = Animation.load("standing", Animation.RESOURCES_FOLDER + dir, 10);
        leftRun = Animation.load("leftrun", Animation.RESOURCES_FOLDER + dir, 10);
        rightRun = Animation.load("rightrun", Animation.RESOURCES_FOLDER + dir, 10);
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
        this.current.stop();
        
        if (animationIndex == AnimationIndex.STANDING && this.current != this.standing) {
            this.current = this.standing;
        } else if (animationIndex == AnimationIndex.LEFTRUN && this.current != this.leftRun) {
            this.current = this.leftRun;
        } else if (animationIndex == AnimationIndex.RIGHTRUN && this.current != this.rightRun) {
            this.current = this.rightRun;
        }

        this.current.play();
    }
}