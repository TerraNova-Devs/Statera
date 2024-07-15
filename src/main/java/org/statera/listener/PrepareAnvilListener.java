package org.statera.listener;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.statera.utils.ChatUtils;

import java.util.HashMap;
import java.util.Map;

public class PrepareAnvilListener implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        event.setResult(null);
        AnvilInventory anvilInventory = event.getInventory();

        // Check if we have valid items in the anvil
        ItemStack firstItem = anvilInventory.getItem(0);
        ItemStack secondItem = anvilInventory.getItem(1);
        String renameText = event.getInventory().getRenameText();

        if (firstItem == null) return;

        // Handle Elytra specific logic
        if (firstItem.getType() == Material.ELYTRA) {
            boolean hasMending = firstItem.getItemMeta().hasEnchant(Enchantment.MENDING);
            boolean hasUnbreaking = firstItem.getItemMeta().hasEnchant(Enchantment.UNBREAKING);

            if (secondItem != null) {
                boolean addingMending = hasEnchantment(secondItem, Enchantment.MENDING);
                boolean addingUnbreaking = hasEnchantment(secondItem, Enchantment.UNBREAKING);

                // If Elytra already has both Mending and Unbreaking or will have after the operation
                if ((hasMending && hasUnbreaking) || (hasMending && addingUnbreaking) || (hasUnbreaking && addingMending) || (addingMending && addingUnbreaking)) {
                    // Prevent the operation
                    event.setResult(null);
                    return;
                }
            }
        }

        // Handle renaming
        if (renameText != null && !renameText.isEmpty()) {
            ItemStack renamedItem = firstItem.clone();
            renamedItem.setAmount(1); // Ensure the correct amount
            ItemMeta meta = renamedItem.getItemMeta();
            if (meta != null) {
                meta.displayName(ChatUtils.stringToComponent(renameText));
                renamedItem.setItemMeta(meta);
            }
            event.setResult(renamedItem);
            anvilInventory.setRepairCost(Math.min(anvilInventory.getRepairCost(), 39));
            return;
        }

        // Validate and combine enchantments based on vanilla rules
        if (secondItem != null) {
            ItemStack combinedItem = firstItem.clone();
            combinedItem.setAmount(1); // Ensure correct amount

            boolean hasValidEnchantments = combineEnchantments(combinedItem, secondItem);

            // If no valid enchantments were added, cancel the combination
            if (!hasValidEnchantments) {
                event.setResult(null);
            } else {
                event.setResult(combinedItem);
            }
        }

        // Ensure the repair cost does not exceed 39
        anvilInventory.setRepairCost(Math.min(anvilInventory.getRepairCost(), 39));
    }

    private boolean hasEnchantment(ItemStack item, Enchantment enchantment) {
        if (item.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            return meta.hasStoredEnchant(enchantment);
        } else {
            return item.getItemMeta().hasEnchant(enchantment);
        }
    }

    private boolean combineEnchantments(ItemStack target, ItemStack source) {
        ItemMeta targetMeta = target.getItemMeta();
        Map<Enchantment, Integer> targetEnchantments = new HashMap<>(targetMeta.getEnchants());
        Map<Enchantment, Integer> sourceEnchantments = getEnchantsFromSource(source);

        boolean appliedAnyEnchantment = false;

        for (Map.Entry<Enchantment, Integer> entry : sourceEnchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int sourceLevel = entry.getValue();

            // Skip invalid enchantments for non-book items
            if (!enchantment.canEnchantItem(target) && !(target.getItemMeta() instanceof EnchantmentStorageMeta)) {
                continue;
            }

            appliedAnyEnchantment = true;

            if (targetEnchantments.containsKey(enchantment)) {
                int targetLevel = targetEnchantments.get(enchantment);
                if (targetLevel == sourceLevel) {
                    sourceLevel += 1; // Combine same level enchantments to the next level
                } else {
                    sourceLevel = Math.max(sourceLevel, targetLevel); // Keep the highest level
                }
            }

            if (sourceLevel > enchantment.getMaxLevel()) {
                sourceLevel = enchantment.getMaxLevel(); // Cap at the enchantment's max level
            }

            if (target.getItemMeta() instanceof EnchantmentStorageMeta) {
                ((EnchantmentStorageMeta) targetMeta).addStoredEnchant(enchantment, sourceLevel, true);
            } else {
                targetMeta.addEnchant(enchantment, sourceLevel, true);
            }
        }

        target.setItemMeta(targetMeta);
        return appliedAnyEnchantment;
    }

    private Map<Enchantment, Integer> getEnchantsFromSource(ItemStack source) {
        if (source.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta sourceMeta = (EnchantmentStorageMeta) source.getItemMeta();
            return sourceMeta.getStoredEnchants();
        } else {
            return source.getEnchantments();
        }
    }
}