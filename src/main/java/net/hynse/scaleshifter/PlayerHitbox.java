package net.hynse.scaleshifter;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerHitbox {

    public boolean isInsideCustomHitbox(Player player, Player target) {
        Location playerLoc = player.getLocation();
        double playerX = playerLoc.getX();
        double playerY = playerLoc.getY();
        double playerZ = playerLoc.getZ();

        Location targetLoc = target.getLocation();
        double targetX = targetLoc.getX();
        double targetY = targetLoc.getY();
        double targetZ = targetLoc.getZ();

        // Calculate player's hitbox boundaries
        double minX = playerX - 0.75;
        double maxX = playerX + 0.75;
        double minY = playerY;
        double maxY = playerY + 0.16;
        double minZ = playerZ - 0.75;
        double maxZ = playerZ + 0.75;

        // Check if target player is within the hitbox of the player
        return (targetX >= minX && targetX <= maxX &&
                targetY >= minY && targetY <= maxY &&
                targetZ >= minZ && targetZ <= maxZ);
    }
}