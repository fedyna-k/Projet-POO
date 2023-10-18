package character;

import geometry.Vector2D;

public class Player extends Entity {
    public Player() {
        this.setAnimations("player/");
        this.coordinates = new Vector2D();
    }
}
