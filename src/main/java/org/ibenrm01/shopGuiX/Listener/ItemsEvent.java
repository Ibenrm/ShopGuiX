package org.ibenrm01.shopGuiX.Listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ibenrm01.shopGuiX.Inventory.GUIHandler;
import org.ibenrm01.shopGuiX.Utility;
import org.ibenrm01.shopGuiX.YAMLConfig.Lang;
import org.ibenrm01.shopGuiX.YAMLConfig.Settings;
import org.ibenrm01.shopGuiX.player.InvenGUI;
import org.ibenrm01.shopGuiX.player.PlayerInventorys;

import java.util.HashMap;
import java.util.Map;

public class ItemsEvent implements Listener {

    private Settings settings = Settings.getInstance();
    private String prefix = ChatColor.translateAlternateColorCodes('&', settings.getConfig().getString("serverName"));
    private Lang lang = Lang.getInstance();
    private Utility utils = Utility.getInstance();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        InvenGUI invenGUI = InvenGUI.get(p.getUniqueId());
        PlayerInventorys plInven = PlayerInventorys.get(p.getUniqueId());

        if (invenGUI == null || plInven == null || !invenGUI.getOpenShop()) {
            return;
        }
        if (invenGUI.getStage() == null || invenGUI.getStage().isEmpty()) {
            InvenGUI.remove(p.getUniqueId());
            PlayerInventorys.remove(p.getUniqueId());
            p.closeInventory();
            return;
        }
        
        switch (invenGUI.getStage()) {
            case "openQuantitySelector":
                handleQuantitySelectorClickDelayed(e, p, clicked, invenGUI, plInven);
                break;

            case "openStackSelector":
                handleStackSelectorClickDelayed(e, p, clicked, invenGUI, plInven);
                break;
        }
    }

    private void handleQuantitySelectorClickDelayed(InventoryClickEvent e, Player p, ItemStack clicked, InvenGUI invenGUI, PlayerInventorys plInven) {
        int slot = e.getSlot();
        Material type = clicked.getType();
        int amount = invenGUI.getItemsAmount();
        ItemStack item = new ItemStack(Material.matchMaterial(invenGUI.getTypeItems()), amount);

        int prices = utils.findItemPrice(GUIHandler.getInstance().formatTitle(plInven.getCategory()), GUIHandler.getInstance().formatKey(invenGUI.getTypeItems()));
        int totalprice = prices * amount;

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("price", utils.formatToRupiah(totalprice));
        placeholders.put("amount", "" + invenGUI.getItemsAmount());
        placeholders.put("name", GUIHandler.getInstance().formatTitle(invenGUI.getTypeItems()));

        if (type == Material.GREEN_STAINED_GLASS_PANE) {
            if (slot == 24) amount = Math.min(64, amount + 1);
            else if (slot == 25) amount = Math.min(64, amount + 10);
            else if (slot == 26) amount = Math.min(64, amount + 64);
        } else if (type == Material.RED_STAINED_GLASS_PANE) {
            if (slot == 18) amount = Math.max(1, amount - 1);
            else if (slot == 19) amount = Math.max(1, amount - 10);
            else if (slot == 20) amount = Math.max(1, amount - 32);
        } else if (slot == 38 && type == Material.LIME_DYE) {
            if (!Utility.getInstance().hasAvaliableSlot(p)) {
                invenGUI.setOpenShop(false);
                p.sendMessage(prefix + " " + utils.setColor(utils.replace(lang.getConfig().getString("buy.inventoryfull"), placeholders)));
                p.closeInventory();
                return;
            }
            String[] pay = utils.payment(p.getUniqueId().toString(), totalprice);
            if (pay[0].equals("notenough")) {
                invenGUI.setOpenShop(false);
                p.sendMessage(prefix + " " + utils.setColor(utils.replace(lang.getConfig().getString("buy.no-enoughmoney"), placeholders)));
                p.closeInventory();
                return;
            } else if (pay[0].equals("success")) {
                invenGUI.setOpenShop(false);
                p.sendMessage(prefix + " " + utils.setColor(utils.replace(lang.getConfig().getString("buy.success"), placeholders)));
                p.closeInventory();
                p.getInventory().addItem(item);
                return;
            } else {
                invenGUI.setOpenShop(false);
                p.sendMessage(prefix + " " + utils.setColor(utils.replace(lang.getConfig().getString("buy.no-enoughmoney"), placeholders)));
                p.closeInventory();
                return;
            }
        } else if (slot == 42 && type == Material.RED_DYE) {
            invenGUI.setOpenShop(false);
            p.closeInventory();
            return;
        } else if (slot == 40 && type == Material.CHEST) {
            invenGUI.setStage("openStackSelector");
            Inventory invensss = GUIHandler.getInstance().openStackSelector(p, item);
            plInven.setInventory(invensss);
            p.openInventory(invensss);
            return;
        }

        invenGUI.setItemsAmount(amount);
        Inventory inven = GUIHandler.getInstance().openQuantitySelector(p, item, amount);
        plInven.setInventory(inven);

        p.openInventory(inven);
    }

    private void handleStackSelectorClickDelayed(InventoryClickEvent e, Player p, ItemStack clicked, InvenGUI invenGUI, PlayerInventorys plInven) {
        int slot = e.getSlot();
        Material type = clicked.getType();

        if (slot == 36 && type == Material.RED_DYE) {
            invenGUI.setOpenShop(false);
            p.closeInventory();
            return;
        }

        int[] validSlots = {14, 15, 16, 23, 24, 25, 32, 33, 34};
        for (int i = 0; i < validSlots.length; i++) {
            if (slot == validSlots[i]) {
                int stacks = i + 1;
                int amounts = stacks * 64;

                int prices = utils.findItemPrice(GUIHandler.getInstance().formatTitle(plInven.getCategory()), GUIHandler.getInstance().formatKey(invenGUI.getTypeItems()));
                int totalprice = prices * amounts;

                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("price", utils.formatToRupiah(totalprice));
                placeholders.put("amount", "" + amounts);
                placeholders.put("name", GUIHandler.getInstance().formatTitle(invenGUI.getTypeItems()));

                if (!Utility.getInstance().hasAvailableSlots(p, amounts / 64)) {
                    invenGUI.setOpenShop(false);
                    p.sendMessage(prefix + " " + utils.setColor(utils.replace(lang.getConfig().getString("buy.inventoryfull"), placeholders)));
                    p.closeInventory();
                    return;
                }
                String[] pay = utils.payment(p.getUniqueId().toString(), totalprice);
                if (pay[0].equals("notenough")) {
                    invenGUI.setOpenShop(false);
                    p.sendMessage(prefix + " " + utils.setColor(utils.replace(lang.getConfig().getString("buy.no-enoughmoney"), placeholders)));
                    p.closeInventory();
                } else if (pay[0].equals("success")) {
                    invenGUI.setOpenShop(false);
                    p.sendMessage(prefix + " " + utils.setColor(utils.replace(lang.getConfig().getString("buy.success"), placeholders)));
                    p.closeInventory();
                    p.getInventory().addItem(new ItemStack(Material.matchMaterial(invenGUI.getTypeItems()), amounts));
                } else {
                    invenGUI.setOpenShop(false);
                    p.sendMessage(prefix + " " + utils.setColor(utils.replace(lang.getConfig().getString("buy.no-enoughmoney"), placeholders)));
                    p.closeInventory();
                }
                return;
            }
        }
    }

}
