package graphics;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Timer;

import geometry.Vector2D;


public class Canvas extends JPanel implements KeyListener {
    private boolean isFullscreen;
    private Timer timer;

    private Animation current;
    private Animation standing;
    private Animation leftRun;
    private Animation rightRun;
    private Vector2D position;
    private Vector2D movement;
    private Set<Integer> allPressedKeys;

    public Canvas() {
        this(false);
    }

    public Canvas(boolean isFullscreen) {
        super(true);
        this.isFullscreen = isFullscreen;
        this.allPressedKeys = new HashSet<Integer>();
        this.position = new Vector2D(0, 100);
        this.movement = new Vector2D(2, 1);

        setFocusable(true);
        addKeyListener(this);

        (new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.out.println("Position : " + position.x + ", " + position.y);
                System.out.println(allPressedKeys);
            }
        })).start();

        standing = Animation.load("standing", Animation.RESOURCES_FOLDER + "player/", 10);
        leftRun = Animation.load("leftrun", Animation.RESOURCES_FOLDER + "player/", 10);
        rightRun = Animation.load("rightrun", Animation.RESOURCES_FOLDER + "player/", 10);
        
        current = standing;
        current.play();

        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if (position.x < 0 || position.x > 472) {movement.x *= -1;}
                if (position.y < 0 || position.y > 472) {movement.y *= -1;}


                if (allPressedKeys.contains(KeyEvent.VK_LEFT)) {
                    movement.x -= 1;
                }
                if (allPressedKeys.contains(KeyEvent.VK_RIGHT)) {
                    movement.x += 1;
                }
                if (allPressedKeys.contains(KeyEvent.VK_UP)) {
                    movement.y -= 1;
                }
                if (allPressedKeys.contains(KeyEvent.VK_DOWN)) {
                    movement.y += 1;
                }
                position = Vector2D.add(position, movement);

                if ((movement.x > 0 || movement.x == 0 && movement.y != 0) && current != rightRun) {
                    current.stop();
                    current = rightRun;
                    current.play();
                } else if ((movement.x < 0) && current != leftRun) {
                    current.stop();
                    current = leftRun;
                    current.play();
                } else if (movement.x == 0 && movement.y == 0 && current != standing) {
                    current.stop();
                    current = standing;
                    current.play();
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
        
        g.drawImage(current.getCurrentFrame(), (int)position.x, (int)position.y, 128, 128, this);
    }
    
    public void keyPressed(KeyEvent e) {
        System.out.println("Pressed : " + e.getKeyCode());
        allPressedKeys.add(e.getKeyCode());
    }

    public void keyReleased(KeyEvent e) {
        System.out.println("Released : " + e.getKeyCode());
        allPressedKeys.remove(e.getKeyCode());
    }

    public void keyTyped(KeyEvent e) {}
}
