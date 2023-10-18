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
    private Timer frameOnceTimer;
    private int frameCounter;
    private int frameIndex;
    private boolean isPlaying;

    /**
     * Helper function to load without returning an error
     * @param framesName The frame base name. Frames should be named "framesNameX.png" with X starting at 1
     * @param baseURL The base URL to the folder with frames
     * @param frameRate The number of frames per seconds
     * @return The new Animation object
     */
    public static Animation load(String framesName, String baseURL, int frameRate) {
        try {
            return new Animation(framesName, baseURL, frameRate);
        } catch (IOException e) {
            System.err.println("Couldn't load animation : " + e);
        }

        return null;
    }

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
        this.isPlaying = false;

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
        this.frameOnceTimer = new Timer(Math.round(1000 / frameRate), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                frameIndex++;
                if (frameIndex == frameCounter) {
                    frameIndex = 0;
                    frameOnceTimer.stop();
                    isPlaying = false;
                }
            }
        });
    }

    /**
     * Start animation
     */
    public void play() {
        this.frameTimer.start();
        this.isPlaying = true;
    }

    /**
     * Start animation
     */
    public void playOnce() {
        this.frameIndex = 0;
        this.frameOnceTimer.start();
        this.isPlaying = true;
    }

    /**
     * Stop animation
     */
    public void stop() {
        this.frameTimer.stop();
        this.isPlaying = false;
    }

    /**
     * Get the current frame to display
     * @return The current frame
     */
    public BufferedImage getCurrentFrame() {
        return this.frames[this.frameIndex];
    }

    /**
     * Gets the dimension of the current frame
     * @return An array in the form of {width, height}
     */
    public int[] getSize() {
        return new int[]{this.frames[this.frameIndex].getWidth(), this.frames[this.frameIndex].getHeight()};
    }

    /**
     * Check if animation is playing
     * @return The current state of the animation
     */
    public boolean isPlaying() {
        return this.isPlaying;
    }
}
