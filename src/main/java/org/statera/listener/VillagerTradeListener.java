package org.statera.listener;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VillagerTradeListener implements Listener {

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        MerchantRecipe recipe = event.getRecipe();
        ItemStack result = recipe.getResult();

        if (result.getType() == Material.ENCHANTED_BOOK && result.hasItemMeta()) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) result.getItemMeta();
            Map<Enchantment, Integer> enchantments = meta.getStoredEnchants();

            if (!enchantments.isEmpty()) {
                ItemStack newResult = new ItemStack(result.getType());
                EnchantmentStorageMeta newMeta = (EnchantmentStorageMeta) newResult.getItemMeta();
                boolean isMending = false;

                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    int level = entry.getValue();

                    if (enchantment == Enchantment.MENDING) {
                        isMending = true;
                    }

                    if (level == enchantment.getMaxLevel() && level > 1) {
                        newMeta.addStoredEnchant(enchantment, level - 1, true);
                    } else {
                        newMeta.addStoredEnchant(enchantment, level, true);
                    }
                }

                newResult.setItemMeta(newMeta);

                MerchantRecipe newRecipe;
                if (isMending) {
                    // Create a new recipe with 1 Heart of the Sea and 8 Nautilus Shells
                    List<ItemStack> ingredients = new ArrayList<>();
                    ingredients.add(new ItemStack(Material.HEART_OF_THE_SEA, 1));
                    ingredients.add(new ItemStack(Material.NAUTILUS_SHELL, 8));
                    newRecipe = new MerchantRecipe(newResult, recipe.getMaxUses());
                    newRecipe.setIngredients(ingredients);
                } else {
                    // Use the original ingredients for other enchantments
                    newRecipe = new MerchantRecipe(newResult, recipe.getMaxUses());
                    newRecipe.setIngredients(recipe.getIngredients());
                }

                event.setRecipe(newRecipe);
            }
        }
    }
}


