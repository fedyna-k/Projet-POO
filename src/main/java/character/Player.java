package character;

import geometry.Vector2D;

public class Player extends Entity {
    public Player() {
        this.setAnimations("player/");
        this.coordinates = new Vector2D();
    }

    public Player(double x, double y) {
        this.setAnimations("player/");
        this.coordinates = new Vector2D(x, y);
    }
}
