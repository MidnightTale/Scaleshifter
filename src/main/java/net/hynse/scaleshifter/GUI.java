package net.hynse.scaleshifter;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
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

                if (itemName.equalsIgnoreCase("Tiny")) {
                    setPlayerStatus(player,
                            4.2,
                            0.10000000149011612,
                            20.0,
                            4.0,
                            1.0,
                            1.0,
                            0.08,
                            0.41999998688697815,
                            0.0,
                            3.0,
                            0.06,
                            1.0,
                            4.5,
                            4.5);
                } else if (itemName.equalsIgnoreCase("Small")) {
                    setPlayerStatus(player,
                            0.8,
                            0.10000000149011612,
                            20.0,
                            4.0,
                            1.0,
                            1.0,
                            0.08,
                            0.41999998688697815,
                            0.0,
                            3.0,
                            0.06,
                            1.0,
                            4.5,
                            4.5);
                } else if (itemName.equalsIgnoreCase("Normal")) {
                    setPlayerStatus(player,
                            1.0,
                            0.10000000149011612,
                            20.0,
                            4.0,
                            1.0,
                            1.0,
                            0.08,
                            0.41999998688697815,
                            0.0,
                            3.0,
                            0.06,
                            1.0,
                            4.5,
                            4.5);
                } else if (itemName.equalsIgnoreCase("Large")) {
                    setPlayerStatus(player,
                            1.32,
                            0.10000000149011612,
                            20.0,
                            4.0,
                            1.0,
                            1.0,
                            0.08,
                            0.41999998688697815,
                            0.0,
                            3.0,
                            0.06,
                            1.0,
                            4.5,
                            4.5);
                } else if (itemName.equalsIgnoreCase("Massive")) {
                    setPlayerStatus(player,
                            1.6,
                            0.10000000149011612,
                            20.0,
                            4.0,
                            1.0,
                            1.0,
                            0.08,
                            0.41999998688697815,
                            0.0,
                            3.0,
                            0.06,
                            1.0,
                            4.5,
                            4.5);
                }
                Scaleshifter.instance.playerInteractions.put(player.getUniqueId(), true);
                getLogger().info("Player " + player.getName() + " clicked in inventory. Interaction status updated.");
                player.closeInventory();
            }
        }
    }
    public void setPlayerStatus(Player player, Double scale, Double speed, Double maxhealth, Double attackspeed, Double attackdamage, Double falldamangemultiple, Double gravity, Double jumpstrength, Double knockbackresitance, Double safefalldistance, Double stepheight, Double blockbreakspeed, Double blockinteractionrange, Double entityinteractionrange) {
        Scaleshifter.scaleUtil.setPlayerScale(player, scale);
        FoliaScheduler.getRegionScheduler().runDelayed(Scaleshifter.instance, player.getLocation(), (o) -> {
            player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxhealth);
            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(attackspeed);
            player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(attackdamage);
            player.getAttribute(Attribute.GENERIC_FALL_DAMAGE_MULTIPLIER).setBaseValue(falldamangemultiple);
            player.getAttribute(Attribute.GENERIC_GRAVITY).setBaseValue(gravity);
            player.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(jumpstrength);
            player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(knockbackresitance);
            player.getAttribute(Attribute.GENERIC_SAFE_FALL_DISTANCE).setBaseValue(safefalldistance);
            player.getAttribute(Attribute.GENERIC_STEP_HEIGHT).setBaseValue(stepheight);
            player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED).setBaseValue(blockbreakspeed);
            player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).setBaseValue(blockinteractionrange);
            player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).setBaseValue(entityinteractionrange);
        },10);
        getLogger().info("Setting status" + player
                + "To (Speed:" + speed
                + ", Max Health" + maxhealth
                + ", Attack Speed" + attackspeed
                + ", Attack Damage" + attackdamage
                + ", Fall Damage Multiple" + falldamangemultiple
                + ", Gravity" + gravity
                + ", Jump Strength" + jumpstrength
                + ", Knockback Resitance" + knockbackresitance
                + ", Safe Fall Distance" + safefalldistance
                + ", Step Height" + stepheight
                + ", Block Break Speed" + blockbreakspeed
                + ", Block Interaction Range" + blockinteractionrange
                + ", Entity Interaction Range" + entityinteractionrange);
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "Choose Your Scale");

        ItemStack tinyScaleItem = new ItemStack(Material.COPPER_INGOT);
        ItemMeta tinyScaleMeta = tinyScaleItem.getItemMeta();
        if (tinyScaleMeta != null) {
            tinyScaleMeta.setDisplayName(ChatColor.GREEN + "Tiny");
            tinyScaleItem.setItemMeta(tinyScaleMeta);
        }
        gui.setItem(0, tinyScaleItem);

        ItemStack smallScaleItem = new ItemStack(Material.IRON_INGOT);
        ItemMeta smallScaleMeta = smallScaleItem.getItemMeta();
        if (smallScaleMeta != null) {
            smallScaleMeta.setDisplayName(ChatColor.GREEN + "Small");
            smallScaleItem.setItemMeta(smallScaleMeta);
        }
        gui.setItem(1, smallScaleItem);

        ItemStack normalScaleItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta normalScaleMeta = normalScaleItem.getItemMeta();
        if (normalScaleMeta != null) {
            normalScaleMeta.setDisplayName(ChatColor.YELLOW + "Normal");
            normalScaleItem.setItemMeta(normalScaleMeta);
        }
        gui.setItem(2, normalScaleItem);

        ItemStack largeScaleItem = new ItemStack(Material.DIAMOND);
        ItemMeta largeScaleMeta = largeScaleItem.getItemMeta();
        if (largeScaleMeta != null) {
            largeScaleMeta.setDisplayName(ChatColor.AQUA + "Large");
            largeScaleItem.setItemMeta(largeScaleMeta);
        }
        gui.setItem(3, largeScaleItem);

        ItemStack bigScaleItem = new ItemStack(Material.NETHERITE_INGOT);
        ItemMeta bigScaleMeta = bigScaleItem.getItemMeta();
        if (bigScaleMeta != null) {
            bigScaleMeta.setDisplayName(ChatColor.RED + "Massive");
            bigScaleItem.setItemMeta(bigScaleMeta);
        }
        gui.setItem(4, bigScaleItem);

        ItemStack emptyItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta emptyMeta = emptyItem.getItemMeta();
        if (emptyMeta != null) {
            emptyMeta.setDisplayName(" ");
            emptyItem.setItemMeta(emptyMeta);
        }
        gui.setItem(5, emptyItem);
        gui.setItem(6, emptyItem);
        gui.setItem(7, emptyItem);
        gui.setItem(8, emptyItem);

        player.openInventory(gui);
        getLogger().info("Opening GUI for player " + player.getName());
    }
}
