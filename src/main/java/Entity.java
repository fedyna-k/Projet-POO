import geometry.Vector2D;

public abstract class Entity {
    protected Vector2D coordinates;
    protected int height;
    protected int width;
    protected String name;

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