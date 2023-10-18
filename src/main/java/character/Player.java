package character;

import geometry.Vector2D;

public class Player extends Entity {
    public Player() {
        this(0, 0);
    }

    public Player(double x, double y) {
        this.setAnimations("player/");
        this.coordinates = new Vector2D(x, y);
        this.isFacingLeft = true;
    }

    public Vector2D getOffset() {
        if (isAttacking && isFacingLeft) {
            return new Vector2D(-32, 0);
        }
        
        return new Vector2D();
    }
}
