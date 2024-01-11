/**
 * @brief This file contains the public class Camera.
 * 
 * @file Camera.java
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * Part of the `graphics` package. It contains a class that allow to simplify maths for drawing.
 */

package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import character.Entity;
import geometry.Vector2D;
import map.Map;

/**
 * @class Camera
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * @brief This class allows to simplify maths for drawing on screen.
 * 
 *        The class uses the Singleton design pattern.
 */
public class Camera {
    /** @brief The instance of the singleton. */
    private static Camera singleton;
    /**
     * @brief The Entity the camera focuses.
     * @see character.Entity
     */
    private Entity focused;
    /** @brief The canvas associated to the camera. */
    private Canvas canvas;

    private Camera(Canvas canvas) {
        this.canvas = canvas;
    }

    /**
     * @brief Get the Camera.
     * 
     * @note In this case, the camera will focus on point (0, 0) in absolute
     *       positions.
     * 
     * @param canvas The canvas the camera will watch.
     * @return The Camera.
     */
    public static Camera getCamera(Canvas canvas) {
        // If singleton hasn't been create, create Camera
        if (singleton == null) {
            singleton = new Camera(canvas);
        }

        return singleton;
    }

    /**
     * @brief Get the Camera focused on given entity.
     * @param canvas The canvas the camera will watch.
     * @param entity The entity to focus on.
     * @return The Camera.
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
     * @brief Set focus on Entity.
     * @param entity The entity to focus on.
     * @note If null is passed, the camera will focus on point (0, 0) in absolute
     *       positions.
     */
    public void setFocusOn(Entity entity) {
        singleton.focused = entity;
    }

    /**
     * @brief Get the focused Entity.
     * @return The focused Entity object.
     */
    public Entity getFocused() {
        return singleton.focused;
    }

    /**
     * @brief Draw image on screen based on focused point.
     * 
     *        The image will be drawn with a scale of 1 and no offset.
     * 
     * @param graph The Graphics object.
     * @param image The Image we want to draw.
     * @param x     The x position in absolute coordinates.
     * @param y     The y position in absolute coordinates.
     */
    public void drawImage(Graphics graph, BufferedImage image, double x, double y) {
        drawImage(graph, image, x, y, 1, new Vector2D());
    }

    /**
     * @brief Draw image on screen based on focused point.
     * 
     *        The image will be drawn with no offset.
     * 
     * @param graph The Graphics object.
     * @param image The Image we want to draw.
     * @param x     The x position in absolute coordinates.
     * @param y     The y position in absolute coordinates.
     * @param scale Scale factor for the image width and height.
     */
    public void drawImage(Graphics graph, BufferedImage image, double x, double y, double scale) {
        drawImage(graph, image, x, y, scale, new Vector2D());
    }

    /**
     * @brief Draw image on screen based on focused point.
     * 
     *        The most complete version of this method.
     * 
     * @param graph  The Graphics object.
     * @param image  The Image we want to draw.
     * @param x      The x position in absolute coordinates.
     * @param y      The y position in absolute coordinates.
     * @param scale  Scale factor for the image width and height.
     * @param offset The offset on image (without scaling).
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
        Vector2D relativePosition = getRelativePosition(singleton.focused.getPosition().x,
                singleton.focused.getPosition().y, x, y);
        Vector2D imageCenter = new Vector2D(-width / 2, -height / 2);
        offset = Vector2D.scale(offset, -scale);

        Vector2D position = Vector2D.add(relativePosition, imageCenter, offset);

        graph.drawImage(image, (int) position.x, (int) position.y, width, height, singleton.canvas);
    }

    /**
     * @brief Draw tile on screen based on focused point.
     * 
     *        The tile will be drawn with a scale of 1 and no offset.
     *        It will be clamped to the map.
     * 
     * @param graph The Graphics object.
     * @param map   The Map object.
     * @param image The Image we want to draw.
     * @param x     The x position in absolute coordinates.
     * @param y     The y position in absolute coordinates.
     */
    public void drawImageClamped(Graphics graph, Map map, BufferedImage image, double x, double y) {
        drawImageClamped(graph, map, image, x, y, 1, new Vector2D());
    }

    /**
     * @brief Draw tile on screen based on focused point.
     * 
     *        The tile will be drawn with no offset.
     *        It will be clamped to the map.
     * 
     * @param graph The Graphics object.
     * @param map   The Map object.
     * @param image The Image we want to draw.
     * @param x     The x position in absolute coordinates.
     * @param y     The y position in absolute coordinates.
     * @param scale Scale factor on width and height.
     */
    public void drawImageClamped(Graphics graph, Map map, BufferedImage image, double x, double y, double scale) {
        drawImageClamped(graph, map, image, x, y, scale, new Vector2D());
    }

    /**
     * @brief Draw tile on screen based on focused point.
     * 
     *        It will be clamped to the map.
     * 
     * @param graph The Graphics object.
     * @param map   The Map object.
     * @param image The Image we want to draw.
     * @param x     The x position in absolute coordinates.
     * @param y     The y position in absolute coordinates.
     * @param scale Scale factor on width and height.
     * @param offset The offset on image (without scaling).
     */
    public void drawImageClamped(Graphics graph, Map map, BufferedImage image, double x, double y, double scale, Vector2D offset) {
        // Get image size after scaling
        int width = (int) Math.floor(image.getWidth() * scale);
        int height = (int) Math.floor(image.getHeight() * scale);

        // If nothing is focused, we simply draw image on given position
        if (singleton.focused == null) {
            graph.drawImage(image, (int) x, (int) y, width, height, singleton.canvas);
            return;
        }

        // Compute clamped focus
        double mapHeight = map.getHeight() * map.getTileSize() * scale;
        double mapWidth = map.getWidth() * map.getTileSize() * scale;
        Vector2D canvasRadii = singleton.canvas.getCenter();
        double focusX = singleton.focused.getPosition().x;
        double focusY = singleton.focused.getPosition().y;

        double clampedFocusX = Math.min(Math.max(focusX, canvasRadii.x), mapWidth - canvasRadii.x - map.getTileSize());
        double clampedFocusY = Math.min(Math.max(focusY, canvasRadii.y), mapHeight - canvasRadii.y - map.getTileSize());

        // Get all components
        Vector2D relativePosition = getRelativePosition(clampedFocusX, clampedFocusY, x, y);
        Vector2D imageCenter = new Vector2D(-width / 2, -height / 2);
        offset = Vector2D.scale(offset, -scale);

        Vector2D position = Vector2D.add(relativePosition, imageCenter, offset);

        graph.drawImage(image, (int) position.x, (int) position.y, width, height, singleton.canvas); 
    }

    /**
     * @brief Get relative position of point based on focus coordinates.
     * @param focusX The focused entity x position.
     * @param focusY The focused entity y position.
     * @param x      The image x position.
     * @param y      The image y position.
     * @return The Vector2D corresponding to the shift that has to be made.
     */
    private Vector2D getRelativePosition(double focusX, double focusY, double x, double y) {
        Vector2D relativeOffset = new Vector2D(x - focusX, y - focusY);
        return Vector2D.add(singleton.canvas.getCenter(), relativeOffset);
    }

    /**
     * @brief Draw rectangle based on focused entity and centered on position.
     * @param graph The Graphics object.
     * @param x     The rectangle's center's x position in absolute coordinates.
     * @param y     The rectangle's center's y position in absolute coordinates.
     * @param w     The rectangle's width.
     * @param h     The rectangle's height.
     * @param color The rectangle's color.
     */
    public void drawRect(Graphics graph, double x, double y, int w, int h, Color color) {
        graph.setColor(color);

        // If nothing is focused, we draw on given position
        if (singleton.focused == null) {
            graph.drawRect((int) x, (int) y, w, h);
            return;
        }

        // Get all components
        Vector2D relativePosition = getRelativePosition(singleton.focused.getPosition().x,
                singleton.focused.getPosition().y, x, y);
        Vector2D rectangleCenter = new Vector2D(-w / 2, -h / 2);

        Vector2D position = Vector2D.add(relativePosition, rectangleCenter);

        graph.drawRect((int) position.x, (int) position.y, w, h);
    }

    /**
     * @brief Fill rectangle based on focused entity and centered on position.
     * @param graph The Graphics object.
     * @param x     The rectangle's center's x position in absolute coordinates.
     * @param y     The rectangle's center's y position in absolute coordinates.
     * @param w     The rectangle's width.
     * @param h     The rectangle's height.
     * @param color The rectangle's color.
     */
    public void fillRect(Graphics graph, double x, double y, int w, int h, Color color) {
        graph.setColor(color);

        // If nothing is focused, we draw on given position
        if (singleton.focused == null) {
            graph.fillRect((int) x, (int) y, w, h);
            return;
        }

        // Get all components
        Vector2D relativePosition = getRelativePosition(singleton.focused.getPosition().x,
                singleton.focused.getPosition().y, x, y);
        Vector2D rectangleCenter = new Vector2D(-w / 2, -h / 2);

        Vector2D position = Vector2D.add(relativePosition, rectangleCenter);

        graph.fillRect((int) position.x, (int) position.y, w, h);
    }

    /**
     * @brief Draw rectangle based on focused entity and centered on position.
     * @param graph The Graphics object.
     * @param map   The Map object to use for clamping.
     * @param x     The rectangle's center's x position in absolute coordinates.
     * @param y     The rectangle's center's y position in absolute coordinates.
     * @param w     The rectangle's width.
     * @param h     The rectangle's height.
     * @param color The rectangle's color.
     */
    public void drawRectClamped(Graphics graph, Map map,double x, double y, int w, int h, Color color) {
        graph.setColor(color);

        // If nothing is focused, we draw on given position
        if (singleton.focused == null) {
            graph.drawRect((int) x, (int) y, w, h);
            return;
        }

        // Compute clamped focus
        double mapHeight = map.getHeight() * map.getTileSize() * 2;
        double mapWidth = map.getWidth() * map.getTileSize() * 2;
        Vector2D canvasRadii = singleton.canvas.getCenter();
        double focusX = singleton.focused.getPosition().x;
        double focusY = singleton.focused.getPosition().y;

        double clampedFocusX = Math.min(Math.max(focusX, canvasRadii.x), mapWidth - canvasRadii.x - map.getTileSize());
        double clampedFocusY = Math.min(Math.max(focusY, canvasRadii.y), mapHeight - canvasRadii.y - map.getTileSize());

        // Get all components
        Vector2D relativePosition = getRelativePosition(clampedFocusX, clampedFocusY, x, y);
        Vector2D rectangleCenter = new Vector2D(-w / 2, -h / 2);

        Vector2D position = Vector2D.add(relativePosition, rectangleCenter);

        graph.drawRect((int) position.x, (int) position.y, w, h);
    }

    /**
     * @brief Fill rectangle based on focused entity and centered on position.
     * @param graph The Graphics object. 
     * @param map   The Map object to use for clamping.
     * @param x     The rectangle's center's x position in absolute coordinates.
     * @param y     The rectangle's center's y position in absolute coordinates.
     * @param w     The rectangle's width.
     * @param h     The rectangle's height.
     * @param color The rectangle's color.
     */
    public void fillRectClamped(Graphics graph, Map map,double x, double y, int w, int h, Color color) {
        graph.setColor(color);

        // If nothing is focused, we draw on given position
        if (singleton.focused == null) {
            graph.fillRect((int) x, (int) y, w, h);
            return;
        }

        // Compute clamped focus
        double mapHeight = map.getHeight() * map.getTileSize() * 2;
        double mapWidth = map.getWidth() * map.getTileSize() * 2;
        Vector2D canvasRadii = singleton.canvas.getCenter();
        double focusX = singleton.focused.getPosition().x;
        double focusY = singleton.focused.getPosition().y;

        double clampedFocusX = Math.min(Math.max(focusX, canvasRadii.x), mapWidth - canvasRadii.x - map.getTileSize());
        double clampedFocusY = Math.min(Math.max(focusY, canvasRadii.y), mapHeight - canvasRadii.y - map.getTileSize());

        // Get all components
        Vector2D relativePosition = getRelativePosition(clampedFocusX, clampedFocusY, x, y);
        Vector2D rectangleCenter = new Vector2D(-w / 2, -h / 2);

        Vector2D position = Vector2D.add(relativePosition, rectangleCenter);

        graph.fillRect((int) position.x, (int) position.y, w, h);
    }
}