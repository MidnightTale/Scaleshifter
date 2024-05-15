package net.hynse.scaleshifter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class ScaleOrb implements Listener {
    private final int CustomModelData = 69001;
    private final Material CustomItem = Material.HEART_OF_THE_SEA;
    private final NamespacedKey itemKey;

    public ScaleOrb() {
        this.itemKey = new NamespacedKey(Scaleshifter.instance, "scale_orb");
    }

    public void item() {
        ItemStack scaleOrbItem = new ItemStack(CustomItem, 1);
        ItemMeta meta = scaleOrbItem.getItemMeta();

        meta.setDisplayName("Scale Orb");
        meta.setCustomModelData(CustomModelData);
        meta.setMaxStackSize(1);
        meta.setEnchantmentGlintOverride(true);
        meta.setRarity(ItemRarity.EPIC);
        scaleOrbItem.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(itemKey, scaleOrbItem);
        recipe.shape("IXI", "OKP", "IHI");
        recipe.setIngredient('X', Material.COPPER_INGOT);
        recipe.setIngredient('O', Material.IRON_INGOT);
        recipe.setIngredient('K', Material.ENDER_PEARL);
        recipe.setIngredient('P', Material.DIAMOND);
        recipe.setIngredient('H', Material.NETHERITE_INGOT);
        recipe.setIngredient('I', Material.GOLD_INGOT);
        Bukkit.getServer().addRecipe(recipe);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != CustomItem) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData() || meta.getCustomModelData() != CustomModelData) return;

        Scaleshifter.instance.playerInteractions.put(player.getUniqueId(), false);
        Scaleshifter.gui.openGUI(player);
        item.setAmount(0);
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();

        for (ItemStack item : matrix) {
            if (item != null && item.getType() == CustomItem) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasCustomModelData() && meta.getCustomModelData() == CustomModelData) {
                    inventory.setResult(null);
                    return;
                }
            }
        }
    }
}
