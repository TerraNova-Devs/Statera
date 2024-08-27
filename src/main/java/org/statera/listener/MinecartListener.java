package org.statera.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.statera.Statera;
import org.statera.utils.Chat;

import java.util.*;

public class MinecartListener implements Listener {

    private final List<EntityType> deniedEntitys = new ArrayList<>(Arrays.asList(EntityType.TNT_MINECART,EntityType.HOPPER_MINECART,EntityType.CHEST_MINECART,EntityType.FURNACE_MINECART));

    private final Plugin plugin;
    private Map<Player,BukkitTask> tasks = new HashMap<>();

    public MinecartListener(Statera plugin) {
        this.plugin = plugin;
    }


/*Ü
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        //Denies other Minecart Types than the casual one
        if(deniedEntitys.contains(e.getEntityType())){
            e.setCancelled(true);
            return;
        };

        if(e.getEntity().getType() == EntityType.MINECART){
            BukkitTask task = new BukkitRunnable() {

                @Override
                public void run() {

                }
            }.runTaskTimer(plugin, 0, 20 * 60 * 5);
            tasks.put(task);
        }



    }

 */

    @EventHandler
    public void onEntityPlace(EntityPlaceEvent e) {
        //Denies other Minecart Types than the casual one
        if(deniedEntitys.contains(e.getEntityType())){
            e.setCancelled(true);
            return;
        };

        Player p = e.getPlayer();

        if(tasks.containsKey(p)) {
            p.sendMessage(Chat.errorFade("Du hast bereits ein Minecart in dieser Welt plaziert, bitte nutze erst / um dieses wieder einzusammeln."));
        }

        if(e.getEntity().getType() == EntityType.MINECART){
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {

                }
            }.runTaskTimer(plugin, 0, 20 * 60 * 5);
            tasks.put(p,task);
        }



    }

}
