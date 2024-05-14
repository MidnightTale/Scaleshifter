package net.hynse.scaleshifter;

import me.nahu.scheduler.wrapper.FoliaWrappedJavaPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public final class Scaleshifter extends FoliaWrappedJavaPlugin implements Listener, CommandExecutor {
    public static Scaleshifter instance;
    public static DataManagers dataManagers;
    public static GUI gui;
    public static ScaleUtil scaleUtil;

    public final HashMap<UUID, Boolean> playerInteractions = new HashMap<>();


    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("Scaleshifter enabled");
        register();
        datainit();

    }

    @Override
    public void onDisable() {
        getLogger().info("Scaleshifter disabled");
        // Save player interactions data to file
        // saveInteractions();
    }
    public void register() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new GUI(), this);
        getCommand("scale").setExecutor(new ScaleCommands());
        getCommand("scaleplayer").setExecutor(new ScaleCommands());
        getCommand("scalegui").setExecutor(new GeneralCommands());
        getCommand("scaleguiplayer").setExecutor(new GeneralCommands());

    }
    public void datainit() {
        dataManagers.dataFile = new File(getDataFolder(), "player_interactions.json");
        if (!dataManagers.dataFile.exists()) {
            dataManagers.saveDefaultInteractions();
        } else {
            dataManagers.loadInteractions();
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getLogger().info("Player " + player.getName() + " joined the server.");
        if (!playerInteractions.containsKey(player.getUniqueId()) || !playerInteractions.get(player.getUniqueId())) {
            // Player was previously interacting with the GUI but didn't make a choice
            // Reopen the GUI for them
            playerInteractions.put(player.getUniqueId(), false);
            boolean interacted = playerInteractions.get(player.getUniqueId());
            getLogger().info("Player " + player.getName() + " previously interacted with the GUI: " + interacted);
            gui.openGUI(player);
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        //playerInteractions.remove(player.getUniqueId());
        //getLogger().info("Player " + player.getName() + " quit the server. Removed from playerInteractions map.");
    }
}
