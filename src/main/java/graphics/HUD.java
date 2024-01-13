package graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import character.Entity;
import character.Player;
import map.Map;

public class HUD {
    static public void drawEntityHealth(Graphics g, Camera camera, Map map, Entity entity, int scale) {
        double healthPercent = entity.getStats().getHealth().getPercent();
        int healthLength = (int)(entity.getSpriteSize().x * healthPercent);
        int healthOffset = (int)(entity.getSpriteSize().x * (1 - healthPercent) / 2);
                    
        camera.fillRectClamped(g, map, entity.getPosition().x, entity.getPosition().y - (int)(entity.getSpriteSize().y / 1.2), (int)entity.getSpriteSize().x, 1 * scale, Color.lightGray);
        camera.fillRectClamped(g, map, entity.getPosition().x - healthOffset, entity.getPosition().y - (int)(entity.getSpriteSize().y / 1.2), healthLength, 1 * scale, new Color((int)(255 *  (1 - healthPercent)), (int)(255 *  healthPercent), 0));
        camera.drawTextClamped(g, map, (int)entity.getPosition().x - (int)(entity.getSpriteSize().x / 2), (int)entity.getPosition().y - (int)(entity.getSpriteSize().y / 1.15), entity.getStats().getHealth().get() + "/" + entity.getStats().getHealth().getMax(), 8, Color.white);
        camera.drawTextClamped(g, map, (int)entity.getPosition().x - (int)(entity.getSpriteSize().x / 2), (int)entity.getPosition().y - (int)(entity.getSpriteSize().y), (int)entity.getStats().getAttack() + "/" + (int)entity.getStats().getDefence() + "/" + (int)entity.getStats().getSpeed(), 8, Color.white);
    }

    static public void drawEntityCooldown(Graphics g, Camera camera, Map map, Entity entity, int scale) {
        double cooldownPercent = 1d * entity.attackCooldown / (1000 - entity.getStats().getSpeed() * 80); // 50 * (1000 - this.stats.getSpeed() * 80);  // <-- Linux
        if (cooldownPercent == 0) {
            return;
        }

        int cdLength = (int)(entity.getSpriteSize().x * cooldownPercent);
        int cdOffset = (int)(entity.getSpriteSize().x * (1 - cooldownPercent) / 2);
                    
        camera.fillRectClamped(g, map, entity.getPosition().x, entity.getPosition().y - (int)(entity.getSpriteSize().y / 1.3), (int)entity.getSpriteSize().x, 1 * scale, Color.lightGray);
        camera.fillRectClamped(g, map, entity.getPosition().x - cdOffset, entity.getPosition().y - (int)(entity.getSpriteSize().y / 1.3), cdLength, 1 * scale, new Color((int)(255 *  (1 - cooldownPercent)), (int)(255 *  cooldownPercent), 0));
    }

    static public void drawPlayerHealth(Graphics g, Camera camera, Player player) {
        g.setColor(Color.lightGray);
        g.fillPolygon(new int[]{20, 220, 210, 10}, new int[]{10, 10, 40, 40}, 4);
        g.setColor(new Color((int)(255 *  (1 - player.getStats().getHealth().getPercent())), (int)(255 *  player.getStats().getHealth().getPercent()), 0));
        g.fillPolygon(new int[]{20, 20 + (int)(200 * player.getStats().getHealth().getPercent()), 10 + (int)(200 * player.getStats().getHealth().getPercent()), 10}, new int[]{10, 10, 40, 40}, 4);
        g.setColor(Color.black);
        g.drawPolygon(new int[]{20, 220, 210, 10}, new int[]{10, 10, 40, 40}, 4);
        camera.drawTextFixed(g, 27, 27, player.getStats().getHealth().get() + "/" + player.getStats().getHealth().getMax(), 15, Color.black);
        camera.drawTextFixed(g, 25, 25, player.getStats().getHealth().get() + "/" + player.getStats().getHealth().getMax(), 15, Color.white);
    }

    static public void drawStat(Graphics g, Camera camera, double stat, String label, int xlbl, int ylbl, int x, int y) {
        camera.drawTextFixed(g, xlbl + 2, ylbl + 2, label, 15, Color.black);
        camera.drawTextFixed(g, xlbl, ylbl, label, 15, Color.white);

        for (int i = 0 ; i < (int)Math.floor(stat) + 1 ; i++) {
            g.setColor(Color.lightGray);
            g.fillPolygon(new int[]{i * 20 + x + 5, i * 20 + x + 10, i * 20 + x + 5, i * 20 + x}, new int[]{y, y + 10, y + 20, y + 10}, 4);

            g.setColor(new Color(49, 183, 255));
            for (int j = 1 ; j <= 4 ; j++) {
                if (i < (int)Math.floor(stat) || j <= (stat * 4) % 4) {
                    if (j == 1) {
                        g.fillPolygon(new int[]{i * 20 + x + 5, i * 20 + x, i * 20 + x + 5}, new int[]{y, y + 10, y + 10}, 3);
                    }
                    if (j == 2) {
                        g.fillPolygon(new int[]{i * 20 + x + 5, i * 20 + x, i * 20 + x + 5}, new int[]{y + 10, y + 10, y + 20}, 3);
                    }
                    if (j == 3) {
                        g.fillPolygon(new int[]{i * 20 + x + 5, i * 20 + x + 10, i * 20 + x + 5}, new int[]{y + 10, y + 10, y + 20}, 3);
                    }
                    if (j == 4) {
                        g.fillPolygon(new int[]{i * 20 + x + 5, i * 20 + x + 10, i * 20 + x + 5}, new int[]{y, y + 10, y + 10}, 3);
                    }
                }
            }

            g.setColor(Color.black);
            g.drawPolygon(new int[]{i * 20 + x + 5, i * 20 + x + 10, i * 20 + x + 5, i * 20 + x}, new int[]{y, y + 10, y + 20, y + 10}, 4);
        }
    }

    static public void drawXP(Graphics g, Camera camera, Canvas canvas, Player player) {
        camera.drawTextFixed(g, canvas.getWidth() - 218, 32, "Level " + player.level, 18, Color.black);
        camera.drawTextFixed(g, canvas.getWidth() - 220, 30, "Level " + player.level, 18, Color.white);

        if (player.skillPoints > 0) {
            camera.drawTextFixed(g, canvas.getWidth() - 58, 32, player.skillPoints + "SP", 18, Color.black);
            camera.drawTextFixed(g, canvas.getWidth() - 60, 30, player.skillPoints + "SP", 18, Color.white);
        }

        g.setColor(Color.lightGray);
        g.fillPolygon(new int[]{canvas.getWidth() - 20, canvas.getWidth() - 220, canvas.getWidth() - 210, canvas.getWidth() - 10}, new int[]{50, 50, 80, 80}, 4);
        g.setColor(Color.blue);

        double xpPercent = 1d * player.xp / (player.level * 250 + 500);

        g.fillPolygon(new int[]{canvas.getWidth() - 220, canvas.getWidth() - 210, canvas.getWidth() - (int)(200 * (1 - xpPercent)) - 10, canvas.getWidth() - (int)(200 * (1 - xpPercent)) - 20}, new int[]{50, 80, 80, 50}, 4);
        
        g.setColor(Color.black);
        g.drawPolygon(new int[]{canvas.getWidth() - 20, canvas.getWidth() - 220, canvas.getWidth() - 210, canvas.getWidth() - 10}, new int[]{50, 50, 80, 80}, 4);
    }

    static public void drawCommands(Graphics g, Camera camera, Canvas canvas) {
        camera.drawTextFixed(g, canvas.getWidth() / 2 - (canvas.isFullscreen ? 498 : 378), canvas.getHeight() - 28, "Move - ZQSD   Dodge - Space   Attack - O   Block - I   Use Skill Point - KLM", canvas.isFullscreen ? 14 : 10, Color.black);
        camera.drawTextFixed(g, canvas.getWidth() / 2 - (canvas.isFullscreen ? 500 : 380), canvas.getHeight() - 30, "Move - ZQSD   Dodge - Space   Attack - O   Block - I   Use Skill Point - KLM", canvas.isFullscreen ? 14 : 10, Color.white);
    }

    static public void drawDialog(Graphics g, Camera camera, Canvas canvas, String text) {
        g.setColor(Color.black);
        g.fillRoundRect(10, canvas.getHeight() - 150, canvas.getWidth() - 20, 140, 20, 20);
        
        g.setColor(Color.white);
        g.fillRoundRect(20, canvas.getHeight() - 140, canvas.getWidth() - 40, 120, 10, 10);

        final int STR_LENGTH = (canvas.getWidth() - 80) / 24;
        
        ArrayList<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        String line = "";
        int len = 0;

        for (String word : words) {
            if (len + word.length() > STR_LENGTH) {
                lines.add(line);
                line = "";
                len = 0;
            }

            line += word + " ";
            len += word.length() + 1;
        }
        lines.add(line);

        for (int i = 0 ; i < Math.min(lines.size(), 3) ; i++) {
            camera.drawTextFixed(g, 40, canvas.getHeight() - 100 + i * 30, lines.get(i), 24, Color.black);
        }

        g.fillRect(canvas.getWidth() - 50, canvas.getHeight() - 50, 4, 10);
        g.fillRect(canvas.getWidth() - 70, canvas.getHeight() - 40, 24, 4);
        g.fillPolygon(new int[]{canvas.getWidth() - 80, canvas.getWidth() - 70, canvas.getWidth() - 70}, new int[]{canvas.getHeight() - 38, canvas.getHeight() - 44, canvas.getHeight() - 32}, 3);
    }
}
