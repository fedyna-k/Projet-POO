package graphics;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.Timer;

public class Canvas extends JPanel {
    private boolean isFullscreen;
    private Timer timer;
    private Timer changeFrame;

    private int frameIndex;
    private BufferedImage[] frames;

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

        changeFrame = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                frameIndex = (frameIndex + 1) % 5;
            }
        });
        changeFrame.start();

        this.frameIndex = 0;
        this.frames = new BufferedImage[5];
        for (int i = 1 ; i < 6 ; i++) {
            try {
                this.frames[i - 1] = ImageIO.read(new File("../src/main/resources/player/standing" + i + ".png"));
            } catch (IOException e) {}
        }
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
                g.drawImage(this.frames[(this.frameIndex + i + j) % 5], i * 120, j * 120, 128, 128, this);
            }
        }
    }
}
