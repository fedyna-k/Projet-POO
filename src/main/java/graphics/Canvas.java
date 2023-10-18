package graphics;


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
    // ---------------

    public Canvas() {
        this(false);
    }

    public Canvas(boolean isFullscreen) {
        super(true);
        this.isFullscreen = isFullscreen;

        // TESTING PURPOSE
        this.player = new Player();
        this.stack = new KeyStack(this);
        stack.listenTo("Z");
        stack.listenTo("S");
        stack.listenTo("Q");
        stack.listenTo("D");
        // ---------------

        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // TESTING PURPOSE
                Vector2D movement = new Vector2D();
                if (stack.isPressed("Z")) {
                    movement.y -= 2;
                }
                if (stack.isPressed("S")) {
                    movement.y += 2;
                }
                if (stack.isPressed("Q")) {
                    movement.x -= 2;
                }
                if (stack.isPressed("D")) {
                    movement.x += 2;
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
        g.drawImage(this.player.getSprite(), (int)this.player.getPosition().x, (int)this.player.getPosition().y, 128, 128, this);
        // ---------------
    }
}
