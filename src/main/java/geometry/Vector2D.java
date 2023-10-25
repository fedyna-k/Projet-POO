package geometry;

public class Vector2D {
    public double x, y;

    /**
     * Initialize null vector
     */
    public Vector2D() {
        this.x = 0.0;
        this.y = 0.0;
    }

    /**
     * Initialize vector with given values
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Initialize vector with given vector
     * @param initializing_vector The vector to copy
     */
    public Vector2D(Vector2D initializing_vector) {
        this.x = initializing_vector.x;
        this.y = initializing_vector.y;
    }

    /**
     * Change signs of both coordinates of vector
     */
    public void negate() {
        this.x *= -1;
        this.y *= -1;
    }

    /**
     * Normalize vector to make norm equals to 1
     */
    public void normalize() {
        if (!isNull()) {
            double norm = Math.sqrt(dot(this, this));
            this.x /= norm;
            this.y /= norm;
        }
    }

    /**
     * Computes Vector norm
     * @return The 2-norm of the vector
     */
    public double norm() {
        return Math.sqrt(dot(this, this));
    }

    /**
     * Check if vector is Null vector
     * @return true if x = y = 0
     */
    public boolean isNull() {
        return this.x == 0 && this.y == 0;
    }

    /**
     * Scale a vector by a real factor
     * @param vector The vector to scale
     * @param factor The number we use to apply multiplication
     * @return A scaled version of the vector
     */
    public static Vector2D scale(Vector2D vector, double factor) {
        return new Vector2D(vector.x * factor, vector.y * factor);
    }

    /**
     * Computes dot product of two vectors
     * @param first The first vector
     * @param second The second vector
     * @return The dot product between the two vectors
     */
    public static double dot(Vector2D first, Vector2D second) {
        return first.x * second.x + first.y * second.y;
    }

    /**
     * Add two vectors
     * @param first The first vector
     * @param second The second vector
     * @return The sum of the two vectors
     */
    public static Vector2D add(Vector2D first, Vector2D second) {
        return new Vector2D(first.x + second.x, first.y + second.y);
    }

    @Override
    public String toString() {
        return x + ";" + y;
    }
}