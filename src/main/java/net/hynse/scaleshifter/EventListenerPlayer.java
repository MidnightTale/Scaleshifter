package net.hynse.scaleshifter;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
}
