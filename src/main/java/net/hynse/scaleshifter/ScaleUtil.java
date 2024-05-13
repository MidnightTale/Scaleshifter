package net.hynse.scaleshifter;

import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class ScaleUtil {
    public void setPlayerScale(Player player, double scale) {
        double currentScale = player.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();
        double difference = scale - currentScale;
        int steps = 30; // Number of steps for transition
        long delayBetweenSteps = 1; // Delay between each step in milliseconds

        WrappedRunnable task = new WrappedRunnable() {
            int stepCount = 0;

            @Override
            public void run() {
                if (stepCount >= steps) {
                    player.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(scale); // Set final scale
                    cancel(); // Stop  task
                    return;
                }
                double newScale = currentScale + difference * ((double) stepCount / steps);
                player.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(newScale);
                stepCount++;
            }
        };

        task.runTaskTimerAtLocation(Scaleshifter.instance,player.getLocation(), 1, delayBetweenSteps);
    }
}
