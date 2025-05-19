package org.ibenrm01.shopGuiX;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.ibenrm01.ecoSyX.api.EcoSystem;
import org.ibenrm01.shopGuiX.Commands.SellComand;
import org.ibenrm01.shopGuiX.Commands.ShopCommand;
import org.ibenrm01.shopGuiX.Listener.CategoryEvent;
import org.ibenrm01.shopGuiX.Listener.InventoryEvents;
import org.ibenrm01.shopGuiX.Listener.ItemsEvent;
import org.ibenrm01.shopGuiX.Listener.sell.InventoryEventSell;
import org.ibenrm01.shopGuiX.YAMLConfig.Lang;
import org.ibenrm01.shopGuiX.YAMLConfig.Sell;
import org.ibenrm01.shopGuiX.YAMLConfig.Settings;
import org.ibenrm01.shopGuiX.YAMLConfig.Shop;

public final class ShopGuiX extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Settings.getInstance().load();
        Lang.getInstance().load(Settings.getInstance().getConfig().getString("lang"));
        Sell.getInstance().load();
        Shop.getInstance().load();

        getServer().getPluginManager().registerEvents(new InventoryEvents(), this);
        getServer().getPluginManager().registerEvents(new CategoryEvent(), this);
        getServer().getPluginManager().registerEvents(new ItemsEvent(), this);
        getServer().getPluginManager().registerEvents(new InventoryEventSell(), this);

        getLogger().info("ShopGuiX Enabled");

        getCommand("shopgui").setExecutor(new ShopCommand());
        getCommand("sellgui").setExecutor(new SellComand());
    }

    @Override
    public void onDisable() {
        getLogger().info("ShopGuiX has been disabled");
        Settings.getInstance().save();
        Shop.getInstance().save();
        Lang.getInstance().save();
        Sell.getInstance().save();
    }

    public EcoSystem getEcoSystem() {
        return EcoSystem.getInstance();
    }

    public static ShopGuiX getInstance() {
        return getPlugin(ShopGuiX.class);
    }
}
