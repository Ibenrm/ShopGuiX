package org.ibenrm01.shopGuiX.Listener.sell;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ibenrm01.shopGuiX.Utility;
import org.ibenrm01.shopGuiX.YAMLConfig.Lang;
import org.ibenrm01.shopGuiX.YAMLConfig.Sell;
import org.ibenrm01.shopGuiX.YAMLConfig.Settings;
import org.ibenrm01.shopGuiX.player.sell.PlayerSell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryEventSell implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        PlayerSell ps = PlayerSell.get(player.getUniqueId());
        if (ps == null || !ps.getActiveGUI()) return;
        Inventory sellInventory = e.getInventory();
        YamlConfiguration sellConfig = Sell.getInstance().getConfig();
        List<Map<?, ?>> sellList = sellConfig.getMapList("mainmenu");
        int totalPrice = 0;
        for (ItemStack item : sellInventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            String itemType = item.getType().name();
            int pricePerItem = -1;
            for (Map<?, ?> sellItem : sellList) {
                if (itemType.equalsIgnoreCase(String.valueOf(sellItem.get("type")))) {
                    pricePerItem = Integer.parseInt(String.valueOf(sellItem.get("price")));
                    break;
                }
            }
            if (pricePerItem > 0) {
                int amount = item.getAmount();
                totalPrice += pricePerItem * amount;
            } else {
                HashMap<Integer, ItemStack> notAdded = player.getInventory().addItem(item);
                for (ItemStack leftover : notAdded.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                }
            }
        }

        sellInventory.clear();

        if (totalPrice > 0) {
            String[] statue = Utility.getInstance().sellPayment(player.getUniqueId().toString(), totalPrice);

            if (!statue[0].equals("success")) {
                player.sendMessage(Utility.getInstance().setColor(Settings.getInstance().getConfig().getString("serverName")) + " " + Utility.getInstance().setColor(Lang.getInstance().getConfig().getString("add.onlyPlayer")));
                return;
            }
            Map<String, String> placeholderss = new HashMap<>();
            placeholderss.put("price", String.valueOf(totalPrice));
            player.sendMessage(Utility.getInstance().setColor(Settings.getInstance().getConfig().getString("serverName")) + " " + Utility.getInstance().setColor(Utility.getInstance().replace(Lang.getInstance().getConfig().getString("sell.success-batch"), placeholderss)));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        } else {
            player.sendMessage(Utility.getInstance().setColor(Settings.getInstance().getConfig().getString("serverName")) + " " + Utility.getInstance().setColor(Lang.getInstance().getConfig().getString("sell.no-sell-batch")));
        }
        PlayerSell.remove(player.getUniqueId());
    }

}
