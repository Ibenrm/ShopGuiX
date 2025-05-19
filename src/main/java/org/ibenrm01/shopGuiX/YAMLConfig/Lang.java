package org.ibenrm01.shopGuiX.YAMLConfig;

import org.bukkit.configuration.file.YamlConfiguration;
import org.ibenrm01.shopGuiX.ShopGuiX;

import java.io.File;

public class Lang {

    private final static Lang instance = new Lang();

    private Lang() {
    }

    private File file;
    private YamlConfiguration config;

    public void load(String langauges) {
        file = new File(ShopGuiX.getInstance().getDataFolder(), "lang/" + langauges + ".yml");
        if (!file.exists()) {
            ShopGuiX.getInstance().saveResource("lang/" + langauges + ".yml", false);
        }
        config = new YamlConfiguration();
        config.options().parseComments(true);
        try {
            config.load(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static Lang getInstance() {
        return instance;
    }
}
