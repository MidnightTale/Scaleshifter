package net.hynse.scaleshifter;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventListenerPlayer implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        NamespacedKey recipeKey = new NamespacedKey(Scaleshifter.instance, "scale_orb");
        player.discoverRecipe(recipeKey);
        if (!Scaleshifter.instance.playerInteractions.containsKey(player.getUniqueId()) || !Scaleshifter.instance.playerInteractions.get(player.getUniqueId())) {
            Scaleshifter.instance.playerInteractions.put(player.getUniqueId(), false);
            Scaleshifter.gui.openGUI(player);
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Material blockType = player.getLocation().getBlock().getType();
        double scale = player.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
        if (blockType == Material.COBWEB && scale <= 0.8) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 6, 4, false, false));
//            double newSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() + 0.13;
//            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(newSpeed);
        }
        if (blockType == Material.COBWEB && scale <= 0.42) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3, 10, false, false));
//            double newSpeed = player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() + 0.16;
//            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(newSpeed);
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity targetEntity = event.getRightClicked();

        if (player.isSneaking() && player.getInventory().getItemInMainHand().getType() == Material.AIR && player.getPassengers().isEmpty()) {
            if (targetEntity instanceof LivingEntity && event.getRightClicked() != null) {
                LivingEntity target = (LivingEntity) targetEntity;
                LivingEntity host = (LivingEntity) event.getPlayer();

                double targetScale = target.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
                double hostScale = host.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();

                if (targetScale <= 1.0 && hostScale == 1.6) {
                    if (player.getGameMode() != GameMode.SPECTATOR && target.getType().isAlive()) {
                        player.addPassenger(target);
                    }
                }
            }
        }
//        FoliaScheduler.getGlobalRegionScheduler().runDelayed(Scaleshifter.instance, (n) -> {
            if (player.isSneaking() && player.getInventory().getItemInMainHand().getType() == Material.BREEZE_ROD) {
                if (targetEntity instanceof LivingEntity && event.getRightClicked() != null) {
                    charge(player);
                }
            }
//        },10);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
            Player player = event.getPlayer();
            if ((player.isSneaking() && player.getInventory().getItemInMainHand().getType() == Material.BREEZE_ROD)) {
                charge(player);
            }
    }

    public void charge(Player player) {
        if (!player.getPassengers().isEmpty()) {
            UUID playerId = player.getUniqueId();
            if (!Scaleshifter.instance.chargingPlayers.containsKey(playerId)) {
                Scaleshifter.instance.chargingPlayers.put(playerId, 0);

                new WrappedRunnable() {
                    @Override
                    public void run() {
                        if (!Scaleshifter.instance.chargingPlayers.containsKey(playerId)) {
                            cancel();
                            return;
                        }

                        int power = Scaleshifter.instance.chargingPlayers.get(playerId);
                        power = Math.min(power + 1, 10);
                        Scaleshifter.instance.chargingPlayers.put(playerId, power);

                        // Build the power bar string with color and placeholder
                        StringBuilder powerBar = new StringBuilder("Power [");
                        for (int i = 0; i < 10; i++) {
                            if (i < power) {
                                if (i < 5) {
                                    // Green color for low power
                                    powerBar.append(ChatColor.GREEN);
                                } else if (i < 8) {
                                    // Yellow color for medium power
                                    powerBar.append(ChatColor.YELLOW);
                                } else {
                                    // Red color for high power
                                    powerBar.append(ChatColor.RED);
                                }
                                powerBar.append("█");
                            } else {
                                // Gray color for placeholder
                                powerBar.append(ChatColor.GRAY).append("▒");
                            }
                        }

                        // Send the power bar to the player's action bar
                        player.sendActionBar(powerBar.toString() + ChatColor.WHITE + "]");

                        if ((!player.isSneaking() || player.getInventory().getItemInMainHand().getType() != Material.BREEZE_ROD)) {
                            throwPassenger(player, power);
                            Scaleshifter.instance.chargingPlayers.remove(playerId);
                            cancel();
                        }
                    }
                }.runTaskTimer(Scaleshifter.instance, 1, 3);
            }
        }
    }

    public void throwPassenger(Player player, int power) {
        double scaledPower = power / 10.0 * 1.6;
        double scaledPowerUpward = power / 10.0 * 0.53;
        double scaledPowerFeedback = power / 10.0 * -0.6;

        new WrappedRunnable() {
            @Override
            public void run() {
                for (Entity passenger : player.getPassengers()) {
                    player.removePassenger(passenger);
                    FoliaScheduler.getEntityScheduler().runDelayed(player, Scaleshifter.instance, (y) -> {
                        Vector velocity = player.getLocation().getDirection().multiply(scaledPower);
                        velocity.setY(velocity.getY() + scaledPowerUpward);
                        passenger.setVelocity(velocity);
                        player.setVelocity(player.getLocation().getDirection().multiply(scaledPowerFeedback));
                        //player.sendActionBar("P: " + power);
                    }, null, 3);
                }
            }
        }.runTaskAtEntity(Scaleshifter.instance, player);
    }
}
