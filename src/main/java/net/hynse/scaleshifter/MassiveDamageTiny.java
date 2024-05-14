package net.hynse.scaleshifter;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getServer;

public class MassiveDamageTiny {
    public void ScheduleTask_A () {
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
                                    if (targetScale == 0.42 && target.getGameMode() == GameMode.SURVIVAL) {
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
}
