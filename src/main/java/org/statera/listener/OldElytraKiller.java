package org.statera.listener;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.inventory.ItemStack;
import org.statera.utils.Chat;

import java.util.Arrays;
import java.util.Objects;

public class OldElytraKiller implements Listener {

    @EventHandler
    public void onElytraUsage(EntityToggleGlideEvent e) {
        if(!(e.getEntity() instanceof Player p)) return;

        ItemStack[] armor = p.getInventory().getArmorContents();

        Arrays.stream(armor)
                .filter(Objects::nonNull)
                .filter(stack -> stack.getType().equals(Material.ELYTRA))
                .filter(stack -> stack.getItemMeta().getEnchants().containsKey(Enchantment.MENDING))
                .filter(stack -> stack.getItemMeta().getEnchants().containsKey(Enchantment.UNBREAKING))
                .forEach(stack -> {
                    World w = p.getWorld();
                    w.createExplosion(p.getLocation(),10f,false,false);
                    ItemStack elytra = new ItemStack(Material.ELYTRA);
                    elytra.addEnchantment(Enchantment.MENDING, 1);
                    armor[2] = elytra;
                    p.getInventory().setArmorContents(armor);
                    p.sendMessage(Chat.errorFade("Jo digga deine Elytra ist nun legalisiert. Hau rein!"));
                });
    }

}
