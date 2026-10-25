package org.statera.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MerchantCheckListener implements Listener {

    private final JavaPlugin plugin;
    private final HttpClient http = HttpClient.newHttpClient();

    public MerchantCheckListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || !(event.getRightClicked() instanceof Villager villager)) return;
        Player player = event.getPlayer();
        if (player.hasPermission("statera.merchant.bypass")) return;
        MerchantRecipe offer = villager.getRecipes().stream().filter(MerchantCheckListener::isInvalid).findFirst().orElse(null);
        if (offer == null) return;

        event.setCancelled(true);
        Location loc = villager.getLocation();
        String where = loc.getWorld().getName() + " " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
        String price = offer.getIngredients().stream().map(i -> i.getAmount() + "x " + i.getType()).toList().toString();
        villager.getWorld().createExplosion(loc, 2f, false, false, villager);
        villager.setHealth(0);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                plugin.getConfig().getString("merchant-command", "").replace("{player}", player.getName()));
        String text = player.getName() + " (" + player.getUniqueId() + "), " + where + ", " + price;
        plugin.getLogger().warning(text);
        notify(text);
    }

    // A book with Mending must cost what VillagerTradeListener sets: 1 Heart of the Sea and 8 Nautilus Shells.
    private static boolean isInvalid(MerchantRecipe recipe) {
        ItemStack result = recipe.getResult();
        if (result.getType() != Material.ENCHANTED_BOOK || !(result.getItemMeta() instanceof EnchantmentStorageMeta meta)) return false;
        if (meta.getStoredEnchants().keySet().stream().noneMatch(e -> e.getKey().equals(Enchantment.MENDING.getKey()))) return false;
        List<ItemStack> in = recipe.getIngredients();
        return !(in.size() == 2
                && in.get(0).getType() == Material.HEART_OF_THE_SEA && in.get(0).getAmount() == 1
                && in.get(1).getType() == Material.NAUTILUS_SHELL && in.get(1).getAmount() == 8);
    }

    private void notify(String text) {
        String url = plugin.getConfig().getString("merchant-webhook", "");
        if (url.isBlank()) return;
        String json = "{\"content\":\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"}";
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        http.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .exceptionally(e -> {
                    plugin.getLogger().warning("Webhook: " + e.getMessage());
                    return null;
                });
    }
}
