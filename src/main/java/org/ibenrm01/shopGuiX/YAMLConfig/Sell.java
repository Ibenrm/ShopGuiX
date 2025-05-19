package org.ibenrm01.shopGuiX.YAMLConfig;

import org.bukkit.configuration.file.YamlConfiguration;
import org.ibenrm01.shopGuiX.ShopGuiX;

import java.io.File;

public class Sell {

    private final static Sell instance = new Sell();

    private Sell() {
    }

    private File file;
    private YamlConfiguration config;

    public void load() {
        file = new File(ShopGuiX.getInstance().getDataFolder(), "shop/sellshop.yml");
        if (!file.exists()) {
            ShopGuiX.getInstance().saveResource("shop/sellshop.yml", false);
        }
        config = new YamlConfiguration();
        config.options().parseComments(true);
        try {
            config.load(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public YamlConfiguration getConfig() {
        return config;
    }

    public static Sell getInstance() {
        return instance;
    }
}
