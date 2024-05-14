package net.hynse.scaleshifter;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class GUI implements Listener {
    private static final Map<String, Double[]> SCALE_DATA = new LinkedHashMap<>();

    static {
        SCALE_DATA.put("Tiny", new Double[]{0.42, 0.135, 16.0, 4.7, 0.95, 1.36, 0.058, 0.35, -0.16, 2.8, 0.05, 0.97, 4.0, 4.0});
        SCALE_DATA.put("Small", new Double[]{0.8, 0.12, 18.0, 4.26, 0.97, 1.2, 0.076, 0.37, -0.1, 2.5, 0.055, 0.98, 4.2, 4.2});
        SCALE_DATA.put("Normal", new Double[]{1.0, 0.10000000149011612, 20.0, 4.0, 1.0, 1.0, 0.08, 0.41999998688697815, 0.0, 3.0, 0.06, 1.0, 4.5, 4.5});
        SCALE_DATA.put("Large", new Double[]{1.32, 0.096, 26.0, 3.75, 1.5, 0.95, 0.085, 0.56, 0.16, 4.5, 0.08, 1.3, 5.0, 5.0});
        SCALE_DATA.put("Massive", new Double[]{1.6, 0.09265, 32.0, 3.4, 2.0, 0.9, 0.087, 0.59, 0.37, 5.0, 0.12, 1.6, 5.5, 5.5});
    }

    private static final String GUI_TITLE = "Choose Your Scale";

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals(GUI_TITLE)) {

            double playerScale = player.getAttribute(Attribute.GENERIC_SCALE).getBaseValue();

            ItemStack item = event.getCurrentItem();
            String itemName = ChatColor.stripColor(Objects.requireNonNull(item.getItemMeta()).getDisplayName());
            Double[] data = SCALE_DATA.get(itemName);

            if (playerScale == data[0]) {

                player.sendMessage(ChatColor.RED + "You are already " + itemName);

            } else if (playerScale != data[0]) {

                setPlayerStatus(player, data);
                Scaleshifter.instance.playerInteractions.put(player.getUniqueId(), true);
                player.closeInventory();

            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick2(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null && event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory draginventory = event.getInventory();
        if (draginventory != null && event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);
        }
    }

    public void setPlayerStatus(Player player, Double[] data) {
        Scaleshifter.scaleUtil.setPlayerScale(player, data[0]);
        FoliaScheduler.getRegionScheduler().runDelayed(Scaleshifter.instance, player.getLocation(), (o) -> {
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(data[1]);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(data[2]);
            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(data[3]);
            player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(data[4]);
            player.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER).setBaseValue(data[5]);
            player.getAttribute(Attribute.GENERIC_GRAVITY).setBaseValue(data[6]);
            player.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(data[7]);
            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(data[8]);
            player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(data[9]);
            player.getAttribute(Attribute.GENERIC_STEP_HEIGHT).setBaseValue(data[10]);
            player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(data[11]);
            player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).setBaseValue(data[12]);
            player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).setBaseValue(data[13]);
        }, 10);
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, GUI_TITLE);
        int slot = 0;
        for (Map.Entry<String, Double[]> entry : SCALE_DATA.entrySet()) {
            String itemName = entry.getKey();
            ItemStack scaleItem = new ItemStack(getMaterialFromScale(itemName));
            ItemMeta scaleMeta = scaleItem.getItemMeta();
            if (scaleMeta != null) {
                scaleMeta.setDisplayName(ChatColor.GREEN + itemName);
                scaleItem.setItemMeta(scaleMeta);
            }
            gui.setItem(slot++, scaleItem);
        }
        for (int i = slot; i < 9; i++) {
            ItemStack emptyItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta emptyMeta = emptyItem.getItemMeta();
            if (emptyMeta != null) {
                emptyMeta.setDisplayName(" ");
                emptyItem.setItemMeta(emptyMeta);
            }
            gui.setItem(i, emptyItem);
        }
        player.openInventory(gui);
    }


    private Material getMaterialFromScale(String scale) {
        switch (scale) {
            case "Tiny":
                return Material.COPPER_INGOT;
            case "Small":
                return Material.IRON_INGOT;
            case "Normal":
                return Material.GOLD_INGOT;
            case "Large":
                return Material.DIAMOND;
            case "Massive":
                return Material.NETHERITE_INGOT;
            default:
                return Material.AIR;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        FoliaScheduler.getGlobalRegionScheduler().runDelayed(Scaleshifter.instance, (u) -> {
            if (event.getView().getTitle().equals(GUI_TITLE) && (!Scaleshifter.instance.playerInteractions.containsKey(player.getUniqueId()) || !Scaleshifter.instance.playerInteractions.get(player.getUniqueId())))
                openGUI(player);
        }, 1);
    }
}
