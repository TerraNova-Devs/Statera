package org.statera.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.statera.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VillagerTradeListener implements Listener {

    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        Villager villager = (Villager) event.getEntity();
        List<MerchantRecipe> newRecipes = new ArrayList<>();

        villager.getRecipes().forEach( r -> {
            ItemStack result = r.getResult();
            Bukkit.broadcast(ChatUtils.returnRedFade("Ketchup"));
            if (result.getType() == Material.ENCHANTED_BOOK){
                if (result.hasItemMeta() && result.getItemMeta() instanceof EnchantmentStorageMeta meta) {
                    Map<Enchantment, Integer> enchantments = meta.getStoredEnchants();

                    if (!enchantments.isEmpty()) {
                        Bukkit.broadcast(ChatUtils.returnRedFade("Currywurst"));
                        ItemStack newResult = new ItemStack(result.getType());
                        EnchantmentStorageMeta newMeta = (EnchantmentStorageMeta) newResult.getItemMeta();
                        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                            Bukkit.broadcast(ChatUtils.returnRedFade("Mayo"));
                            Enchantment enchantment = entry.getKey();
                            int level = entry.getValue();
                            if (level > 1 && level == enchantment.getMaxLevel()) {
                                newMeta.addStoredEnchant(enchantment, level - 1, true);
                                Bukkit.broadcast(ChatUtils.returnRedFade("Changed Top-Level Enchantment, " + enchantment.getMaxLevel()));

                            } else {
                                newMeta.addStoredEnchant(enchantment, level, true);
                            }
                        }
                        newResult.setItemMeta(newMeta);
                        MerchantRecipe newRecipe = new MerchantRecipe(newResult, r.getMaxUses());
                        newRecipe.setIngredients(r.getIngredients());

                        newRecipes.add(newRecipe);
                    }
                }
            } else {
                newRecipes.add(r);
            }
        });

        villager.setRecipes(newRecipes);
    }
}
