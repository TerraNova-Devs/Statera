package org.statera;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.statera.commands.RecallCommand;
import org.statera.listener.AutomaticFarmListener;
import org.statera.listener.MinecartListener;
import org.statera.listener.PrepareAnvilListener;
import org.statera.listener.VillagerTradeListener;
import org.statera.packet.AnvilPacketHandler;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Statera extends JavaPlugin {

    private AnvilPacketHandler anvilPacketHandler;
    Logger logger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();

        if (getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().severe("ProtocolLib is required to run this plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        listenerRegistry();
        anvilPacketHandler = new AnvilPacketHandler(this);
        logger.log(Level.INFO, "Statera is enabled");
    }

    @Override
    public void onDisable() {
        if (anvilPacketHandler != null) {
            anvilPacketHandler.unregister();
        }
        getLogger().info("Statera plugin disabled!");
    }

    public void listenerRegistry() {
        Bukkit.getPluginManager().registerEvents(new VillagerTradeListener(), this);
        Bukkit.getPluginManager().registerEvents(new PrepareAnvilListener(), this);
        Bukkit.getPluginManager().registerEvents(new AutomaticFarmListener(), this);
        Bukkit.getPluginManager().registerEvents(new MinecartListener(), this);
    }



}
