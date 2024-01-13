/**
 * @brief This file contains the public class Canvas.
 *
 * @file Canvas.java
 * @author Kevin Fedyna
 * @author Imene Bousmaha
 * @date 10/01/2024
 *
 * Part of the `graphics` package. It contains a class that allow to draw on screen.
 */

package graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import character.Player;
import character.Dragon;
import character.Enemies;
import character.Entity;
import character.Monster;
import geometry.Range;
import geometry.Vector2D;
import map.Map;

/**
 * @class Canvas
 * @author Kevin Fedyna
 * @author Imene Bousmaha
 * @date 10/01/2024
 *
 * @brief This class allows to draw on screen.
 *
 *        It should only be instancied once per Window.
 *
 *        The instanciation takes place inside the Window class.
 *
 * @see graphics.Window
 */
public class Canvas extends JPanel {
    /** @brief Tells if the window is in fullscreen. */
    public boolean isFullscreen;
    /** @brief Timer that does not depend on EDT, handles all computations. */
    private TrueTimer mainTimer;
    /** @brief The camera that follows the player. */
    private Camera camera;

    /** @brief The map object */
    private Map map;
    /** @brief The player */
    private Player player;
    private int lastHit = 0;
    private int accel = 0;
    /** @brief Monster pool */
    private ArrayList<Monster> badguys;
    /** @brief All entities */
    private ArrayList<Entity> allEntities;
    private KeyStack stack;
    private boolean wasReleasedO;
    private boolean wasReleasedSpace;
    private boolean wasReleasedI;
    private boolean wasReleasedEsc;
    private boolean wasReleasedK;
    private boolean wasReleasedL;
    private boolean wasReleasedM;
    private boolean wasReleasedH;
    private boolean wasReleasedEnter;

    private boolean inDialog = false;
    private int dialogIndex = 0;
    private boolean hasStarted = false;
    private boolean isPaused = false;
    private boolean showHelp = true;
    private int[] hasSpawned = new int[Enemies.enemies.length];
    private int[] isNotThere = new int[Enemies.enemies.length];

    private int[] hasSpawnedDragon = new int[Enemies.dragons.length];
    private int[] isNotThereDragon = new int[Enemies.dragons.length];

    boolean entitiesCollision = false;

    static final double PROBABILITY_OF_ATTACK = 0.8;
    static final double AGGRO_RANGE = 500.0;
    Random random = new Random();

    // ---------------

    /**
     * @brief The default constructor.
     *
     *        Calls the main constructor with fullscreen set to false.
     */
    public Canvas() {
        this(false);
    }

    /**
     * @brief The main constructor.
     *
     *        First starts by constructing a JFrame with double buffer.
     *
     *        The timer is set here, so if you want to add things to the main loop
     *        you should edit this.
     *
     * @param isFullscreen Is the screen in fullscreen mode ?
     * @see javax.swing.JPanel
     */
    public Canvas(boolean isFullscreen) {
        super(true);
        this.isFullscreen = isFullscreen;
        this.camera = Camera.getCamera(this);
        setBackground(new Color(42, 42, 42, 255));

        this.player = new Player(300, 1250);
        this.allEntities = new ArrayList<>(); 
        this.badguys = new ArrayList<>();

        for (int i = 0 ; i < hasSpawned.length ; i++) {
            hasSpawned[i] = 0;
            isNotThere[i] = 0;
        }

        this.allEntities.add(player);

        this.map = new Map("resources/map/");
        this.stack = new KeyStack(this);
        this.wasReleasedO = true;
        this.wasReleasedSpace = true;
        this.wasReleasedI = true;
        this.wasReleasedK = true;
        this.wasReleasedL = true;
        this.wasReleasedM = true;

        stack.listenTo("Z");
        stack.listenTo("S");
        stack.listenTo("Q");
        stack.listenTo("D");
        stack.listenTo("SPACE");
        stack.listenTo("ENTER");
        stack.listenTo("O");
        stack.listenTo("I");
        stack.listenTo("ESCAPE");
        stack.listenTo("K");
        stack.listenTo("L");
        stack.listenTo("M");
        stack.listenTo("H");

        this.camera.setFocusOn(player);
        // ---------------

        Function<Void, Void> loop = e -> {
            if (!hasStarted || player.isDead()) {
                if (stack.isPressed("ENTER")) {

                    for (Entity ent : allEntities) {
                        ent.current.stop();
                    }

                    this.player = new Player(1300, 7300);
                    
                    this.allEntities = new ArrayList<>(); 
                    this.allEntities.add(player);
                    this.badguys = new ArrayList<>();
                    this.camera.setFocusOn(player);

                    this.hasStarted = true;
                    this.inDialog = true;
                    this.dialogIndex = 0;
                    this.wasReleasedEnter = false;
                }


                repaint();
                return null;
            }

            if (stack.isPressed("ESCAPE")) {
                if (wasReleasedEsc) {
                    if (isPaused) {
                        isPaused = false;
                        for (Entity ent : this.allEntities) {
                            ent.current.resume();
                        }
                    } else {
                        isPaused = true;
                        for (Entity ent : this.allEntities) {
                            ent.current.stop();
                        }
                    }
                    wasReleasedEsc = false;

                    repaint();
                    return null;
                }
            } else {
                wasReleasedEsc = true;
            }

            if (isPaused) {
                repaint();
                return null;
            }


            for (int i = 0 ; i < Dialogs.triggers.length ; i++) {
                if (dialogIndex < i + 1
                    && Range.isIn(Dialogs.triggers[i][0], Dialogs.triggers[i][1], (int)player.coordinates.x)
                    && Range.isIn(Dialogs.triggers[i][2], Dialogs.triggers[i][3], (int)player.coordinates.y)) {
                    
                    dialogIndex = i + 1;
                    inDialog = true;
                }
            }


            for (int i = 0 ; i < Enemies.enemies.length ; i++) {
                int x = Enemies.enemies[i][0] - (int)player.coordinates.x;
                int y = Enemies.enemies[i][1] - (int)player.coordinates.y;

                hasSpawned[i] = Math.max(0, hasSpawned[i] - 1);
                
                if (x * x < getWidth() * getWidth() / 4 && y * y < getHeight() * getHeight() / 4) {
                    if (hasSpawned[i] == 0 && isNotThere[i] == 0) {
                        Monster newMonster = new Monster(Enemies.enemies[i][0], Enemies.enemies[i][1], player, Enemies.enemies[i][2]);
                        allEntities.add(newMonster);
                        badguys.add(newMonster);
                        hasSpawned[i] = 5000;
                        isNotThere[i] = 1;
                    }
                } else {
                    isNotThere[i] = 0;
                }
            }

            for (int i = 0 ; i < Enemies.dragons.length ; i++) {
                int x = Enemies.dragons[i][0] - (int)player.coordinates.x;
                int y = Enemies.dragons[i][1] - (int)player.coordinates.y;

                hasSpawnedDragon[i] = Math.max(0, hasSpawnedDragon[i] - 1);
                
                if (x * x < getWidth() * getWidth() / 4 && y * y < getHeight() * getHeight() / 4) {
                    if (hasSpawnedDragon[i] == 0 && isNotThereDragon[i] == 0) {
                        Dragon newMonster = new Dragon(Enemies.dragons[i][0], Enemies.dragons[i][1], player, Enemies.dragons[i][2]);
                        allEntities.add(newMonster);
                        badguys.add(newMonster);
                        hasSpawnedDragon[i] = 5000;
                        isNotThereDragon[i] = 1;
                    }
                } else {
                    isNotThereDragon[i] = 0;
                }
            }

            // TESTING PURPOSE
            Vector2D movement = new Vector2D();
            if (stack.isPressed("C")) {
                movement.y -= 1;
            }
            if (stack.isPressed("Z")) {
                movement.y -= 1;
            }
            if (stack.isPressed("S")) {
                movement.y += 1;
            }
            if (stack.isPressed("Q")) {
                movement.x -= 1;
            }
            if (stack.isPressed("D")) {
                movement.x += 1;
            }
            if (stack.isPressed("O")) {
                if (wasReleasedO && !player.isDodging() && !player.isBlocking()) {
                    player.attack();
                    wasReleasedO = false;
                }
            } else {
                wasReleasedO = true;
            }
            if (stack.isPressed("SPACE")) {
                if (wasReleasedSpace) {
                    player.dodge();
                    wasReleasedSpace = false;
                }
            } else {
                wasReleasedSpace = true;
            }

            if (stack.isPressed("H")) {
                if (wasReleasedH) {
                    showHelp = !showHelp;
                    wasReleasedH = false;
                }
            } else {
                wasReleasedH = true;
            }

            if (stack.isPressed("ENTER")) {
                if (wasReleasedEnter && inDialog) {
                    inDialog = false;
                    wasReleasedEnter = false;
                }
            } else {
                wasReleasedEnter = true;
            }

            if (stack.isPressed("K")) {
                if (wasReleasedK && player.skillPoints > 0) {
                    player.getStats().upgradeAttack();
                    player.skillPoints--;
                    wasReleasedK = false;
                }
            } else {
                wasReleasedK = true;
            }

            if (stack.isPressed("L")) {
                if (wasReleasedL && player.skillPoints > 0) {
                    player.getStats().upgradeDefence();
                    player.skillPoints--;
                    wasReleasedL = false;
                }
            } else {
                wasReleasedL = true;
            }

            if (stack.isPressed("M")) {
                if (wasReleasedM && player.skillPoints > 0) {
                    player.getStats().upgradeSpeed();
                    player.skillPoints--;
                    wasReleasedM = false;
                }
            } else {
                wasReleasedM = true;
            }

            if (stack.isPressed("I") && wasReleasedI) {
                player.block();
                wasReleasedI = false;
            }

            if (!stack.isPressed("I") && !wasReleasedI) {
                player.stopBlocking();
                wasReleasedI = true;
            }

            // ---------------

            // movement

            // The minimum distance required between player and monster
            double minDistance = 70.0;

            // Cooldown time for monster attacks
            double cooldown = 60.0;

            ArrayList<Monster> deadguys = new ArrayList<>();

            player.move(movement, player.getStats().getSpeed() / 10 + 0.5, allEntities);
            lastHit++;

            for (Monster badguy : badguys) {
                Vector2D difference = Vector2D.subtract(player.getPosition(), badguy.getPosition());

                if (difference.norm() > getWidth() * 2) {
                    deadguys.add(badguy);
                    continue;
                }

                if (difference.norm() < AGGRO_RANGE) {
                    badguy.isActive = true;
                    if (!badguy.isDodging() && !badguy.isBlocking() && !badguy.isAttacking() && !badguy.current.isPlaying()) {
                        badguy.current.resume();
                    }

                    if (difference.norm() > minDistance) {
                        // Normalize the vector to set the direction
                        difference.normalize();
                        badguy.move(difference, badguy.getStats().getSpeed() / 10 + 0.5, allEntities);
                    } else if (difference.norm() <= minDistance) {
                        // Stop monster movement and attempt an attack
                        badguy.stopMoving();
                        Monster.tryAttack(badguy, player, difference, PROBABILITY_OF_ATTACK, cooldown);

                        // Handle monster attack
                        if (Collision.checkMonsterAttack(badguy, player, badguy.getPosition(), player.getPosition())) {
                            Collision.handleMonsterAttack(badguy, player, badguy.getPosition(), player.getPosition());

                            lastHit = 0;
                            accel = 0;
                        }
                    }
                } else {
                    // If outside aggro range, make the monster move randomly
                    if (difference.norm() > getWidth() / this.map.getTileSize() * 2) {
                        badguy.current.stop();
                        badguy.isActive = false;
                    } else {
                        badguy.current.resume();
                        badguy.isActive = true;
                    }

                    badguy.randMovement(allEntities);
                }

                // Handle player attack
                if (Collision.checkPlayerAttack(player, badguy, player.getPosition(), badguy.getPosition())) {
                    Collision.handlePlayerAttack(player, badguy, player.getPosition(), badguy.getPosition());
                }

                if (badguy.isDead()) {
                    deadguys.add(badguy);

                    player.xp += badguy.xp;
                    
                    while (player.xp / (player.level * 250 + 500) > 0) {
                        player.xp -= player.level * 250 + 500;
                        player.level++;
                        player.skillPoints++;
                    }
                    
                }
            }

            for (Monster deadguy : deadguys) {
                badguys.remove(deadguy);
                allEntities.remove(deadguy);
                
                deadguy.current.stop();
            }

            // Auto regen
            if (lastHit > 1000) {
                accel++;
                lastHit = 750 + accel * 10;
                player.getStats().heal(1);
            }

            repaint();
            return null;
        };

        mainTimer = new TrueTimer(4, loop);
        mainTimer.execute();
    }

    /**
     * @brief Return the size of the window for cpu/gpu handle.
     *
     *        The default size of the window is 800x600 (in pixels).
     *
     * @return The size depending on the mode.
     * @warning It will take the dimensions of the main screen, the game could be on
     *          portrait mode and get glitchy.
     */
    @Override
    public Dimension getPreferredSize() {
        if (isFullscreen) {
            return Toolkit.getDefaultToolkit().getScreenSize();
        } else {
            return new Dimension(800, 600);
        }
    }

    /**
     * @brief Where we draw everything.
     *
     *        You should use the Camera class to draw as it computes all the evil
     *        maths
     *        behind the conversion between absolute and canvas-relative positions.
     *
     * @param g The objects that stores informations that will be drawn.
     * @warning If too much is drawn, it can lag quite much.
     */
    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!hasStarted) {
            camera.drawTextFixed(g, 20, 100, "Les chevaliers", 48, Color.white);
            camera.drawTextFixed(g, 20, 150, "d'Ether", 48, Color.white);
            camera.drawTextFixed(g, 150, 500, "Press ENTER to begin...", 24, Color.white);
            
            try {
                g.drawImage(ImageIO.read(new File("resources/logo.png")), 300, 150, 350, 350, this);
            } catch (Exception e) {}

            return;
        }

        if (player.isDead()) {
            camera.drawTextFixed(g, (int)getCenter().x - (isFullscreen ? 240 : 200), (int)getCenter().y - 10, "YOU DIED", 48, new Color(181, 0, 6));
            camera.drawTextFixed(g, (int)getCenter().x - (isFullscreen ? 310 : 270), (int)getCenter().y + 30, "Press ENTER to restart.", 24, Color.white);
            return;
        }


        // TESTING PURPOSE

        int SCALE = 2;

        // Get focused coordinates
        int focusX = this.camera.getFocused() != null ? (int) this.camera.getFocused().getPosition().x : 0;
        int focusY = this.camera.getFocused() != null ? (int) this.camera.getFocused().getPosition().y : 0;

        // Get tile infos for screen
        int width = getPreferredSize().width / (this.map.getTileSize() * SCALE);
        int height = getPreferredSize().height / (this.map.getTileSize() * SCALE);

        // Draw map based on coordinates and clamped

        int lowerTileIndexX = focusX / (this.map.getTileSize() * SCALE) - width / 2 - 1;
        int lowerTileIndexY = focusY / (this.map.getTileSize() * SCALE) - height / 2 - 1;

        int upperTileIndexX = focusX / (this.map.getTileSize() * SCALE) + width / 2 + 3;
        int upperTileIndexY = focusY / (this.map.getTileSize() * SCALE) + height / 2 + 3;

        if (lowerTileIndexX < 0) {
            upperTileIndexX -= lowerTileIndexX;
            lowerTileIndexX = 0;
        }

        if (lowerTileIndexY < 0) {
            upperTileIndexY -= lowerTileIndexY;
            lowerTileIndexY = 0;
        }

        if (upperTileIndexX >= map.getWidth()) {
            lowerTileIndexX -= upperTileIndexX - map.getWidth();
            upperTileIndexX -= upperTileIndexX - map.getWidth();
        }

        if (upperTileIndexY >= map.getHeight()) {
            lowerTileIndexY -= upperTileIndexY - map.getHeight();
            upperTileIndexY -= upperTileIndexY - map.getHeight();
        }

        for (int i = lowerTileIndexX; i < upperTileIndexX; i++) {
            for (int j = lowerTileIndexY; j < upperTileIndexY; j++) {
                this.map.drawTile(this.camera, g, i, j, SCALE);
            }
        }

        for (Entity badguy : badguys) {
            this.camera.drawImageClamped(g, this.map, badguy.getSprite(), badguy.getPosition().x,
                    badguy.getPosition().y,
                    SCALE, badguy.getOffset());

            HUD.drawEntityHealth(g, camera, map, badguy, SCALE);
            HUD.drawEntityCooldown(g, camera, map, badguy, SCALE);
        }

        this.camera.drawImageClamped(g, this.map, this.player.getSprite(), this.player.getPosition().x, this.player.getPosition().y,
                SCALE, this.player.getOffset());

        // LEFT HUD

        HUD.drawEntityCooldown(g, camera, map, player, SCALE);
        HUD.drawPlayerHealth(g, camera, player);
        HUD.drawStat(g, camera, player.getStats().getAttack(), "Attack", 25, 65, 150, 50);
        HUD.drawStat(g, camera, player.getStats().getDefence(), "Defence", 25,85, 150, 70);
        HUD.drawStat(g, camera, player.getStats().getSpeed(), "Speed", 25, 105, 150, 90);

        // RIGHT HUD

        HUD.drawXP(g, camera, this, player);

        // Bottom HUD
        if (showHelp) {
            HUD.drawCommands(g, camera, this);
        }

        if (isPaused) {
            camera.drawTextFixed(g, (int)getCenter().x - 100, (int)getCenter().y, "Paused", 24, Color.white);
        }

        if (inDialog) {
            HUD.drawDialog(g, camera, this, Dialogs.get(dialogIndex));
        }

        // ---------------
    }

    /**
     * @brief Get Canvas center point.
     * @return A Vector2D containing the points coordinates.
     */
    public Vector2D getCenter() {
        return new Vector2D(this.getWidth() / 2, this.getHeight() / 2);
    }

}
