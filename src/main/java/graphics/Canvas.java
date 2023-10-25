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
    private Player player2;
    private KeyStack stack;
    private boolean wasReleasedO;
    private boolean wasReleasedSpace;
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
        this.player2 = new Player();
        this.stack = new KeyStack(this);
        this.wasReleasedO = true;
        this.wasReleasedSpace = true;
        stack.listenTo("Z");
        stack.listenTo("S");
        stack.listenTo("Q");
        stack.listenTo("D");
        stack.listenTo("O"); 
        stack.listenTo("SPACE");
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
                if (stack.isPressed("O")) {
                    if (wasReleasedO && !player.isDodging()) {
                        player.attack();
                        wasReleasedO = false;
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

                player.move(movement);
                // ---------------


                Vector2D difference = Vector2D.add(player.getPosition(), Vector2D.scale(player2.getPosition(), -1));
 
                if (difference.norm() > 120) {
                    difference.normalize();
                    player2.move(Vector2D.scale(difference, 3));
                } else {
                    player2.move(0, 0);
                }
                
                
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
        for (int i = -this.getWidth() / 256 ; i < 3 * this.getWidth() / 256 ; i ++) {
            for (int j = -this.getHeight() / 256 ; j < 3 * this.getHeight() / 256 ; j++) {
                if ((i + j) % 2 == 0) {
                    g.fillRect(i * 128 - (int)this.player.getPosition().x, j * 128 - (int)this.player.getPosition().y, 128, 128);
                }
            }
        }    

        // g.setColor(new Color(0, 255, 0));
        // g.fillRect(350 - (int)this.player.getPosition().x, 350 - (int)this.player.getPosition().y, 50, 50);
        // g.setColor(new Color(100, 100, 60));
        // g.fillRect(370 - (int)this.player.getPosition().x, 400 - (int)this.player.getPosition().y, 10, 30);

        g.drawImage(this.player2.getSprite(), (int)(this.getWidth() / 2 - 64 + this.player2.getPosition().x - this.player.getPosition().x), (int)(this.getHeight() / 2 - 64 + this.player2.getPosition().y - this.player.getPosition().y), 128, 128, this);

        final int SCALE = 2;

        int[] dimensions = this.player.getSpriteSize();
        Vector2D positions = Vector2D.add(new Vector2D(this.getWidth() / 2 - 32 * SCALE, this.getHeight() / 2 - 32 * SCALE), Vector2D.scale(this.player.getOffset(), SCALE));
        g.drawImage(this.player.getSprite(), (int)positions.x, (int)positions.y, dimensions[0] * SCALE, dimensions[1] * SCALE, this);
        // ---------------
    }
}
