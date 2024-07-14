package org.statera.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class PrepareAnvilListener implements Listener {

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();

        if (anvilInventory.getRepairCost() > 39) {
            anvilInventory.setRepairCost(39); // Set the repair cost to the maximum allowed value

            // Set the resulting item if it's null or would normally be too expensive
            ItemStack result = event.getResult();
            if (result != null) {
                event.setResult(result);
            } else {
                ItemStack firstItem = anvilInventory.getItem(0);
                ItemStack secondItem = anvilInventory.getItem(1);
                if (firstItem != null && secondItem != null) {
                    ItemStack combinedItem = firstItem.clone();
                    combinedItem.setAmount(1); // Ensure amount is correct
                    event.setResult(combinedItem);
                }
            }
        }
    }
}
