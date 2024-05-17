package net.hynse.scaleshifter;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class ScheduleTask {
    public void MassiveDamageTiny () {
        new WrappedRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    if (player != null && player.getAttribute(Attribute.GENERIC_SCALE) != null) {
                        double playerScale = player.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
                        if (playerScale == 1.6) {
                            for (Player target : getServer().getOnlinePlayers()) {
                                if (target != null && target.getAttribute(Attribute.GENERIC_SCALE) != null) {
                                    double targetScale = target.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
                                    if (targetScale == 0.42 && target.getGameMode() != GameMode.SPECTATOR && target.getGameMode() != GameMode.CREATIVE) {
                                        if (Scaleshifter.instance.hitbox.isInsideCustomHitbox(player, target)) {
                                            FoliaScheduler.getEntityScheduler().runDelayed(target, Scaleshifter.instance, (p) -> {
                                                target.playEffect(target.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                                                target.damage(6, player);
                                            }, () -> {
                                            }, 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Scaleshifter.instance, 1, 8);
    }

//    public void startCheckHoldRightClick() {
//        new WrappedRunnable() {
//            @Override
//            public void run() {
//                long currentTime = System.currentTimeMillis();
//
//                for (UUID playerId : Scaleshifter.instance.lastInteractTime.keySet()) {
//                    long lastTime = Scaleshifter.instance.lastInteractTime.get(playerId);
//
//                    if (currentTime - lastTime > 250) {
//                        Scaleshifter.instance.isHoldingRightClick.put(playerId, false);
//                    }
//                }
//                for (Map.Entry<UUID, Boolean> entry : Scaleshifter.instance.isHoldingRightClick.entrySet()) {
//                    Player player = Bukkit.getPlayer(entry.getKey());
//                    if (player != null) {
//                        if (entry.getValue()) {
//                            return;
//                        } else {
//                            Scaleshifter.instance.isHoldingRightClick.put(entry.getKey(), false);
//                        }
//                    }
//                }
//            }
//        }.runTaskTimer(Scaleshifter.instance, 1, 1);
//    }

}
