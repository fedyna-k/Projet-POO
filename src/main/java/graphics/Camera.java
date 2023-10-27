package graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import character.Entity;
import geometry.Vector2D;

public class Camera {
    private static final int TILE_SIZE = 64;
    private static Camera singleton;
    private Entity focused;
    private Canvas canvas;

    private Camera(Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Get the Camera
     * @param canvas The canvas the camera will watch
     * @return The Camera
     */
    public static Camera getCamera(Canvas canvas) {
        // If singleton hasn't been create, create Camera
        if (singleton == null) {
            singleton = new Camera(canvas);
        }

        return singleton;
    }

    /**
     * Get the Camera focused on given entity
     * @param canvas The canvas the camera will watch
     * @param entity The entity to focus on
     * @return The Camera
     */
    public static Camera getCamera(Canvas canvas, Entity entity) {
        // If singleton hasn't been create, create Camera
        if (singleton == null) {
            singleton = new Camera(canvas);
        }

        singleton.focused = entity;
        return singleton;
    }

    /**
     * Set focus on Entity
     * @param entity The entity to focus on
     */
    public void setFocusOn(Entity entity) {
        singleton.focused = entity;
    }

    /**
     * Draw image on screen based on focused point
     * @param graph The Graphics object
     * @param image The Image we want to draw
     * @param x The x position in absolute coordinates
     * @param y The y position in absolute coordinates
     */
    public void drawImage(Graphics graph, BufferedImage image, double x, double y) {
        drawImage(graph, image, x, y, 1, new Vector2D());
    }

    /**
     * Draw image on screen based on focused point
     * @param graph The Graphics object
     * @param image The Image we want to draw
     * @param x The x position in absolute coordinates
     * @param y The y position in absolute coordinates
     * @param scale Scale factor for the image width and height
     */
    public void drawImage(Graphics graph, BufferedImage image, double x, double y, double scale) {
        drawImage(graph, image, x, y, scale, new Vector2D());
    }

    /**
     * Draw image on screen based on focused point
     * @param graph The Graphics object
     * @param image The Image we want to draw
     * @param x The x position in absolute coordinates
     * @param y The y position in absolute coordinates
     * @param scale Scale factor for the image width and height
     * @param offset The offset on image (without scaling)
     */
    public void drawImage(Graphics graph, BufferedImage image, double x, double y, double scale, Vector2D offset) {      
        // Get image size after scaling
        int width = (int) Math.floor(image.getWidth() * scale);
        int height = (int) Math.floor(image.getHeight() * scale);

        // If nothing is focused, we simply draw image on given position
        if (singleton.focused == null) {
            graph.drawImage(image, (int) x, (int) y, width, height, singleton.canvas);
            return;
        }

        // Compute final position components
        Vector2D canvasCenter = new Vector2D(singleton.canvas.getWidth() / 2, singleton.canvas.getHeight() / 2);
        Vector2D imageCenter = new Vector2D(x - TILE_SIZE * scale / 2, y - TILE_SIZE * scale / 2);
        Vector2D absoluteOffset = singleton.focused.getPosition();
        absoluteOffset.negate();

        // Compute position by adding components and draw image
        Vector2D position = Vector2D.add(canvasCenter, imageCenter, absoluteOffset, Vector2D.scale(offset, scale));
        graph.drawImage(image, (int) position.x, (int) position.y, width, height, singleton.canvas);
    }
}
