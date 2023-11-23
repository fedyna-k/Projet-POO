package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import java.awt.Toolkit;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

import character.Player;
import geometry.Vector2D;
import map.Map;

public class Canvas extends JPanel {
    private boolean isFullscreen;
    private Timer timer;
    private Camera camera;

    // TESTING PURPOSE
    private Player player;
    private Player player2;
    private KeyStack stack;
    private boolean wasReleasedO;
    private boolean wasReleasedSpace;
    private boolean wasReleasedI;

    private int previousValidX;
    private int previousValidY;
    private Map map;

    // ---------------

    public Canvas() {
        this(false);
    }

    public Canvas(boolean isFullscreen) {
        super(true);
        this.isFullscreen = isFullscreen;

        this.camera = Camera.getCamera(this);
        setBackground(new Color(42, 42, 42, 255));

        // TESTING PURPOSE
        this.player = new Player(0, 0);
        this.player2 = new Player(0, 0);
        this.map = new Map("../src/main/resources/map/");

        this.stack = new KeyStack(this);
        this.wasReleasedO = true;
        this.wasReleasedSpace = true;
        this.wasReleasedI = true;

        stack.listenTo("Z");
        stack.listenTo("S");
        stack.listenTo("Q");
        stack.listenTo("D");
        stack.listenTo("O");
        stack.listenTo("SPACE");
        stack.listenTo("I");

        this.camera.setFocusOn(player);
        // ---------------

        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // TESTING PURPOSE
                Vector2D movement = new Vector2D();
                if (stack.isPressed("Z")) {
                    movement.y -= 4;
                }
                if (stack.isPressed("S")) {
                    movement.y += 4;
                }
                if (stack.isPressed("Q")) {
                    movement.x -= 4;
                }

                if (stack.isPressed("D")) {
                    movement.x += 4;
                }
                if (stack.isPressed("O") && !checkCollision(player.getPosition())) {
                    if (wasReleasedO && !player.isDodging()) {
                        player.attack();
                        wasReleasedO = true;
                    }
                } else {
                    wasReleasedO = true;
                }
                if (stack.isPressed("SPACE") && !checkCollision(player.getPosition())) {
                    if (wasReleasedSpace) {
                        player.dodge();
                        wasReleasedSpace = false;
                    }
                } else {
                    wasReleasedSpace = true;
                }

                if (stack.isPressed("I") && wasReleasedI) {
                    player.block();
                    wasReleasedI = false;
                }

                if (!stack.isPressed("I") && !wasReleasedI) {
                    player.stopBlocking();
                    wasReleasedI = true;
                }

                repaint();

                // collision
                Vector2D newPosition = Vector2D.add(player.getPosition(), movement);
                if (!checkCollision(newPosition)) {
                    player.move(movement);
                }

                // ---------------

                Vector2D difference = Vector2D.add(player.getPosition(), Vector2D.scale(player2.getPosition(), -1));

                if (difference.norm() > 120) {
                    difference.normalize();
                    player2.move(Vector2D.scale(difference, 3));
                } else {
                    player2.move(0, 0);
                }

            }
        });

        timer.addActionListener(e -> repaint());
        timer.start();
    }

    @Override
    public Dimension getPreferredSize() {
        if (isFullscreen) {
            return Toolkit.getDefaultToolkit().getScreenSize();
        } else {
            return new Dimension(800, 600);
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // TESTING PURPOSE
        // g.setColor(new Color(56, 56, 56));
        // for (int i = -this.getWidth() / 256 ; i < 3 * this.getWidth() / 256 ; i ++) {
        // for (int j = -this.getHeight() / 256 ; j < 3 * this.getHeight() / 256 ; j++)
        // {
        // if ((i + j) % 2 == 0) {
        // g.fillRect(i * 128 - (int)this.player.getPosition().x, j * 128 -
        // (int)this.player.getPosition().y, 128, 128);
        // }
        // }
        // }

        int SCALE = isFullscreen ? 4 : 2;
        int tileSize = map.getTileSize() * SCALE;

        for (int i = (int) this.player.getPosition().x / (32 * SCALE) - 9; i < (int) this.player.getPosition().x
                / (32 * SCALE) + 10; i++) {
            for (int j = (int) this.player.getPosition().y / (32 * SCALE) - 6; j < (int) this.player.getPosition().y
                    / (32 * SCALE) + 7; j++) {
                BufferedImage tile = map.getTile(i, j);

                if (tile != null) {
                    this.camera.drawImage(g, map.getTile(i, j), i * 32 * SCALE, j * 32 * SCALE, SCALE);
                }
            }
        }

        this.camera.drawImage(g, this.player2.getSprite(), this.player2.getPosition().x, this.player2.getPosition().y,
                SCALE, this.player2.getOffset());
        this.camera.drawImage(g, this.player.getSprite(), this.player.getPosition().x, this.player.getPosition().y,
                SCALE, this.player.getOffset());

        /* Drawing Hitbox */

        // hitbox player
        double centerX = player.getPosition().x;
        double centerY = player.getPosition().y;

        int rectWidth = (int) (64 * SCALE / 1.9);
        int rectHeight = (int) (64 * SCALE / 1.5);

        camera.drawRect(g, centerX, centerY, rectWidth, rectHeight, Color.RED);

        // hitbox sword
        Vector2D offset = player.getOffset();
        double centerswordX;
        double centerswordY = player.getPosition().y - offset.y * SCALE;

        int spriteWidth = player.getSprite().getWidth();

        int swordWidth = (int) (spriteWidth / 2);
        int swordHeight = (int) (player.getSprite().getHeight() * SCALE / 2);

        if (player.isAttacking()) {
            if (player.isFacingLeft()) {
                // Sword on the left side attacking
                centerswordX = player.getPosition().x - offset.x * SCALE - 96;
                swordWidth = (int) (spriteWidth * 2);
            } else {
                // Sword on the right side attacking
                centerswordX = player.getPosition().x - offset.x * SCALE + 96;
                swordWidth = (int) (spriteWidth * 2);
            }

            camera.drawRect(g, centerswordX, centerswordY, swordWidth, swordHeight, Color.RED);
        }

        // else {
        // // Sword on the right side
        // centerswordX = player.getPosition().x - offset.x * SCALE + 64;
        // }

        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                // draw rectangle around walls
                if (map.isWall(i, j)) {
                    int tileX = i * tileSize;
                    int tileY = j * tileSize;
                    camera.drawRect(g, tileX, tileY, tileSize, tileSize, Color.RED);
                }
            }
        }

        /* End of Drawing Hitbox */

        // this.camera.showCam(g, player2, player);
        // ---------------
    }

    /**
     * Get Canvas center point
     * 
     * @return A Vector2D containing the point
     */
    public Vector2D getCenter() {
        return new Vector2D(this.getWidth() / 2, this.getHeight() / 2);
    }

    /**
     * @brief Checks for collisions with walls and enemies based on the given new
     *        position.
     *
     *        Checks if a collision occurs with walls or enemies at the
     *        specified position. It considers the player's hitbox and, if
     *        attacking, the sword's hitbox. The collision is determined by checking
     *        intersections with the game map's walls and potential enemy hitboxes.
     *
     * @param newPosition The new position to check for collisions.
     * @return True if a collision is detected, indicating the player cannot move to
     *         the new position; otherwise, false.
     */
    private boolean checkCollision(Vector2D newPosition) {
        int SCALE = isFullscreen ? 4 : 2;
        int tileSize = map.getTileSize() * SCALE;
        int newPosX = (int) newPosition.x;
        int newPosY = (int) newPosition.y;

        // Hitbox player
        int rectWidth = (int) (64 * SCALE / 1.9);
        int rectHeight = (int) (64 * SCALE / 1.5);
        Rectangle playerRect = new Rectangle(newPosX, newPosY, rectWidth, rectHeight);

        // Hitbox sword
        Vector2D offset = player.getOffset();
        double centerswordX;
        double centerswordY = newPosY - offset.y * SCALE;

        int spriteWidth = player.getSprite().getWidth();
        int swordWidth = (int) (spriteWidth / 2);
        int swordHeight = (int) (player.getSprite().getHeight() * SCALE / 1.5);

        if (player.isAttacking()) {
            if (player.isFacingLeft()) {
                // Sword on the left side attacking
                centerswordX = newPosX - offset.x * SCALE - 96;
                swordWidth = (int) (spriteWidth * 2);
            } else {
                // Sword on the right side attacking
                centerswordX = newPosX - offset.x * SCALE + 96;
                swordWidth = (int) (spriteWidth * 2);
            }

            Rectangle swordRect = new Rectangle((int) centerswordX, (int) centerswordY, swordWidth, swordHeight);

            // Check collision with sword
            // if (swordRect.intersects(monsterRect)) {
            // // Handle collision when player is attacking
            // return true;
            // }
        }

        // Check collision with walls
        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                if (map.isWall(i, j)) {
                    int tileX = i * tileSize;
                    int tileY = j * tileSize;
                    Rectangle tileRect = new Rectangle(tileX, tileY, tileSize, tileSize);

                    if (playerRect.intersects(tileRect)) {
                        if (player.isDodging()) {
                            newPosition.x = previousValidX;
                            newPosition.y = previousValidY;
                            player.setDodging(false);
                            return true;
                        }
                        return true;
                    }
                }
            }
        }

        previousValidX = (int) newPosition.x;
        previousValidY = (int) newPosition.y;
        return false;
    }

}
