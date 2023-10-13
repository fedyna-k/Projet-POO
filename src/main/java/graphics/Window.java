package graphics;

import javax.swing.JFrame;

public class Window extends JFrame {
    
    public Canvas canvas;

    public Window() {
        this(false);
    }

    public Window(boolean isFullscreen) {
        // Basic needs
        setSize(600, 600);
        setTitle("Les chevaliers d'Ether");
        setResizable(false);
        setUndecorated(isFullscreen);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set fullscreen
        if (isFullscreen) {
            setExtendedState(MAXIMIZED_BOTH);
        }

        this.canvas = new Canvas(isFullscreen);

        add(canvas);
        pack();
    }
}
