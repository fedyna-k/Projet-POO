package map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Map {
    private Reader mapReader;

    public Map(String mapDir) {
        // Load map using reader class
        try {
            mapReader = new Reader(mapDir);
        } catch (IOException e) {
            System.out.println(e);
            return;
        }
    }

    public BufferedImage getTile(int x, int y) {
        // Check if coordinates are ok
        if (x >= mapReader.getWidth() || y >= mapReader.getHeight() || x < 0 || y < 0) {
            return null;
        }
        int tileCoordinate = y * mapReader.getWidth() + x;

        // Create tile and painter
        BufferedImage finalTile = new BufferedImage(mapReader.getTileSize(), mapReader.getTileSize(),
                BufferedImage.TYPE_INT_ARGB);
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
     * Get tile for given id
     * 
     * @param id The id of the tile, must be not null
     * @return
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
            tile = tileset.getSubimage(x * mapReader.getTileSize(), y * mapReader.getTileSize(),
                    mapReader.getTileSize(), mapReader.getTileSize());
        } catch (Exception e) {
        }

        return tile;
    }

    public boolean isWall(int x, int y) {
        int[] wallsLayer = mapReader.getLayers().get("WALLS");
        if (wallsLayer == null) {
            // layer null
            return false;
        }

        int tileIndex = y * mapReader.getWidth() + x;
        if (tileIndex < 0 || tileIndex >= wallsLayer.length) {
            return false;
        }

        int tileId = wallsLayer[tileIndex];
    
        return tileId != 0;
    }

    public int getTileSize() {
        return mapReader.getTileSize();
    }

    public int getWidth() {
        return mapReader.getWidth();
    }

    public int getHeight() {
        return mapReader.getHeight();
    }
}