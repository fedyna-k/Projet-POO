package map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashSet;

public class Map {
    private Reader mapReader;
    private HashSet<Integer> wallTiles;

    public Map(String mapDir) {
        // Load map using reader class
        try {
            mapReader = new Reader(mapDir);
        } catch (IOException e) {
            System.out.println(e);
            return;
        }

        wallTiles = new HashSet<>();

        int[] wallIds = { 684, 1411, 1412, 1435, 537, 538, 539, 540, 586, 585, 594, 1401, 1283, 692, 593, 1419, 1420,
                1443, 545, 546, 555, 556, 681, 682, 683, 590, 693, 1427, 1428, 1451, 689, 695, 690, 691, 1452, 1468,
                1469, 1437, 697, 698, 699, 1460, 1476, 1477, 1445, 1597, 700, 1598, 1664, 1662, 686, 1663, 694, 541,
                542, 1357, 702, 547, 548, 549, 550, 1365, 1582, 756, 1583, 1453, 696, 1461, 573, 581, 739, 745, 741,
                749, 740, 737, 579, 687, 1726, 1393, 688, 703, 747, 748, 1413, 1414, 791, 792, 807, 808, 775, 776, 1421,
                1422, 799, 800, 815, 816, 783, 784, 1436, 1429, 1430, 1444, 1480, 641, 642, 643, 1457, 1458, 1459, 649,
                651, 1728, 1465, 1466, 1467, 1473, 1474, 1475, 757, 663, 1409, 1410, 707, 713, 708, 1385, 671, 1417,
                1418, 705, 597, 572, 1386, 1425, 1426, 679, 543, 544, 559, 560, 714, 758, 551, 552, 657, 658, 766, 715,
                716, 633, 634, 635, 1396, 592, 600, 565, 566, 553, 554, 596, 793, 794, 795, 529, 1599, 1600, 809, 810,
                811, 1433, 1727, 1441, 570, 1449, 571, 569, 574, 1982, 1983, 1984, 2046, 2047, 2048, 582, 584, 2110,
                2298, 2112, 2045, 563, 564, 562, 857, 859, 255, 1561, 2053, 865, 867, 1589, 1569, 873, 875, 1391, 589,
                587, 588, 2111, 583, 591 };

        for (int id : wallIds) {
            wallTiles.add(id);
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

    private int getTileId(int x, int y) {
        if (x < 0 || y < 0 || x >= mapReader.getWidth() || y >= mapReader.getHeight()) {
            return -1;
        }
        int tileCoordinate = y * mapReader.getWidth() + x;
        for (int[] layer : mapReader.getLayers().values()) {
            int tileIdForLayer = layer[tileCoordinate];
            if (tileIdForLayer != 0) {
                return tileIdForLayer;
            }
        }
        return 0;
    }

    public boolean isWall(int x, int y) {
        int tileId = getTileId(x, y);
        return wallTiles.contains(tileId);
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