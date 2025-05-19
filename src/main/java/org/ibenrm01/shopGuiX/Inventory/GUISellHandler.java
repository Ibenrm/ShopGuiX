package org.ibenrm01.shopGuiX.Inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.ibenrm01.shopGuiX.YAMLConfig.Settings;

public class GUISellHandler {

    private final static GUISellHandler instance = new GUISellHandler();

    private GUISellHandler() {
    }

    public Inventory sellGUI(Player player) {
        int SIZE = 54;
        Inventory inv = Bukkit.createInventory(null, SIZE, ChatColor.translateAlternateColorCodes('&', Settings.getInstance().getConfig().getString("sellGUI")));

        return inv;
    }

    public static GUISellHandler getInstance() {
        return instance;
    }
}
