/**
 * @brief This file contains the public class Map.
 * 
 * @file Map.java
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * Part of the `map` package.
 * It contains a class that allows to read a map directory and get tile on position given.
 */

package map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;


/**
 * @class Map
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * @brief This class allows to read a map directory and get tile on position given.
 */
public class Map {
    /** @brief Instance of finite state machine that reads the file. */
    private Reader mapReader;

    /**
     * @brief Map constructor.
     * 
     * While looking through the map directory, the map should find :
     * - A `.MAPDATA` file.
     * - All tilesets used in the .MAPDATA.
     * 
     * @param mapDir The path to the map directory.
     * @warning If something is wrong with the map directory, it will log an error but raise it.
     */
    public Map(String mapDir) {
        // Load map using reader class
        try {
            mapReader = new Reader(mapDir);
        } catch (IOException e) {
            System.out.println(e);
            return;
        }
    }

    /**
     * @brief Method to get the wanted tile.
     * 
     * When a tile is asked, it will compute all the layers and return
     * the final tile.
     * 
     * @param x The x coordinate in the map grid.
     * @param y The y coordinate in the map grid.
     * @return The computed tile.
     */
    public BufferedImage getTile(int x, int y) {
        // Check if coordinates are ok
        if (x >= mapReader.getWidth() || y >= mapReader.getHeight() || x < 0 || y < 0) {
            return null;
        }
        int tileCoordinate = y * mapReader.getWidth() + x;
        
        // Create tile and painter
        BufferedImage finalTile = new BufferedImage(mapReader.getTileSize(), mapReader.getTileSize(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D painter = finalTile.createGraphics();
        
        // Get all layers done
        for (int[] layer : mapReader.getLayers().values()) {
            int tileIdForLayer = layer[tileCoordinate];
            if (tileIdForLayer != 0) {
                painter.drawImage(getTileById(tileIdForLayer), null, 0, 0);
            }
        }

        painter.dispose();
        return finalTile;
    }

    /**
     * @brief Get tile for given id.
     * @param id The id of the tile, must be not null.
     * @return The tile in the tileset corresponding to the given id.
     */
    private BufferedImage getTileById(int id) {
        int actualIndex = 0;
        BufferedImage tileset = null;

        // Check all tilesets
        for (var entry : mapReader.getTilesets().entrySet()) {
            // 
            if (entry.getKey() < id) {
                actualIndex = id - entry.getKey();
                tileset = entry.getValue();
            } else {
                break;
            }
        }

        // Compute coordinate on tileset
        int x = actualIndex % (tileset.getWidth() / mapReader.getTileSize());
        int y = actualIndex / (tileset.getWidth() / mapReader.getTileSize());

        // Get tile
        BufferedImage tile = null;

        try {
            tile = tileset.getSubimage(x * mapReader.getTileSize(), y * mapReader.getTileSize(), mapReader.getTileSize(), mapReader.getTileSize());
        } catch (Exception e) {
        }

        return tile;
    }
}