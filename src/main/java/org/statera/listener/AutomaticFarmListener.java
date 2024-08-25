package org.statera.listener;

import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Material;
import org.bukkit.damage.DamageType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutomaticFarmListener implements Listener {

    private final List<DamageType> deniedMobDeathTypes = new ArrayList<>(Arrays.asList(DamageType.LAVA,DamageType.DROWN,DamageType.CACTUS,DamageType.CRAMMING,DamageType.FALL));
    private final List<Material> deniedAutoFoodHarvestBlocks = new ArrayList<>(Arrays.asList(Material.WHEAT,Material.CARROT,Material.BEETROOT,Material.POTATO, Material.SUGAR_CANE));

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if(deniedMobDeathTypes.contains(e.getDamageSource().getDamageType())) {
            e.setDroppedExp(0);
            e.getDrops().clear();
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
    }

    @EventHandler
    public void onBlockGrowth(BlockGrowEvent e) {
        if (!e.getBlock().getBlockData().getMaterial().equals(Material.CACTUS)) return;
        e.getNewState().getDrops().clear();

    }

    @EventHandler
    public void onBlockBreakBlock(BlockBreakBlockEvent e) {
        if(deniedAutoFoodHarvestBlocks.contains(e.getBlock().getType())) {
            e.getDrops().clear();
        }
    }

}
