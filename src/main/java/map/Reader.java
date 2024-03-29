/**
 * @brief This file contains the public class Reader.
 * 
 * @file Reader.java
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * Part of the `map` package.
 * It contains a class that allows to read a map directory and stores all the informations.
 */

package map;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;

/**
 * @class Reader
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * @brief This class allows to read a map directory and stores all the informations.
 */
public class Reader {
    // Cross functions variables

    /**
     * @brief The tilesets.
     *
     * The structure is made as follows :
     * - The **key** is the integer representing the first index.
     * - The **value** is the whole tileset.
     */
    private LinkedHashMap<Integer, BufferedImage> tilesets;
    /**
     * @brief The layers.
     *
     * The structure is made as follows :
     * - The **key** is the string representing the layer name.
     * - The **value** is the array that store the layer's tilemap.
     */
    private LinkedHashMap<String, int[]> layers;
    /** @brief The map height */
    private int height;
    /** @brief The map width */
    private int width;
    /** @brief The map tilesize */
    private int tileSize;
    /** @brief The map walls */
    private String[] walls;

    /** @brief Constant used internally to know sections flags. */
    private final static LinkedHashMap<String, Integer> dataflagDic = new LinkedHashMap<String, Integer>(){{
        this.put(":INDEX-DATA:", 0b001);
        this.put(":META-DATA:", 0b010);
        this.put(":MAP-DATA:", 0b100);
    }};

    /**
     * @brief Read given map directory containing .MAPDATA file and tilesets
     * @param mapDir The map directory
     * @throws IOException If .MAPDATA is invalid or if files are missing
     */
    public Reader(String mapDir) throws IOException {
        // Initial declarations
        tilesets = new LinkedHashMap<Integer, BufferedImage>();
        layers = new LinkedHashMap<String, int[]>();
        height = -1;
        width = -1;
        tileSize = -1;

        // File reader essentials
        BufferedReader mapdataReader = new BufferedReader(new FileReader(mapDir + ".MAPDATA"));
        String line;

        // File validation system
        int currentDataflag = -1;
        int dataflags = 0b111;

        while ((line = mapdataReader.readLine()) != null) {
            // If blank line, the interpreter just skips it
            if (line.isBlank()) {
                continue;
            }

            // If on title, get it and go next line
            if (dataflagDic.containsKey(line)) {
                currentDataflag = dataflagDic.get(line);
                dataflags ^= currentDataflag;
                continue;
            }

            // No section title has been found
            if (currentDataflag == -1) {
                mapdataReader.close();
                throw new IOException("The given file doesn't respect file specifications.");
            }

            // Loading tilesets
            if (currentDataflag == 0b001) {
                loadTileset(mapDir, line);
            }

            // Reading constants
            if (currentDataflag == 0b010) {
                String constant = line.split(" - ")[0];
                String constantValue = line.split(" - ")[1];

                if (constant.equals("HEIGHT")) {
                    height = Integer.parseInt(constantValue);
                } else if (constant.equals("WIDTH")) {
                    width = Integer.parseInt(constantValue);
                } else if (constant.equals("TILESIZE")) {
                    tileSize = Integer.parseInt(constantValue);
                } else if (constant.equals("WALLS")) {
                    walls = constantValue.split(",");
                }
            }

            // Reading layers
            if (currentDataflag == 0b100) {
                loadTileMap(line);
            }
        }

        mapdataReader.close();

        // The file was not correct
        if (dataflags != 0 || height == -1 || width == -1 || tileSize == -1) {
            throw new IOException("The given file doesn't respect file specifications.");
        }
    }

    /**
     * @brief Load tileset and starting index associated with it
     * @param mapDir The directory containing map data
     * @param line The line read by the reader
     * @throws IOException If tileset cannot be found
     */
    private void loadTileset (String mapDir, String line) throws IOException {
        // Get tileset informations
        int tilesetStart = Integer.parseInt(line.split(" - ")[0]);
        String tilesetName = line.split(" - ")[1];

        // Load tileset image and store it
        BufferedImage tileset = ImageIO.read(new File(mapDir + tilesetName));
        tilesets.put(tilesetStart, tileset);
    }

    /**
     * @brief Load tile map in layer set
     * @param data The data line read in the .MAPDATA file
     */
    private void loadTileMap (String data) {
        int identifierEnd = data.indexOf("#", 1);
        String identifier = data.substring(1, identifierEnd);

        // Process layer
        data = data.substring(identifierEnd + 2);

        String[] tilemapRaw = data.split(",");
        int[] tilemapProcessed = new int[tilemapRaw.length];
      
        for (int i = 0 ; i < tilemapRaw.length ; i++) {
            tilemapProcessed[i] = Integer.parseInt(tilemapRaw[i]);
        }

        // Add layer
        layers.put(identifier, tilemapProcessed);
    }

    /**
     * @brief Getter function for tilesets
     * @return The tilesets
     */
    public LinkedHashMap<Integer, BufferedImage> getTilesets() {return tilesets;}

    /**
     * @brief Getter function for layers
     * @return The layers
     */
    public LinkedHashMap<String, int[]> getLayers() {return layers;}

    /**
     * @brief Getter function for width
     * @return Map width
     */
    public int getWidth() {return width;}

    /**
     * @brief Getter function for height
     * @return Map height
     */
    public int getHeight() {return height;}

    /**
     * @brief Getter function for tile size
     * @return the tile size
     */
    public int getTileSize() {return tileSize;}

    /**
     * @brief Getter function for wall layers
     * @return the wall layer names
     */
    public String[] getWalls() {return walls;}
}