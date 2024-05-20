package net.hynse.scaleshifter;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GUI implements Listener {
    private static final Map<String, Double[]> SCALE_DATA = new LinkedHashMap<>();
    public final int CustomModelDataTiny = 69002;
    public final int CustomModelDataSmall = 69003;
    public final int CustomModelDataNormal = 69004;
    public final int CustomModelDataLarge = 69005;
    public final int CustomModelDataMassive = 69006;
    public final int CustomModelDataRandom = 69008;
    public Double[] data = null;

    static {
        SCALE_DATA.put("Tiny", new Double[]{0.42, 0.135, 16.0, 4.7, 0.95, 1.36, 0.058, 0.35, -0.16, 2.8, 0.5, 0.97, 4.0, 4.0});
        SCALE_DATA.put("Small", new Double[]{0.8, 0.12, 18.0, 4.26, 0.97, 1.2, 0.076, 0.37, -0.1, 2.5, 0.55, 0.98, 4.2, 4.2});
        SCALE_DATA.put("Normal", new Double[]{1.0, 0.10000000149011612, 20.0, 4.0, 1.0, 1.0, 0.08, 0.41999998688697815, 0.0, 3.0, 0.6, 1.0, 4.5, 4.5});
        SCALE_DATA.put("Large", new Double[]{1.32, 0.096, 26.0, 3.75, 1.5, 0.95, 0.085, 0.56, 0.16, 4.5, 0.98, 1.3, 5.0, 5.0});
        SCALE_DATA.put("Massive", new Double[]{1.6, 0.09265, 32.0, 3.4, 2.0, 0.9, 0.087, 0.59, 0.37, 5.0, 1.5, 1.6, 5.5, 5.5});
    }

    private static final String GUI_TITLE = "Choose Your Scale";

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);

            ItemStack item = event.getCurrentItem();
            if (item == null || !item.hasItemMeta()) {
                return;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.hasCustomModelData()) {
                return;
            }

            int customModelData = meta.getCustomModelData();

            switch (customModelData) {
                case CustomModelDataTiny -> data = SCALE_DATA.get("Tiny");
                case CustomModelDataSmall -> data = SCALE_DATA.get("Small");
                case CustomModelDataNormal -> data = SCALE_DATA.get("Normal");
                case CustomModelDataLarge -> data = SCALE_DATA.get("Large");
                case CustomModelDataMassive -> data = SCALE_DATA.get("Massive");
            }

            if (data == null) {
                return;
            }

            double playerScale = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_SCALE)).getBaseValue();

//            if (playerScale == data[0]) {
//                player.sendMessage(ChatColor.RED + "You are already " + ChatColor.stripColor(meta.getDisplayName()));
//            } else {
                player.updateInventory();
                FoliaScheduler.getGlobalRegionScheduler().runDelayed(Scaleshifter.instance, (o) -> {
                setPlayerStatus(player, data);
                Scaleshifter.instance.playerInteractions.put(player.getUniqueId(), true);
                player.closeInventory();
                    }, 2);
//                }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryMove(InventoryMoveItemEvent event) {
        Inventory sourceInventory = event.getSource();

        if (sourceInventory.getHolder() instanceof InventoryView inventoryView) {
            if (inventoryView.getTitle().equals(GUI_TITLE)) {
                event.setCancelled(true);
            }
        }

        Inventory destinationInventory = event.getDestination();
        if (destinationInventory.getHolder() instanceof InventoryView inventoryView) {
            if (inventoryView.getTitle().equals(GUI_TITLE)) {
                event.setCancelled(true);
            }
        }
    }


    public void setPlayerStatus(Player player, Double[] data) {
        Scaleshifter.scaleUtil.setPlayerScale(player, data[0]);
        FoliaScheduler.getRegionScheduler().runDelayed(Scaleshifter.instance, player.getLocation(), (o) -> {
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).setBaseValue(data[1]);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(data[2]);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).setBaseValue(data[3]);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(data[4]);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER)).setBaseValue(data[5]);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_GRAVITY)).setBaseValue(data[6]);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)).setBaseValue(data[7]);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE)).setBaseValue(data[8]);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE)).setBaseValue(data[9]);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_STEP_HEIGHT)).setBaseValue(data[10]);
            Objects.requireNonNull(player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED)).setBaseValue(data[11]);
            Objects.requireNonNull(player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE)).setBaseValue(data[12]);
            Objects.requireNonNull(player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE)).setBaseValue(data[13]);
        }, 10);
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, GUI_TITLE);
        int slot = 0;
        double playerScale = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_SCALE)).getBaseValue();

        for (Map.Entry<String, Double[]> entry : SCALE_DATA.entrySet()) {
            String itemName = entry.getKey();
            ItemStack scaleItem = new ItemStack(getMaterialFromScale(itemName));
            ItemMeta scaleMeta = scaleItem.getItemMeta();

            if (scaleMeta != null) {
                scaleMeta.setDisplayName(ChatColor.GREEN + itemName);
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Scale: " + entry.getValue()[0]);
                lore.add(ChatColor.GRAY + "Movement Speed: " + entry.getValue()[1]);
                lore.add(ChatColor.GRAY + "Max Health: " + entry.getValue()[2]);
                lore.add(ChatColor.GRAY + "Attack Speed: " + entry.getValue()[3]);
                lore.add(ChatColor.GRAY + "Attack Damage: " + entry.getValue()[4]);
                lore.add(ChatColor.GRAY + "Fall Damage Multiple: " + entry.getValue()[5]);
                lore.add(ChatColor.GRAY + "Gravity: " + entry.getValue()[6]);
                lore.add(ChatColor.GRAY + "Jump Strength: " + entry.getValue()[7]);
                lore.add(ChatColor.GRAY + "Knockback Resistance: " + entry.getValue()[8]);
                lore.add(ChatColor.GRAY + "Safe Fall Distance: " + entry.getValue()[9]);
                lore.add(ChatColor.GRAY + "Step Height: " + entry.getValue()[10]);
                lore.add(ChatColor.GRAY + "Block Break Speed: " + entry.getValue()[11]);
                lore.add(ChatColor.GRAY + "Block Interaction Range: " + entry.getValue()[12]);
                lore.add(ChatColor.GRAY + "Entity Interaction Range: " + entry.getValue()[13]);

                scaleMeta.setLore(lore);

                switch (itemName) {
                    case "Tiny" -> {
                        scaleMeta.setCustomModelData(CustomModelDataTiny);
                        if (playerScale == SCALE_DATA.get("Tiny")[0]) {
                            scaleMeta.setEnchantmentGlintOverride(true);
                        }
                    }
                    case "Small" -> {
                        scaleMeta.setCustomModelData(CustomModelDataSmall);
                        if (playerScale == SCALE_DATA.get("Small")[0]) {
                            scaleMeta.setEnchantmentGlintOverride(true);
                        }
                    }
                    case "Normal" -> {
                        scaleMeta.setCustomModelData(CustomModelDataNormal);
                        if (playerScale == SCALE_DATA.get("Normal")[0]) {
                            scaleMeta.setEnchantmentGlintOverride(true);
                        }
                    }
                    case "Large" -> {
                        scaleMeta.setCustomModelData(CustomModelDataLarge);
                        if (playerScale == SCALE_DATA.get("Large")[0]) {
                            scaleMeta.setEnchantmentGlintOverride(true);
                        }
                    }
                    case "Massive" -> {
                        scaleMeta.setCustomModelData(CustomModelDataMassive);
                        if (playerScale == SCALE_DATA.get("Massive")[0]) {
                            scaleMeta.setEnchantmentGlintOverride(true);
                        }
                    }
                }

                scaleItem.setItemMeta(scaleMeta);
            }
            gui.setItem(slot++, scaleItem);
        }

        for (int i = slot; i < 9; i++) {
            ItemStack emptyItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta emptyMeta = emptyItem.getItemMeta();
            if (emptyMeta != null) {
                emptyMeta.setDisplayName(" ");
                emptyMeta.setCustomModelData(69007);
                emptyItem.setItemMeta(emptyMeta);
            }
            gui.setItem(i, emptyItem);
        }
        player.openInventory(gui);
    }



    private Material getMaterialFromScale(String scale) {
        return switch (scale) {
            case "Tiny" -> Material.COPPER_INGOT;
            case "Small" -> Material.IRON_INGOT;
            case "Normal" -> Material.GOLD_INGOT;
            case "Large" -> Material.DIAMOND;
            case "Massive" -> Material.NETHERITE_INGOT;
            default -> Material.AIR;
        };
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
