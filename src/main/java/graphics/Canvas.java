package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import java.awt.Toolkit;
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
    private boolean wasReleasedD;
    private boolean wasReleasedQ;
    private boolean wasReleasedS;
    private boolean wasReleasedZ;
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
        this.wasReleasedD = true;
        this.wasReleasedQ = true;
        this.wasReleasedZ = true;
        this.wasReleasedS = true;

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
                    if (!wasReleasedI && (stack.isPressed("I"))) {
                        // maintien de run + débuter le block
                        movement.y -= 4;
                        player.blockwalk();
                        wasReleasedI = true;
                    } else {
                        movement.y -= 4;
                    }
                }
                if (stack.isPressed("S")) {
                    if (!wasReleasedI && (stack.isPressed("I"))) {
                        // maintien de run + débuter le block
                        movement.y += 4;
                        player.blockwalk();
                        wasReleasedI = true;
                    } else {
                        movement.y += 4;
                    }
                }
                if (stack.isPressed("Q")) {
                    if (!wasReleasedI && (stack.isPressed("I"))) {
                        // maintien de run + débuter le block
                        movement.x -= 4;
                        player.blockwalk();
                        wasReleasedI = true;
                    } else {
                        movement.x -= 4;
                    }
                }

                if (stack.isPressed("D")) {
                    if (!wasReleasedI && (stack.isPressed("I"))) {
                        // maintien de run + débuter le block
                        movement.x += 4;
                        player.blockwalk();
                        wasReleasedI = true;
                    } else {
                        movement.x += 4;
                    }
                }
                if (stack.isPressed("O")) {
                    if (wasReleasedO && !player.isDodging()) {
                        player.attack();
                        wasReleasedO = true;
                    }
                } else {
                    wasReleasedO = true;
                }
                if (stack.isPressed("SPACE")) {
                    if (wasReleasedSpace) {
                        player.dodge();
                        wasReleasedSpace = false;
                    }
                } else {
                    wasReleasedSpace = true;
                }

                if (stack.isPressed("I")) {
                    // player.blockstand();
                    wasReleasedI = false;
                } else {
                    if (wasReleasedI && player.isBlocking()) {
                        player.stopBlocking();
                    }
                    wasReleasedI = true;
                }

                if (!wasReleasedI && !wasReleasedQ && (stack.isPressedCombination("Q", "I"))) {
                    player.blockwalk();
                    wasReleasedQ = true;
                    wasReleasedI = true;
                } else if (!wasReleasedI && !wasReleasedD && (stack.isPressedCombination("D", "I"))) {
                    player.blockwalk();
                    wasReleasedD = true;
                    wasReleasedI = true;
                } else if (!wasReleasedI && !wasReleasedS && (stack.isPressedCombination("S", "I"))) {
                    player.blockwalk();
                    wasReleasedS = true;
                    wasReleasedI = true;
                } else if (!wasReleasedI && !wasReleasedZ && (stack.isPressedCombination("Z", "I"))) {
                    player.blockwalk();
                    wasReleasedZ = true;
                    wasReleasedI = true;
                }

                player.move(movement);
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

        // Different origin --> different placement for the rectangle
        Vector2D offset = player.getOffset();

        double centerX = player.getPosition().x - offset.x * SCALE;
        double centerY = player.getPosition().y - offset.y * SCALE;

        int rectWidth = player.getSprite().getWidth(); // reduce hitbox size by note scaling rectangle
        int rectHeight = player.getSprite().getHeight(); // /!\ see what's better when collisions are done

        camera.drawRect(g, centerX, centerY, rectWidth, rectHeight, Color.RED);

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

}
