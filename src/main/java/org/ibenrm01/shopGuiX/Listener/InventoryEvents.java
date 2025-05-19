// InventoryEvents.java
package org.ibenrm01.shopGuiX.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.ibenrm01.shopGuiX.ShopGuiX;
import org.ibenrm01.shopGuiX.player.InvenGUI;
import org.ibenrm01.shopGuiX.player.PlayerInventorys;

import java.util.UUID;

public class InventoryEvents implements Listener {

    private final ShopGuiX plugin = ShopGuiX.getInstance();

    private boolean isShopInventory(Player player) {
        InvenGUI invenGUI = InvenGUI.get(player.getUniqueId());
        return invenGUI != null && invenGUI.getOpenShop();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (isShopInventory(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        UUID uuid = player.getUniqueId();

        InvenGUI invenGUI = InvenGUI.get(uuid);
        PlayerInventorys plInven = PlayerInventorys.get(uuid);
        Inventory inv = plInven.getInventory();

        if (invenGUI != null && invenGUI.getOpenShop() && inv != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.getOpenInventory().getTopInventory() != inv) {
                    player.openInventory(inv);
                }
            }, 5L);
        } else {
            InvenGUI.remove(uuid);
            PlayerInventorys.remove(uuid);
        }
    }


    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();
        InvenGUI invenGUI = InvenGUI.get(player.getUniqueId());
        PlayerInventorys plInven = PlayerInventorys.get(player.getUniqueId());

        if (invenGUI != null && invenGUI.getOpenShop() && plInven != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.getOpenInventory().getTopInventory() != plInven.getInventory()) {
                    player.openInventory(plInven.getInventory());
                }
            }, 2L);
        }
    }
}
