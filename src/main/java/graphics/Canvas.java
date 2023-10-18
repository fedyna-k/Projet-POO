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
    private boolean wasReleasedSpace;

    private Player player2;
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
        this.player2 = new Player(400, 200);
        this.stack = new KeyStack(this);
        this.wasReleasedSpace = true;
        stack.listenTo("Z");
        stack.listenTo("S");
        stack.listenTo("Q");
        stack.listenTo("D");
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
                if (stack.isPressed("SPACE")) {
                    if (wasReleasedSpace) {
                        player.attack();
                        wasReleasedSpace = false;
                    }
                } else {
                    wasReleasedSpace = true;
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
        
        // To fix with character implementation
        // g.drawImage(current.getCurrentFrame(), (int)position.x, (int)position.y, 128, 128, this);

        // TESTING PURPOSE        
        int[] dimensions2 = this.player2.getSpriteSize();
        g.drawImage(this.player2.getSprite(), (int)this.player2.getPosition().x, (int)this.player2.getPosition().y, dimensions2[0] * 2, dimensions2[1] * 2, this);

        int[] dimensions = this.player.getSpriteSize();
        g.drawImage(this.player.getSprite(), (int)this.player.getPosition().x, (int)this.player.getPosition().y, dimensions[0] * 2, dimensions[1] * 2, this);
        // ---------------
    }
}
