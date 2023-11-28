package character;

import java.util.ArrayList;
import java.util.List;

import geometry.Vector2D;
import graphics.Canvas;

public class Monster extends Entity {
    private Canvas canvas;

    public Monster() {
        this(0, 0);
    }

    public Monster(Canvas canvas, double x, double y) {
        this.canvas = canvas;
        this.setAnimations("monster/");
        this.coordinates = new Vector2D(x, y);
        this.isFacingLeft = false;
    }

    public Monster(int i, int j) {
    }

    public Vector2D getOffset() {
        if (isAttacking) {
            return new Vector2D(isFacingLeft ? 16 : -16, 0);
        }

        return new Vector2D();
    }

    public Vector2D RandomMovement(Entity entity, Vector2D currentPosition) {
        int numPoints = 10;
        double radius = 1000;
        List<Vector2D> pointsToFollow = new ArrayList<>();

        // Random points to follow
        for (int i = 0; i < numPoints; i++) {
            double randomAngle = Math.random() * 2 * Math.PI;
            double x = currentPosition.x + radius * Math.cos(randomAngle);
            double y = currentPosition.y + radius * Math.sin(randomAngle);
            pointsToFollow.add(new Vector2D(x, y));
        }

        // Choose one that avoids collisions
        for (Vector2D point : pointsToFollow) {
            Vector2D direction = Vector2D.subtract(point, currentPosition);
            direction.normalize();
            if (!canvas.checkCollision(entity, Vector2D.add(currentPosition, direction))) {
                return Vector2D.add(currentPosition, direction);
            }
        }

        return currentPosition;
    }

}
