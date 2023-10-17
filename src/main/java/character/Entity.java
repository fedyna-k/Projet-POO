package character;


import geometry.Vector2D;

import graphics.Animation;


public abstract class Entity {
    protected Vector2D coordinates;
    protected int height;
    protected int width;
    protected String name;

    protected Animation current;
    protected Animation standing;
    protected Animation leftRun;
    protected Animation rightRun;


    void setAnimations(String dir) {
        standing = Animation.load("standing", Animation.RESOURCES_FOLDER + dir, 10);
        leftRun = Animation.load("leftrun", Animation.RESOURCES_FOLDER + dir, 10);
        rightRun = Animation.load("rightrun", Animation.RESOURCES_FOLDER + dir, 10);
    }

    /**
     * Move Entity by a given vector
     * @param dx x coordinate of vector
     * @param dy y coordinate of vector
     */
    void move(double dx, double dy) {
        this.coordinates.x += dx;
        this.coordinates.y += dy;
    }
}