package graphics;


import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Timer;


public class Animation {
    public static final String RESOURCES_FOLDER = "../src/main/resources/";
    private BufferedImage[] frames;
    private Timer frameTimer;
    private int frameCounter;
    private int frameIndex;

    /**
     * Create animation object with multiple frames
     * @param framesName The frame base name. Frames should be named "framesNameX.png" with X starting at 1
     * @param baseURL The base URL to the folder with frames
     * @param frameRate The number of frames per seconds
     * @throws IOException In case frames couldn't be found
     */
    public Animation(String framesName, String baseURL, int frameRate) throws IOException {
        // Count all frames with given name
        File[] all_files = new File(baseURL).listFiles();
        this.frameCounter = 0;

        for (File file : all_files) {
            if (file.getName().startsWith(framesName)) {
                this.frameCounter++;
            }
        }

        // Throw error if we couldn't find any frame
        if (this.frameCounter == 0) {
            throw new IOException("Couldn't find frames in given path : " + baseURL);
        }

        // Read all frames
        this.frames = new BufferedImage[this.frameCounter];
        for (int i = 1 ; i <= this.frameCounter ; i++) {
            this.frames[i - 1] = ImageIO.read(new File(baseURL + framesName + i + ".png"));
        }

        // Create timer for the animation
        this.frameIndex = 0;
        this.frameTimer = new Timer(Math.round(1000 / frameRate), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                frameIndex = (frameIndex + 1) % frameCounter;
            }
        });
    }

    /**
     * Start animation
     */
    public void play() {
        this.frameTimer.start();
    }

    /**
     * Stop animation
     */
    public void stop() {
        this.frameTimer.stop();
    }

    /**
     * Get the current frame to display
     * @return The current frame
     */
    public BufferedImage getCurrentFrame() {
        return this.frames[this.frameIndex];
    }
}
