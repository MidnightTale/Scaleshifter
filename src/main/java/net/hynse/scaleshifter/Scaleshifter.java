package net.hynse.scaleshifter;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import me.nahu.scheduler.wrapper.FoliaWrappedJavaPlugin;
import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public final class Scaleshifter extends FoliaWrappedJavaPlugin implements Listener, CommandExecutor {
    public static Scaleshifter instance;
    public DataManagers dataManagers;
    public static GUI gui;
    public static ScaleUtil scaleUtil;
    private PlayerHitbox hitbox;

    public final HashMap<UUID, Boolean> playerInteractions = new HashMap<>();


    @Override
    public void onEnable() {
        instance = this;
        dataManagers = new DataManagers();
        gui = new GUI();
        scaleUtil = new ScaleUtil();
        hitbox = new PlayerHitbox();

        getLogger().info("Scaleshifter enabled");
        register();
        dataManagers.datasetup();


        // Schedule a task to check player intersection with hitbox
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
                                        if (hitbox.isInsideCustomHitbox(player, target)) {
                                            // need to run in region but i dont know.
//                                    Location blockBelowLocation = player.getLocation().subtract(0, 1, 0); // Get location of the block below the player
//                                    BlockData blockData = blockBelowLocation.getBlock().getBlockData();
//                                    // Play particle at player's feet
//                                    player.getWorld().spawnParticle(Particle.BLOCK, player.getLocation(), 10, 0, 0, 0, 0.3, blockData);

                                            //player.getWorld().spawnParticle(Particle.CRIT, player.getLocation() ,7,0,0,0,0.1,Material.REDSTONE_BLOCK);
//                                    final Firework f = (Firework) player.getWorld().spawn(player.getLocation(), Firework.class);
//
//                                    FireworkMeta fm = f.getFireworkMeta();
//
//                                    fm.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(FireworkEffect.Type.BURST).withColor(Color.RED)
//                                            .withFade(Color.RED).build());
//
//                                    fm.setPower(0);
//
//                                    f.setFireworkMeta(fm);
//
//                                    f.detonate();
                                            FoliaScheduler.getEntityScheduler().runDelayed(target, instance, (p) -> {
                                                //player.playSound(player.getLocation(),Sound.BLOCK_CHERRY_LEAVES_BREAK ,0.1f,-0.2f);
                                                target.playEffect(target.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                                                target.damage(6, player);
                                            },() -> {
                                                } , 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this ,1, 8); // sub thread of main
    }

    @Override
    public void onDisable() {
//        getLogger().info("Scaleshifter disabled");
        // Save player interactions data to file
        dataManagers.saveInteractions();
    }
    public void register() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new GUI(), this);
        getCommand("scale").setExecutor(new ScaleCommands());
        getCommand("scaleplayer").setExecutor(new ScaleCommands());
        getCommand("scalegui").setExecutor(new GeneralCommands());
        getCommand("scaleguiplayer").setExecutor(new GeneralCommands());

    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getLogger().info("Player " + player.getName() + " joined the server.");
        if (!playerInteractions.containsKey(player.getUniqueId()) || !playerInteractions.get(player.getUniqueId())) {
            // Player was previously interacting with the GUI but didn't make a choice
            // Reopen the GUI for them
            playerInteractions.put(player.getUniqueId(), false);
            //boolean interacted = playerInteractions.get(player.getUniqueId());
            //getLogger().info("Player " + player.getName() + " previously interacted with the GUI: " + interacted);
            gui.openGUI(player);
        }

    }

//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onPlayerQuit(PlayerQuitEvent event) {
//        Player player = event.getPlayer();
//        //playerInteractions.remove(player.getUniqueId());
//        //getLogger().info("Player " + player.getName() + " quit the server. Removed from playerInteractions map.");
//    }
//    @EventHandler
//    public void onEntityDmange(EntityDamageEvent event) {
//        getLogger().info(event.getEntity() + " | Damage: " + event.getDamage());
//    }
}
