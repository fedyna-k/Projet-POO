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
import java.util.LinkedHashMap;


/**
 * @class Map
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * @brief This class allows to read a map directory and get tile on position given.
 */
public class Map {
    /** @brief Stores all tiles */
    private BufferedImage[] tiles;
    /** @brief The map tiles size */
    private int tileSize;
    /** @brief The map width in tile unit (tu) */
    private int width;
    /** @brief The map height in tile unit (tu) */
    private int height;
    /**
     * @brief The layers.
     *
     * The structure is made as follows :
     * - The **key** is the string representing the layer name.
     * - The **value** is the array that store the layer's tilemap.
     */
    private LinkedHashMap<String, int[]> layers;

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
        Reader mapReader;
        // Load map using reader class
        try {
            mapReader = new Reader(mapDir);
        } catch (IOException e) {
            System.out.println(e);
            return;
        }

        // Get all basic info
        tileSize = mapReader.getTileSize();
        width = mapReader.getWidth();
        height = mapReader.getHeight();
        layers = mapReader.getLayers();

        // ---- Split tilesets ----

        // Get number of tiles and create array
        int last_index = mapReader.getTilesets().lastEntry().getKey();
        int last_height = mapReader.getTilesets().lastEntry().getValue().getHeight();
        int last_width = mapReader.getTilesets().lastEntry().getValue().getWidth();

        tiles = new BufferedImage[last_index + last_height * last_width / (tileSize * tileSize) - 1];

        // Loop through all tilesets
        for (var entry : mapReader.getTilesets().entrySet()) {
            // Define array initial index
            int k = entry.getKey() - 1;
            
            // Loop through current tileset
            for (int i = 0 ; i < entry.getValue().getHeight() / tileSize ; i++) {
                for (int j = 0 ; j < entry.getValue().getWidth() / tileSize ; j++) {
                    // Add tile to tile array
                    tiles[k] = entry.getValue().getSubimage(j * tileSize, i * tileSize, tileSize, tileSize);
                    k++;
                }
            }
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
        if (x >= width || y >= height || x < 0 || y < 0) {
            return null;
        }
        int tileCoordinate = y * width + x;
        
        // Create tile and painter
        BufferedImage finalTile = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D painter = finalTile.createGraphics();
        
        // Get all layers done
        for (int[] layer : layers.values()) {
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
        return tiles[id - 1];
    }
}