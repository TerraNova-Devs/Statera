package org.statera.listener;

import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

// Each Mending book bought at an old price is counted (player data, HuskSync carries it between servers) and logged.
// The second one strips Mending from the inventory, blows up and removes the villager and runs merchant-command.
public class MerchantCheckListener implements Listener {

    private static final int LIMIT = 2;

    private final JavaPlugin plugin;
    private final NamespacedKey key;
    private final Set<UUID> pending = new HashSet<>();

    public MerchantCheckListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "mending-trades");
    }

    @EventHandler(ignoreCancelled = true)
    public void onTrade(PlayerTradeEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("statera.merchant.bypass") || !isInvalid(event.getTrade())) return;
        if (pending.contains(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        int count = player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, 0) + 1;
        AbstractVillager villager = event.getVillager();
        Location loc = villager.getLocation();
        String price = event.getTrade().getIngredients().stream().map(i -> i.getAmount() + "x " + i.getType()).toList().toString();
        String text = player.getName() + " (" + player.getUniqueId() + "), " + loc.getWorld().getName() + " "
                + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + ", " + price + ", " + count + "/" + LIMIT;

        if (count < LIMIT) {
            player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, count);
            plugin.getLogger().warning(text);
            return;
        }

        player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 0);
        pending.add(player.getUniqueId());
        plugin.getLogger().warning(text + ", Mending entfernt, Strafe: " + plugin.getConfig().getString("merchant-command", ""));
        // A tick later the bought book has landed in the inventory or on the cursor
        Bukkit.getScheduler().runTask(plugin, () -> {
            pending.remove(player.getUniqueId());
            if (player.isOnline()) {
                player.setItemOnCursor(strip(player.getItemOnCursor()));
                player.closeInventory();
                strip(player.getInventory());
            }
            if (villager.isValid()) {
                villager.getWorld().createExplosion(villager.getLocation(), 2f, false, false, villager);
                villager.remove();
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    plugin.getConfig().getString("merchant-command", "").replace("{player}", player.getName()));
        });
    }

    // A book with Mending must cost what VillagerTradeListener sets: 1 Heart of the Sea and 8 Nautilus Shells.
    static boolean isInvalid(MerchantRecipe recipe) {
        ItemStack result = recipe.getResult();
        if (result.getType() != Material.ENCHANTED_BOOK || !(result.getItemMeta() instanceof EnchantmentStorageMeta meta)) return false;
        if (meta.getStoredEnchants().keySet().stream().noneMatch(e -> e.getKey().equals(Enchantment.MENDING.getKey()))) return false;
        List<ItemStack> in = recipe.getIngredients();
        return !(in.size() == 2
                && in.get(0).getType() == Material.HEART_OF_THE_SEA && in.get(0).getAmount() == 1
                && in.get(1).getType() == Material.NAUTILUS_SHELL && in.get(1).getAmount() == 8);
    }

    // Covers armour and offhand too: getContents() of a player inventory includes them
    private static void strip(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) contents[i] = strip(contents[i]);
        inventory.setContents(contents);
    }

    private static ItemStack strip(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return item;
        ItemMeta meta = item.getItemMeta();
        meta.removeEnchant(Enchantment.MENDING);
        if (meta instanceof EnchantmentStorageMeta book) book.removeStoredEnchant(Enchantment.MENDING);
        if (meta instanceof BlockStateMeta state && state.getBlockState() instanceof ShulkerBox box) {
            strip(box.getInventory());
            state.setBlockState(box);
        }
        item.setItemMeta(meta);
        return item;
    }
}
