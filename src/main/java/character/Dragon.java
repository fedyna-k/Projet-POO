package character;

import geometry.Vector2D;

public class Dragon extends Monster {
    public int zone;

    /**
     * @brief Constructor for Monster with specified coordinates.
     *
     *        Initializes a Monster at the specified coordinates with default
     *        animations
     *        and facing direction.
     *
     * @param x      The x-coordinate of the Monster.
     * @param y      The y-coordinate of the Monster.
     */
    public Dragon(double x, double y, Player player, int zone) {
        super(x, y, player, zone);
        this.setAnimations("dragon/");
        this.coordinates = new Vector2D(x, y);
        this.isFacingLeft = false;
        this.zone = zone;

        int attack = Math.min((int)Math.floor(Math.random() * (player.level + 5)) + (zone - 1) * 4 + 3, zone * 15);
        int speed = Math.min((int)Math.floor(Math.random() * (player.level + 5)) + (zone - 1) * 4 + 3, zone * 15);
        int defence = Math.min((int)Math.floor(Math.random() * (player.level + 5)) + (zone - 1) * 4 + 3, zone * 15);

        this.stats = new EntityStats((zone + 1) * 250, 100, speed, attack, 1, defence);
        this.xp = 20 * defence + 30 * attack + 15 * speed;
    }
}
