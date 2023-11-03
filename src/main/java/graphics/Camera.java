package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import character.Entity;
import geometry.Vector2D;

public class Camera {
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

        // Get all components
        Vector2D relativePosition = getRelativePosition(singleton.focused.getPosition().x, singleton.focused.getPosition().y, x, y);
        Vector2D imageCenter = new Vector2D(-width / 2, -height / 2);
        offset = Vector2D.scale(offset, -scale);

        Vector2D position = Vector2D.add(relativePosition, imageCenter, offset);

        graph.drawImage(image, (int) position.x, (int) position.y, width, height, singleton.canvas);
    }

    /**
     * Get relative position of point based on focus coordinates
     * @param focusX The focused entity x position
     * @param focusY The focused entity y position
     * @param x The image x position
     * @param y The image y position
     * @return The Vector2D corresponding to the shift that has to be made
     */
    private Vector2D getRelativePosition(double focusX, double focusY, double x, double y) {
        Vector2D relativeOffset = new Vector2D(x - focusX, y - focusY);
        return Vector2D.add(singleton.canvas.getCenter(), relativeOffset);
    }

    public void drawRect(Graphics graph, double x, double y, int w, int h, Color color) {
        // If nothing is focused, we draw on given position
        if (singleton.focused == null) {
            graph.setColor(color);
            graph.drawRect((int) x, (int) y, w, h);
            return;
        }

        
    }


    public void showCam(Graphics g, Entity focus, Entity unfocus) {
        g.setColor(new Color(255, 0, 0));
        g.drawRect(1720, 970, 193, 109);
        g.setColor(new Color(42, 42, 42));
        g.fillRect(1721, 971, 192, 108);
        g.setColor(new Color(0, 255, 0));
        g.fillRect(1721 + 96, 971 + 54, 2, 2); 
        g.setColor(new Color(255, 0, 0));


        Vector2D other = unfocus.getPosition();
        Vector2D relative = getRelativePosition(focus.getPosition().x, focus.getPosition().y, other.x, other.y);

        if ((1721 + (int)relative.x / 10 + 96) < 1721 || (1721 + (int)relative.x / 10 + 96) > 1913) {
            return;
        }
        if ((971 + (int)relative.y / 10 + 54) < 971 || (971 + (int)relative.y / 10 + 54) > 1079) {
            return;
        }

        g.fillRect(1721 + (int)relative.x / 10 + 96, 971 + (int)relative.y / 10 + 54, 2, 2);
    }
}
