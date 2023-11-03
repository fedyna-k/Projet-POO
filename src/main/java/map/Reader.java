package map;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;

public class Reader {
    // Cross functions variables
    private ArrayList<BufferedImage> tilesets;
    private LinkedHashMap<Integer, BufferedImage> tilesetsStarts;
    private LinkedHashMap<String, int[]> layers;

    // Constant used to know sections flags
    private final static LinkedHashMap<String, Integer> dataflagDic = new LinkedHashMap<String, Integer>(){{
        this.put(":INDEX-DATA:", 0b001);
        this.put(":META-DATA:", 0b010);
        this.put(":MAP-DATA:", 0b100);
    }};

    /**
     * Read given map directory containing .MAPDATA file and tilesets
     * @param mapDir The map directory
     * @throws IOException If .MAPDATA is invalid or if files are missing
     */
    public Reader(String mapDir) throws IOException {
        // Initial declarations
        tilesets = new ArrayList<BufferedImage>();
        tilesetsStarts = new LinkedHashMap<Integer, BufferedImage>();
        layers = new LinkedHashMap<String, int[]>();

        // File reader essentials
        BufferedReader mapdataReader = new BufferedReader(new FileReader(mapDir + ".MAPDATA"));
        String line;
        
        // File validation system
        int currentDataflag = -1;
        int dataflags = 0b111;

        // Map constants we must find
        int height = -1, width = -1, tileSize = -1;

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
                int constantValue = Integer.parseInt(line.split(" - ")[1]);

                if (constant.equals("HEIGHT")) {
                    height = constantValue;
                } else if (constant.equals("WIDTH")) {
                    width = constantValue;
                } else if (constant.equals("TILESIZE")) {
                    tileSize = constantValue;
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
     * Load tileset and starting index associated with it
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
        tilesets.add(tileset);
        tilesetsStarts.put(tilesetStart, tileset);
    }

    /**
     * Load tile map in layer set
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
}
