package org.statera.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.view.AnvilView;

public class AnvilCostListener implements Listener {

    public class AnvilListener implements Listener {

        @EventHandler
        public void onAnvilPrepare(PrepareAnvilEvent event) {
            AnvilView view = event.getView();
            if (view.getRepairCost() >= 40) {
                view.setRepairCost(39);
            }
        }
    }
}