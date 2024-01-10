/**
 * @brief This file contains the public class Animation.
 * 
 * @file Animation.java
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * Part of the `graphics` package. It allows to load multiple frames in **PNG** format and to cycle through it.
 */


package graphics;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import geometry.Vector2D;


/**
 * @class Animation
 * @author Kevin Fedyna
 * @date 16/11/2023
 * 
 * @brief This class allows to perform animations based on multiple frames.
 * 
 * It can be called inside a `try catch` block or using the helper function @ref Animation.load but it won't raise an exception.
 * 
 * @warning The frames must be in **PNG** format and be named using `framesName<X>.png` with X starting at 1.
 */
public class Animation {
    /**
     * @brief Constant that points to the resources folder of the game.
     */
    public static final String RESOURCES_FOLDER = "../src/main/resources/";
    
    /** @brief An array containing the frames. */
    private BufferedImage[] frames;
    /** @brief Timer used for the loop. */
    private Timer frameTimer;
    /** @brief Timer used for the playOnce method. */
    private Timer frameOnceTimer;
    /** @brief The total number of frames. */
    private int frameCounter;
    /** @brief The index of the current frame. */
    private int frameIndex;
    /** @brief A boolean describing if the animation is playing. */
    private boolean isPlaying;

    /**
     * @brief Helper function to load without returning an error.
     * 
     * This function is usefull when you want to avoid bulky `try catch` blocks in your code.
     * 
     * @param framesName The frame base name. Frames should be named "framesNameX.png" with X starting at 1
     * @param baseURL The base URL to the folder with frames
     * @param frameRate The number of frames per seconds
     * @return The new Animation object
     * 
     * @warning Returns `null` when it can't load animation.
     * @see graphics.Animation.Animation
     */
    public static Animation load(String framesName, String baseURL, int frameRate) {
        try {
            return new Animation(framesName, baseURL, frameRate);
        } catch (IOException e) {
            // System.err.println("Couldn't load animation named \"" + framesName + "\" : " + e);
        }

        return null;
    }

    /**
     * @brief Constructor for Animation class.
     * 
     * Create animation object based on multiple frames.
     * 
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
        for (int i = 1; i <= this.frameCounter; i++) {
            this.frames[i - 1] = ImageIO.read(new File(baseURL + framesName + i + ".png"));
        }

        // Create timer for the animation
        this.frameIndex = 0;

        this.frameTimer = new Timer(Math.round(1000 / frameRate), event -> {
            frameIndex = (frameIndex + 1) % frameCounter;
        });

        this.frameOnceTimer = new Timer(Math.round(1000 / frameRate), event -> {
            frameIndex++;
            if (frameIndex == frameCounter) {
                frameIndex = 0;
                frameOnceTimer.stop();
                isPlaying = false;
            }
        });
    }

    /**
     * @brief Starts animation timer.
     * 
     * When this method is called, the animation will be looped endlessly.
     * 
     * @note This method doesn't reset the frame counter.
     */
    public void play() {
        this.frameTimer.start();
        this.isPlaying = true;
    }

    /**
     * @brief Start animation timer.
     * 
     * When this method is called, the frame counter is reset and the animation is played once.
     */
    public void playOnce() {
        this.frameIndex = 0;
        this.frameOnceTimer.start();
        this.isPlaying = true;
    }

    /**
     * @brief Stops animation timer.
     * 
     * When this method is called, the Animation object will stay in memory, but the timer will be paused.
     */
    public void stop() {
        this.frameTimer.stop();
        this.isPlaying = false;
    }

    /**
     * @brief Get the current frame to display.
     * 
     * The frame is a BufferedImage object, allowing it to be directly displayed with
     * a Graphics object for example.
     * 
     * @return The current frame
     * 
     * @see java.awt.image.BufferedImage
     * @see java.awt.Graphics
     */
    public BufferedImage getCurrentFrame() {
        return this.frames[this.frameIndex];
    }

    /**
     * @brief Gets the dimension of the current frame.
     * 
     * The dimensions are returned as a `Vector2D` object, with :
     * - The x being the width
     * - The y being the height
     * 
     * @return A Vector2D storing the width and height of the frame.
     * 
     * @see geometry.Vector2D
     */
    public Vector2D getSize() {
        return new Vector2D(this.frames[this.frameIndex].getWidth(), this.frames[this.frameIndex].getHeight());
    }

    /**
     * @brief Check if animation is playing.
     * 
     * This will check if either the `play` method or the `playOnce`
     * method has been called.
     * 
     * @return `true` if the animation is playing, `false` otherwise.
     */
    public boolean isPlaying() {
        return this.isPlaying;
    }
}
