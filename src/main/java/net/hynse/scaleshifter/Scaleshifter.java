package net.hynse.scaleshifter;

import me.nahu.scheduler.wrapper.FoliaWrappedJavaPlugin;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public final class Scaleshifter extends FoliaWrappedJavaPlugin implements Listener, CommandExecutor {
    public static Scaleshifter instance;
    public DataManagers dataManagers;
    public static GUI gui;
    public static ScaleUtil scaleUtil;
    public PlayerHitbox hitbox;
    public static ScaleOrb scaleOrb;
    public static MassiveDamageTiny massiveDamageTiny;
    public static EventListenerPlayer eventListener;

    public final HashMap<UUID, Boolean> playerInteractions = new HashMap<>();


    @Override
    public void onEnable() {
        instance = this;
        dataManagers = new DataManagers();
        eventListener = new EventListenerPlayer();
        gui = new GUI();
        scaleUtil = new ScaleUtil();
        hitbox = new PlayerHitbox();
        scaleOrb = new ScaleOrb();
        massiveDamageTiny = new MassiveDamageTiny();

        getLogger().info("Scaleshifter enabled");
        register();
        dataManagers.datasetup();

        scaleOrb.item();

        massiveDamageTiny.ScheduleTask_A();

    }

    @Override
    public void onDisable() {
        dataManagers.saveInteractions();
    }

    public void register() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new GUI(), this);
        getServer().getPluginManager().registerEvents(new ScaleOrb(), this);
        getServer().getPluginManager().registerEvents(new EventListenerPlayer(), this);

        getCommand("scale").setExecutor(new ScaleCommands());
        getCommand("scaleplayer").setExecutor(new ScaleCommands());
        getCommand("scalegui").setExecutor(new GeneralCommands());
        getCommand("scaleguiplayer").setExecutor(new GeneralCommands());

    }

}