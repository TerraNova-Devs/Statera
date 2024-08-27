package org.statera.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MinecartListener implements Listener {

    private final List<EntityType> deniedEntitys = new ArrayList<>(Arrays.asList(EntityType.TNT_MINECART, EntityType.HOPPER_MINECART, EntityType.CHEST_MINECART, EntityType.FURNACE_MINECART));
    /*
    private final Plugin plugin;
    public static Map<Player, Minecart> mp = new HashMap<>();

    public MinecartListener(Statera plugin) {
        this.plugin = plugin;
    }
    */

    @EventHandler
    public void onEntityPlace(EntityPlaceEvent e) {
        //Denies other Minecart Types than the casual one
        if (deniedEntitys.contains(e.getEntityType())) {
            e.setCancelled(true);
            return;
        }
        /*
        Player p = e.getPlayer();
        if(p == null) return;

        if(mp.containsKey(p)) {
            p.sendMessage(Chat.errorFade("Du hast bereits ein Minecart in dieser Welt plaziert, bitte nutze erst / um dieses wieder einzusammeln."));
            e.setCancelled(true);
        }

        if(e.getEntity().getType().equals(EntityType.MINECART)){
           mp.put(p,new Minecart(Instant.now(),e.getEntity()));
        }

         */

    }
    /*
    public class Minecart {

        Instant i;
        Entity e;

        Minecart(Instant i, Entity e) {
            this.i = i;
            this.e = e;
        }

        public Instant getI() {
            return i;
        }

        public void setI(Instant i) {
            this.i = i;
        }

        public Entity getE() {
            return e;
        }
    }

     */

}
