package org.statera.listener;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

// A crafter crafts at most once per crafter-cooldown-ticks; earlier crafts are cancelled and use up nothing.
public class CrafterCooldownListener implements Listener {

    private final JavaPlugin plugin;
    private final Map<String, Integer> lastCraft = new HashMap<>();

    public CrafterCooldownListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CrafterCraftEvent event) {
        Block block = event.getBlock();
        String key = block.getWorld().getName() + ":" + block.getBlockKey();
        int now = Bukkit.getCurrentTick();
        Integer last = lastCraft.get(key);
        if (last != null && now - last < plugin.getConfig().getInt("crafter-cooldown-ticks", 20)) {
            event.setCancelled(true);
            return;
        }
        lastCraft.put(key, now);
    }
}
