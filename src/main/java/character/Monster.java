package character;

import geometry.Vector2D;

public class Monster extends Entity {
    public Monster() {
        this(0, 0);
    }

    public Monster(double x, double y) {
        this.setAnimations("monster/");
        this.coordinates = new Vector2D(x, y);
        this.isFacingLeft = false;
    }

    public Vector2D getOffset() {
        if (isAttacking) {
            return new Vector2D(isFacingLeft ? 16 : -16, 0);
        }

        return new Vector2D();
    }

    public void randMovement() {
        double range = 1000;
        double randomX = (Math.random() * (20 * range)) - range;
        double randomY = (Math.random() * (20 * range)) - range;

        Vector2D randomMovement = new Vector2D(randomX, randomY);

        randomMovement.normalize();
        move(Vector2D.scale(randomMovement, 2));
    }
}
