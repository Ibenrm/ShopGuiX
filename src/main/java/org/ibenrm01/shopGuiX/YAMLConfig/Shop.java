package org.ibenrm01.shopGuiX.YAMLConfig;

import org.bukkit.configuration.file.YamlConfiguration;
import org.ibenrm01.shopGuiX.ShopGuiX;

import java.io.File;

public class Shop {

    private final static Shop instance = new Shop();

    private Shop() {
    }

    private File file;
    private YamlConfiguration config;

    public void load() {
        file = new File(ShopGuiX.getInstance().getDataFolder(), "shop/shopguix.yml");
        if (!file.exists()) {
            ShopGuiX.getInstance().saveResource("shop/shopguix.yml", false);
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

    public static Shop getInstance() {
        return instance;
    }
}
