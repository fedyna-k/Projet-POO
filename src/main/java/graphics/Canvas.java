package graphics;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;


public class Canvas extends JPanel {
    private boolean isFullscreen;
    private Timer timer;


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
    }
}
