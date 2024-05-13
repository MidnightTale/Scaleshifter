package net.hynse.scaleshifter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.Bukkit.getLogger;

public class GUI implements Listener {
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
                Scaleshifter.scaleUtil.setPlayerScale(player, scale); // Apply transition when changing scale through GUI
                Scaleshifter.instance.playerInteractions.put(player.getUniqueId(), true);
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

    public void openGUI(Player player) {
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
}
