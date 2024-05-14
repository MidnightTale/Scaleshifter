package net.hynse.scaleshifter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static net.hynse.scaleshifter.Scaleshifter.gui;

public class ScaleOrb {

    public void item () {
        ItemStack scaleOrbItem = new ItemStack(Material.HEART_OF_THE_SEA,1);
        scaleOrbItem.getItemMeta().setDisplayName("Scale Orb");
        NamespacedKey key = new NamespacedKey(Scaleshifter.instance, "scale_orb");


        ShapedRecipe recipe = new ShapedRecipe(key, scaleOrbItem);
        recipe.shape("IXI","OKP","IHI");
        recipe.setIngredient('X', Material.COPPER_INGOT);
        recipe.setIngredient('O', Material.IRON_INGOT);
        recipe.setIngredient('K', Material.ENDER_PEARL);
        recipe.setIngredient('P', Material.DIAMOND);
        recipe.setIngredient('H', Material.NETHERITE_INGOT);
        recipe.setIngredient('I', Material.GOLD_INGOT);
        Bukkit.addRecipe(recipe);
    }
    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getRecipe().getResult();
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(Scaleshifter.instance, "scale_orb");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                // Ensure the crafted item has the custom NBT data
                result.setItemMeta(meta);
                event.getInventory().setResult(result);
            }
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack craftedItem = event.getCurrentItem();
        ItemMeta meta = craftedItem.getItemMeta();
        if (meta != null) {
            NamespacedKey key = new NamespacedKey(Scaleshifter.instance, "scale_orb");
            if (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
                Player player = (Player) event.getWhoClicked();
                if ("scale_orb".equals(meta.getPersistentDataContainer().get(key, PersistentDataType.STRING))) {
                    // Replace the crafted item with the custom item
                    //event.setCancelled(true); // Cancel the crafting event
                    ItemStack customItem = new ItemStack(Material.HEART_OF_THE_SEA, 1);
                    ItemMeta customMeta = customItem.getItemMeta();
                    if (customMeta != null) {
                        customMeta.setDisplayName("§6Scale Orb");
                        customMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "scale_orb");
                        customMeta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);
                        customMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                        customMeta.setCustomModelData(69001);
                        customMeta.setMaxStackSize(1);
                        customMeta.setRarity(ItemRarity.EPIC);
                        customMeta.setCustomModelData(69001);
                        customItem.setItemMeta(customMeta);
                    }
                    player.setItemOnCursor(customItem);
                    //player.getInventory().addItem(customItem);
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
            NamespacedKey key = new NamespacedKey(Scaleshifter.instance, "scale_orb");

            if (dataContainer.has(key, PersistentDataType.STRING)) {
                String value = dataContainer.get(key, PersistentDataType.STRING);
                if ("scale_orb".equals(value)) {
                    gui.openGUI(event.getPlayer());
                }
                }
            }
        }

}
