package org.statera.listener;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.view.AnvilView;

public class PrepareAnvilListener implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        AnvilView view = event.getView();
        int repairCost = view.getRepairCost();
        view.setMaximumRepairCost(Integer.MAX_VALUE);
        if (repairCost >= 40) {
            view.setRepairCost(39);
        }
        if(event.getInventory().getFirstItem() != null &&
                event.getInventory().getSecondItem() != null &&
                event.getInventory().getFirstItem().getType() == Material.ELYTRA &&
                event.getInventory().getSecondItem().getType() == Material.PHANTOM_MEMBRANE){
            view.setRepairCost(0);
        }
        if (result != null) {
            if (result.getType() == Material.ELYTRA) {
                boolean hasMending = result.getEnchantmentLevel(Enchantment.MENDING) > 0;
                boolean hasUnbreaking = result.getEnchantmentLevel(Enchantment.UNBREAKING) > 0;

                if (hasMending && hasUnbreaking) {
                    event.setResult(null);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.ANVIL) {
            AnvilInventory inventory = (AnvilInventory) event.getInventory();
            AnvilView view = (AnvilView) event.getView();
            int repairCost = view.getRepairCost();
            view.setMaximumRepairCost(Integer.MAX_VALUE);
            if (repairCost >= 40) {
                view.setRepairCost(39);
            }

            if(inventory.getFirstItem() != null &&
                    inventory.getSecondItem() != null &&
                    inventory.getFirstItem().getType() == Material.ELYTRA &&
                    inventory.getSecondItem().getType() == Material.PHANTOM_MEMBRANE){
                view.setRepairCost(0);
                ItemMeta meta = inventory.getFirstItem().getItemMeta();
                if (meta instanceof Repairable) {
                    Repairable repairable = (Repairable) meta;
                    repairable.setRepairCost(0);
                    inventory.getFirstItem().setItemMeta(repairable);
                }
            }
        }
    }
}