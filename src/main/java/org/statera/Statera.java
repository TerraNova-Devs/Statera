package org.statera;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.statera.listener.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Statera extends JavaPlugin {

    Logger logger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();
        saveDefaultConfig();
        listenerRegistry();
        logger.log(Level.INFO, "Statera is enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Statera plugin disabled!");
    }

    public void listenerRegistry() {
        Bukkit.getPluginManager().registerEvents(new VillagerTradeListener(), this);
        Bukkit.getPluginManager().registerEvents(new PrepareAnvilListener(), this);
        Bukkit.getPluginManager().registerEvents(new AutomaticFarmListener(), this);
        Bukkit.getPluginManager().registerEvents(new MinecartListener(), this);
        Bukkit.getPluginManager().registerEvents(new OldElytraKiller(), this);
        getServer().getPluginManager().registerEvents(new AnvilCostListener(), this);
        getServer().getPluginManager().registerEvents(new MerchantCheckListener(this), this);
        getServer().getPluginManager().registerEvents(new CrafterCooldownListener(this), this);
    }


}
