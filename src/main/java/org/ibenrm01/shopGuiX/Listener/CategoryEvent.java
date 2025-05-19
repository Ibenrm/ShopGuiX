package org.ibenrm01.shopGuiX.Listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ibenrm01.shopGuiX.Inventory.GUIHandler;
import org.ibenrm01.shopGuiX.player.InvenGUI;
import org.ibenrm01.shopGuiX.player.PlayerInventorys;

public class CategoryEvent implements Listener {


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        InvenGUI invenGUI = InvenGUI.get(p.getUniqueId());
        PlayerInventorys plInven = PlayerInventorys.get(p.getUniqueId());

        if (invenGUI.getOpenShop()) {
            if (invenGUI.getStage().isEmpty()) {
                InvenGUI.remove(p.getUniqueId());
                PlayerInventorys.remove(p.getUniqueId());
                return;
            }

            switch (invenGUI.getStage()) {
                case "openMainMenu":
                    if (e.getSlot() == 49 && clicked.getType() == Material.BARRIER) {
                        invenGUI.setOpenShop(false);
                        p.closeInventory();
                    }
                    if (clicked.getType() == Material.BARRIER || clicked.getType() == Material.BLACK_STAINED_GLASS_PANE)
                        return;
                    if (clicked.getItemMeta() != null && clicked.getItemMeta().hasDisplayName()) {
                        String catName = clicked.getItemMeta().getDisplayName();
                        Object inven = GUIHandler.getInstance().openCategoryItems(p, catName.toLowerCase().replace(" ", "_"), plInven.getPages());
                        if (inven instanceof Object[]) {
                            Object[] arr = (Object[]) inven;
                            if (arr.length > 0 && "error".equals(arr[0])) {
                                return;
                            }
                        }
                        invenGUI.setStage("openCategoryItems");
                        plInven.setCategory(catName);
                        Object[] result = (Object[]) inven;
                        Inventory inv = (Inventory) result[1];
                        plInven.setInventory(inv);

                        p.openInventory(inv);
                    }
                    break;
                case "openCategoryItems":
                    int slot = e.getSlot();
                    Material type = clicked.getType();

                    if (slot == 53 && type == Material.PAPER) {
                        Object invenss = GUIHandler.getInstance().openCategoryItems(p, plInven.getCategory(), plInven.getPages() + 1);
                        Object[] result = (Object[]) invenss;
                        Inventory inv = (Inventory) result[1];
                        plInven.setPages(plInven.getPages() + 1);
                        plInven.setInventory(inv);
                        p.openInventory(inv);
                        return;
                    } else if (slot == 45 && type == Material.PAPER && plInven.getPages() > 0) {
                        Object invenss = GUIHandler.getInstance().openCategoryItems(p, plInven.getCategory(), plInven.getPages() - 1);
                        plInven.setPages(plInven.getPages() - 1);
                        Object[] result = (Object[]) invenss;
                        Inventory inv = (Inventory) result[1];
                        plInven.setInventory(inv);
                        p.openInventory(inv);
                        return;
                    } else if (slot == 49 && type == Material.BARRIER) {
                        invenGUI.setOpenShop(false);
                        p.closeInventory();
                        return;
                    }
                    if (type != Material.PAPER && type != Material.BARRIER && type != Material.BLACK_STAINED_GLASS_PANE) {
                        Inventory invens = GUIHandler.getInstance().openQuantitySelector(p, clicked.clone(), 1);
                        plInven.setInventory(invens);
                        invenGUI.setStage("openQuantitySelector");
                        invenGUI.setTypeItems(clicked.getType().name());
                        invenGUI.setItemsAmount(1);

                        p.openInventory(invens);
                    }
                    break;
            }
        }
    }
}
