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
    private Camera camera;

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
        
        this.camera = Camera.getCamera(this);
        setBackground(new Color(42, 42, 42, 255));

        // TESTING PURPOSE
        this.player = new Player(800, 500);
        this.player2 = new Player(800, 500);
        this.stack = new KeyStack(this);
        this.wasReleasedO = true;
        this.wasReleasedSpace = true;
        stack.listenTo("Z");
        stack.listenTo("S");
        stack.listenTo("Q");
        stack.listenTo("D");
        stack.listenTo("O"); 
        stack.listenTo("SPACE");

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


        int SCALE = 2;

        this.camera.drawImage(g, this.player2.getSprite(), this.player2.getPosition().x, this.player2.getPosition().y, SCALE, this.player2.getOffset());
        this.camera.drawImage(g, this.player.getSprite(), this.player.getPosition().x, this.player.getPosition().y, SCALE, this.player.getOffset());

        this.camera.showCam(g, player, player2);
        // ---------------
    }
}
