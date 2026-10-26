package org.statera.listener;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VillagerTradeListener implements Listener {

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        MerchantRecipe fixed = fix(event.getRecipe());
        if (fixed != null) event.setRecipe(fixed);
    }

    // Villagers keep the trades they had before; fix them the moment someone opens one
    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) return;
        List<MerchantRecipe> recipes = new ArrayList<>(villager.getRecipes());
        boolean changed = false;
        for (int i = 0; i < recipes.size(); i++) {
            MerchantRecipe fixed = fix(recipes.get(i));
            if (fixed == null) continue;
            recipes.set(i, fixed);
            changed = true;
        }
        if (changed) villager.setRecipes(recipes);
    }

    // Enchanted books: a maximum level becomes one lower, Mending costs 1 Heart of the Sea and 8 Nautilus Shells.
    // Returns null when the trade is already right, so fixing twice changes nothing.
    static MerchantRecipe fix(MerchantRecipe recipe) {
        ItemStack result = recipe.getResult();
        if (result.getType() != Material.ENCHANTED_BOOK || !(result.getItemMeta() instanceof EnchantmentStorageMeta meta)
                || !meta.hasStoredEnchants()) return null;

        ItemStack newResult = new ItemStack(Material.ENCHANTED_BOOK, result.getAmount());
        EnchantmentStorageMeta newMeta = (EnchantmentStorageMeta) newResult.getItemMeta();
        boolean mending = false;
        boolean changed = false;
        for (Map.Entry<Enchantment, Integer> entry : meta.getStoredEnchants().entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            if (enchantment.getKey().equals(Enchantment.MENDING.getKey())) mending = true;
            if (level == enchantment.getMaxLevel() && level > 1) {
                level--;
                changed = true;
            }
            newMeta.addStoredEnchant(enchantment, level, true);
        }
        if (mending && MerchantCheckListener.isInvalid(recipe)) changed = true;
        if (!changed) return null;
        newResult.setItemMeta(newMeta);

        MerchantRecipe newRecipe = new MerchantRecipe(newResult, recipe.getUses(), recipe.getMaxUses(),
                recipe.hasExperienceReward(), recipe.getVillagerExperience(), recipe.getPriceMultiplier(),
                recipe.getDemand(), recipe.getSpecialPrice(), recipe.shouldIgnoreDiscounts());
        newRecipe.setIngredients(mending
                ? List.of(new ItemStack(Material.HEART_OF_THE_SEA, 1), new ItemStack(Material.NAUTILUS_SHELL, 8))
                : recipe.getIngredients());
        return newRecipe;
    }
}
