package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import character.Player;
import geometry.Vector2D;

public class Canvas extends JPanel {
    private boolean isFullscreen;
    private Timer timer;

    // TESTING PURPOSE
    private Player player;
    private KeyStack stack;
    private boolean wasReleasedO;
    private boolean wasReleasedSpace;
    private boolean wasReleasedI;
    private boolean wasReleasedQ;
    private boolean wasReleasedD;
    // ---------------

    public Canvas() {
        this(false);
    }

    public Canvas(boolean isFullscreen) {
        super(true);
        this.isFullscreen = isFullscreen;

        setBackground(new Color(42, 42, 42, 255));

        // TESTING PURPOSE
        this.player = new Player();
        this.stack = new KeyStack(this);
        this.wasReleasedO = true;
        this.wasReleasedSpace = true;
        this.wasReleasedI = true;
        this.wasReleasedQ = true;
        this.wasReleasedD = true;
        stack.listenTo("Z");
        stack.listenTo("S");
        stack.listenTo("Q");
        stack.listenTo("D");
        stack.listenTo("O");
        stack.listenTo("SPACE");
        stack.listenTo("I");
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
                        wasReleasedQ = true;
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
                        wasReleasedD = true;
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
                    if (wasReleasedI) {
                        player.block();
                        wasReleasedI = false;
                        wasReleasedQ = false;
                        wasReleasedD = false;
                    } else {
                        if (!wasReleasedI && wasReleasedQ || wasReleasedD) {
                            player.blockstand();
                        } else {
                            player.blockwalk();
                        }
                    }
                } else {
                    if (!wasReleasedI && player.isBlocking()) {
                        player.stopBlocking();
                    }
                    wasReleasedI = true;
                }

                player.move(movement);
                // ---------------

                repaint();
            }
        });
        timer.start();
    }

    @Override
    public Dimension getPreferredSize() {
        if (isFullscreen) {
            return Toolkit.getDefaultToolkit().getScreenSize();
        } else {
            return new Dimension(600, 600);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // TESTING PURPOSE
        g.setColor(new Color(56, 56, 56));
        for (int i = -this.getWidth() / 2; i < 3 * this.getWidth() / 2; i += 10) {
            for (int j = -this.getHeight() / 2; j < 3 * this.getHeight() / 2; j += 10) {
                if (((i + j) / 10) % 2 == 0) {
                    g.fillRect(i - (int) this.player.getPosition().x, j - (int) this.player.getPosition().y, 10, 10);
                }
            }
        }

        g.setColor(new Color(0, 255, 0));
        g.fillRect(350 - (int) this.player.getPosition().x, 350 - (int) this.player.getPosition().y, 50, 50);
        g.setColor(new Color(100, 100, 60));
        g.fillRect(370 - (int) this.player.getPosition().x, 400 - (int) this.player.getPosition().y, 10, 30);

        final int SCALE = 2;

        int[] dimensions = this.player.getSpriteSize();
        Vector2D positions = Vector2D.add(new Vector2D(220, 220), Vector2D.scale(this.player.getOffset(), SCALE));
        g.drawImage(this.player.getSprite(), (int) positions.x, (int) positions.y, dimensions[0] * SCALE,
                dimensions[1] * SCALE, this);
        // ---------------
    }
}
