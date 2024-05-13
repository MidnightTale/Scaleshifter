package net.hynse.scaleshifter;

import me.nahu.scheduler.wrapper.FoliaWrappedJavaPlugin;
import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public final class Scaleshifter extends FoliaWrappedJavaPlugin implements Listener, CommandExecutor {
    public static Scaleshifter instance;

    private final HashMap<UUID, Boolean> playerInteractions = new HashMap<>();
    private File dataFile;

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
        getCommand("scale").setExecutor(this);
        getCommand("scaleplayer").setExecutor(this);

    }
    public void datainit() {
        dataFile = new File(getDataFolder(), "player_interactions.json");
        if (!dataFile.exists()) {
            saveDefaultInteractions();
        } else {
            loadInteractions();
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
            openGUI(player);
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        //playerInteractions.remove(player.getUniqueId());
        //getLogger().info("Player " + player.getName() + " quit the server. Removed from playerInteractions map.");
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getClickedInventory() != null && event.getClickedInventory().equals(player.getOpenInventory().getTopInventory())) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) return;
            if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
                String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                double scale = 1.0;
                if (itemName.equalsIgnoreCase("Small")) {
                    scale = 0.75;
                } else if (itemName.equalsIgnoreCase("Normal")) {
                    scale = 1.0;
                } else if (itemName.equalsIgnoreCase("Big")) {
                    scale = 1.35;
                }
                setPlayerScale(player, scale); // Apply transition when changing scale through GUI
                playerInteractions.put(player.getUniqueId(), true);
                getLogger().info("Player " + player.getName() + " clicked in inventory. Interaction status updated.");
                player.closeInventory();
            }
        }
//        if (event.getClickedInventory() != null && event.getClickedInventory().getHolder() instanceof Player) {
//            Player player = (Player) event.getClickedInventory().getHolder();
//            if (playerInteractions.containsKey(player.getUniqueId()) && !playerInteractions.get(player.getUniqueId())) {
//                // Player is interacting with the GUI
//                // Handle their choice based on the clicked item
//                playerInteractions.put(player.getUniqueId(), true);
//                getLogger().info("Player " + player.getName() + " clicked in inventory. Interaction status updated.");
//                // Handle the clicked item
//                // ...
//                event.setCancelled(true); // Prevent item from being taken or moved
//            }
//        }
    }

    private void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "Choose Your Scale");

        ItemStack smallScaleItem = new ItemStack(Material.IRON_INGOT);
        ItemMeta smallScaleMeta = smallScaleItem.getItemMeta();
        if (smallScaleMeta != null) {
            smallScaleMeta.setDisplayName(ChatColor.GREEN + "Small");
            smallScaleItem.setItemMeta(smallScaleMeta);
        }
        gui.setItem(2, smallScaleItem);

        ItemStack normalScaleItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta normalScaleMeta = normalScaleItem.getItemMeta();
        if (normalScaleMeta != null) {
            normalScaleMeta.setDisplayName(ChatColor.YELLOW + "Normal");
            normalScaleItem.setItemMeta(normalScaleMeta);
        }
        gui.setItem(4, normalScaleItem);

        ItemStack bigScaleItem = new ItemStack(Material.DIAMOND);
        ItemMeta bigScaleMeta = bigScaleItem.getItemMeta();
        if (bigScaleMeta != null) {
            bigScaleMeta.setDisplayName(ChatColor.AQUA + "Big");
            bigScaleItem.setItemMeta(bigScaleMeta);
        }
        gui.setItem(6, bigScaleItem);

        player.openInventory(gui);
        getLogger().info("Opening GUI for player " + player.getName());
    }


    private void saveInteractions() {
        JSONObject jsonObject = new JSONObject();
        for (UUID uuid : playerInteractions.keySet()) {
            jsonObject.put(uuid.toString(), playerInteractions.get(uuid));
        }
        try (FileWriter fileWriter = new FileWriter(dataFile)) {
            fileWriter.write(jsonObject.toJSONString());
            getLogger().info("Player interactions saved to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadInteractions() {
        JSONParser jsonParser = new JSONParser();
        try (FileReader fileReader = new FileReader(dataFile)) {
            Object obj = jsonParser.parse(fileReader);
            JSONObject jsonObject = (JSONObject) obj;
            for (Object key : jsonObject.keySet()) {
                UUID uuid = UUID.fromString((String) key);
                boolean interacted = (boolean) jsonObject.get(key);
                playerInteractions.put(uuid, interacted);
            }
            getLogger().info("Player interactions loaded from file.");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultInteractions() {
        saveInteractions();
    }
    private void setPlayerScale(Player player, double scale) {
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

        task.runTaskTimerAtLocation(this,player.getLocation(), 1, delayBetweenSteps);
    }



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("scaleplayer")) {
            if (!sender.hasPermission("scaleshifter.scaleplayer")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }

            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /scaleplayer <playername> <value>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }

            double scale;
            try {
                scale = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid value! Please enter a number.");
                return true;
            }

            setPlayerScale(target, scale); // Apply transition when changing scale through command
            sender.sendMessage(ChatColor.GREEN + "Player scale set successfully!");

            return true;
        } else if (command.getName().equalsIgnoreCase("scale")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                return true;
            }

            if (!sender.hasPermission("scaleshifter.scale")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /scale <value>");
                return true;
            }

            Player player = (Player) sender;

            double scale;
            try {
                scale = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid value! Please enter a number.");
                return true;
            }

            setPlayerScale(player, scale); // Apply transition when changing own scale through command
            sender.sendMessage(ChatColor.GREEN + "Your scale set successfully!");

            return true;
        }
        return false;
    }
}
