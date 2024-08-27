package org.statera.listener;

import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutomaticFarmListener implements Listener {

    //Denies Mob-drops from automated killing methods
    private final List<DamageType> deniedDamageTypes = new ArrayList<>(Arrays.asList(DamageType.LAVA, DamageType.DROWN, DamageType.CACTUS, DamageType.CRAMMING, DamageType.FALL));
    //If water is being used to harvest fields
    private final List<Material> deniedPlants = new ArrayList<>(Arrays.asList(Material.WHEAT, Material.CARROT, Material.BEETROOT, Material.POTATO, Material.BAMBOO, Material.SUGAR_CANE, Material.CACTUS));

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) return;
        if (deniedDamageTypes.contains(e.getDamageSource().getDamageType())) {
            e.setDroppedExp(0);
            e.getDrops().clear();
        }
    }

    //Denies Cactus growing into something
    @EventHandler
    public void onBlockGrowth(BlockGrowEvent e) {
        Location childPlant = e.getNewState().getBlock().getLocation();
        Location motherPlant = e.getNewState().getBlock().getLocation().subtract(0, 1, 0);
        if (!motherPlant.getBlock().getType().equals(Material.CACTUS)) return;
        if (!childPlant.add(1, 0, 0).getBlock().getType().equals(Material.AIR) || !childPlant.add(-2, 0, 0).getBlock().getType().equals(Material.AIR) || !childPlant.add(1, 0, 1).getBlock().getType().equals(Material.AIR) || !childPlant.add(0, 0, -2).getBlock().getType().equals(Material.AIR))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreakBlock(BlockBreakBlockEvent e) {
        if (deniedPlants.contains(e.getBlock().getType())) {
            e.getDrops().clear();
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e) {
        //Prevents a Piston from extending when there is no Block attached
        if (e.getBlocks().isEmpty()) {
            e.setCancelled(true);
            return;
        }

        // Denies if a block is pushed next to a cactus
        Location pistonDirection = e.getBlocks().getFirst().getLocation().subtract(e.getBlock().getLocation());
        Block block = e.getBlocks().getLast().getLocation().add(pistonDirection).getBlock();
        if (block.getLocation().add(pistonDirection).getBlock().getType().equals(Material.CACTUS)) e.setCancelled(true);

        //Denies if a Piston is used to harvest something
        if (deniedPlants.contains(e.getBlocks().getLast().getType())) e.setCancelled(true);
    }

}
