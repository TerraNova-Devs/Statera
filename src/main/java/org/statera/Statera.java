package org.statera;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.statera.listener.PrepareAnvilListener;
import org.statera.listener.VillagerTradeListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Statera extends JavaPlugin {

    Logger logger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();

        logger.log(Level.INFO, "Statera is enabled");

        listenerRegistry();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void listenerRegistry() {
        Bukkit.getPluginManager().registerEvents(new VillagerTradeListener(), this);
        Bukkit.getPluginManager().registerEvents(new PrepareAnvilListener(), this);
    }
}
