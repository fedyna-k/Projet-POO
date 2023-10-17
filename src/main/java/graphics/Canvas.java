package graphics;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.Timer;

import graphics.Animation;


public class Canvas extends JPanel {
    private boolean isFullscreen;
    private Timer timer;

    private Animation perso;

    public Canvas() {
        this(false);
    }

    public Canvas(boolean isFullscreen) {
        super(true);
        this.isFullscreen = isFullscreen;

        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                repaint();
            }
        });
        timer.start();

        try {
            perso = new Animation("standing", Animation.RESOURCES_FOLDER + "player/", 10);
        } catch (IOException e) {
            System.out.println("Couldn't create character");
        }

        perso.play();
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
        
        for (int i = 0 ; i < 5 ; i++) {
            for (int j = 0 ; j < 5 ; j++) {
                g.drawImage(perso.getCurrentFrame(), i * 120, j * 120, 128, 128, this);
            }
        }
    }
}
