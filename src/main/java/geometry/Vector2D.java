/**
 * @brief This file contains the public class Vector2D.
 * 
 * @file Vector2D.java
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * Part of the `geometry` package.
 * It contains a class that allows to perform mathematical operations on R² vectors.
 */

package geometry;

/**
 * @class Vector2D
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * @brief This class allows to perform mathematical operations on R² vectors.
 */
public class Vector2D {
    /** @brief The first coordinate of the vector. */
    public double x;
    /** @brief The second coordinate of the vector. */
    public double y;

    /**
     * @brief Initialize null vector.
     * 
     * \f[
     *      \mathbf{v} := \mathbf{0}
     * \f]
     */
    public Vector2D() {
        this.x = 0.0;
        this.y = 0.0;
    }

    /**
     * @brief Initialize vector with given values.
     * 
     * \f[
     *      \mathbf{v} := \left[\begin{array}{c} x \\ y \end{array}\right]
     * \f]
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * 
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @brief Create copy of given vector.
     * 
     * \f[
     *      \mathbf{v} := \left[\begin{array}{c} \mathbf{u}_x \\ \mathbf{u}_y \end{array}\right]
     * \f]
     * 
     * @param initializing_vector The vector to copy.
     */
    public Vector2D(Vector2D initializing_vector) {
        this.x = initializing_vector.x;
        this.y = initializing_vector.y;
    }

    /**
     * @brief Change signs of both coordinates of vector
     * 
     * \f[
     *      \mathbf{v} := -\mathbf{v}
     * \f]
     */
    public void negate() {
        this.x *= -1;
        this.y *= -1;
    }

    /**
     * @brief Make the vector's norm equal to 1.
     * 
     * \f[
     *      \mathbf{v} := \frac{\mathbf{v}}{\|\mathbf{v}\|_2}
     * \f]
     * 
     * @note It will not raise an error on null vector.
     */
    public void normalize() {
        if (!isNull()) {
            double norm = Math.sqrt(dot(this, this));
            this.x /= norm;
            this.y /= norm;
        }
    }

    /**
     * @brief Computes Vector norm.
     * 
     * The norm is given by :
     * \f[
     *      \|\mathbf{v}\|_2 = \sqrt{x^2 + y^2} = \sqrt{\mathbf{v}\cdot\mathbf{v}}
     * \f]
     * 
     * @return The 2-norm of the vector.
     */
    public double norm() {
        return Math.sqrt(dot(this, this));
    }

    /**
     * @brief Check if vector is Null vector.
     * @return true if \f$ \mathbf{v} = \mathbf{0} \f$.
     */ 
    public boolean isNull() {
        return this.x == 0 && this.y == 0;
    }

    /**
     * @brief Scale a vector by a real factor.
     * 
     * \f[
     *      \mathrm{scale}(\mathbf{v}, \lambda) = \lambda \mathbf{v}
     * \f]
     * 
     * @param vector The vector to scale.
     * @param factor The number we use to apply multiplication.
     * @return A scaled version of the vector.
     */
    public static Vector2D scale(Vector2D vector, double factor) {
        return new Vector2D(vector.x * factor, vector.y * factor);
    }

    /**
     * @brief Computes dot product of two vectors.
     * 
     * \f[
     *      \mathbf{v}\cdot \mathbf{u} = \mathbf{v}_x \mathbf{u}_x + \mathbf{v}_y \mathbf{u}_y
     * \f]
     * 
     * @param first The first vector.
     * @param second The second vector.
     * @return The dot product between the two vectors.
     */
    public static double dot(Vector2D first, Vector2D second) {
        return first.x * second.x + first.y * second.y;
    }

    /**
     * Add two or more vectors.
     * 
     * \f[
     *      \sum \mathbf{v}_i = \left[\begin{array}{c} \sum \mathbf{v}_{i, x} \\ \sum \mathbf{v}_{i, y} \end{array}\right]
     * \f]
     * 
     * @param first The first vector.
     * @param second The second vector.
     * @param others The other vectors.
     * @return The sum of the vectors.
     */
    public static Vector2D add(Vector2D first, Vector2D second, Vector2D... others) {
        double x = first.x + second.x;
        double y = first.y + second.y;

        for (Vector2D vector : others) {
            x += vector.x;
            y += vector.y;
        }

        return new Vector2D(x, y);
    }

    /**
     * @brief Override toString in order to print Vector2D.
     * @return A string in the form of `"x;y"`.
     */
    @Override
    public String toString() {
        return x + ";" + y;
    }
}