package org.statera.listener;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinecartListener implements Listener {

    private final List<EntityType> deniedEntitys = new ArrayList<>(Arrays.asList(EntityType.TNT_MINECART, EntityType.HOPPER_MINECART, EntityType.CHEST_MINECART, EntityType.FURNACE_MINECART));
    private final List<ItemStack> deniedItems = new ArrayList<>(Arrays.asList(new ItemStack(Material.TNT_MINECART), new ItemStack(Material.HOPPER_MINECART), new ItemStack(Material.CHEST_MINECART), new ItemStack(Material.FURNACE_MINECART)));


    @EventHandler
    public void onEntityDispense(BlockDispenseEvent event) {
        if (deniedItems.contains(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityPlace(EntityPlaceEvent e) {
        if (deniedEntitys.contains(e.getEntityType())) {
            e.setCancelled(true);
        }
    }
}
