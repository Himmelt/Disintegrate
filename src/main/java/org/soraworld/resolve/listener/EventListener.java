package org.soraworld.resolve.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.soraworld.resolve.config.RslConfig;

public class EventListener implements Listener {

    private final RslConfig config;

    public EventListener(RslConfig config) {
        this.config = config;
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            Inventory inv = event.getClickedInventory();
            if (inv != null && inv.equals(config.getResolver(player))) {
                config.checkClick(player, event.getRawSlot());
                if (event.getRawSlot() != 13) event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void closeInventory(InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if (inv != null && inv.equals(config.getResolver(player)) && inv.getItem(13) != null) {
                player.getInventory().addItem(inv.getItem(13));
                inv.setItem(13, null);
            }
        }
    }

}
