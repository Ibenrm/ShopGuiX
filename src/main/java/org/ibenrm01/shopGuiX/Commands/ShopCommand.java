package org.ibenrm01.shopGuiX.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ibenrm01.shopGuiX.Inventory.GUIHandler;
import org.ibenrm01.shopGuiX.Utility;
import org.ibenrm01.shopGuiX.YAMLConfig.Lang;
import org.ibenrm01.shopGuiX.YAMLConfig.Sell;
import org.ibenrm01.shopGuiX.YAMLConfig.Settings;
import org.ibenrm01.shopGuiX.YAMLConfig.Shop;
import org.ibenrm01.shopGuiX.player.InvenGUI;
import org.ibenrm01.shopGuiX.player.PlayerInventorys;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        String prefix = ChatColor.translateAlternateColorCodes('&', Settings.getInstance().getConfig().getString("serverName"));

        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + " " + Lang.getInstance().getConfig().getString("add.onlyPlayer"));
            return false;
        }

        switch (args.length > 0 ? args[0].toLowerCase() : "") {
            case "add":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("This command can only be run by a player.");
                    return true;
                }

                Player player = (Player) sender;
                if (args.length < 4) {
                    player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                            Lang.getInstance().getConfig().getString("add.usage")));
                    return false;
                }

                String categoryInput = args[1];
                String categoryNormalized = normalizeCategoryName(categoryInput);

                int price;
                int sell;
                try {
                    price = Integer.parseInt(args[2]);
                    sell = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                            Lang.getInstance().getConfig().getString("add.onlynumber")));
                    return false;
                }

                ItemStack item = player.getInventory().getItemInMainHand();
                if (item == null || item.getType() == Material.AIR) {
                    player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                            Lang.getInstance().getConfig().getString("add.noHandItem")));
                    return false;
                }

                String type = item.getType().name().toLowerCase();
                String name;
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    name = meta.getDisplayName();
                } else {
                    name = capitalizeWords(type.replace("_", " "));
                }


                Map<String, Object> shopItemMap = new LinkedHashMap<>();
                shopItemMap.put("type", type);
                shopItemMap.put("name", name);
                shopItemMap.put("price", price);

                Map<String, Object> sellItemMap = new LinkedHashMap<>();
                sellItemMap.put("type", type);
                sellItemMap.put("name", name);
                sellItemMap.put("price", sell);

                // Add item to category in shopguix.yml
                List<Map<?, ?>> mainMenu = Shop.getInstance().getConfig().getMapList("MainMenu");
                boolean found = false;
                for (Map<?, ?> section : mainMenu) {
                    Object rawName = section.get("name");
                    if (rawName instanceof String sectionName) {
                        if (normalizeCategoryName(sectionName).equals(categoryNormalized)) {
                            Object rawIn = section.get("in");
                            List<Map<String, Object>> inList = new ArrayList<>();
                            if (rawIn instanceof List<?>) {
                                for (Object obj : (List<?>) rawIn) {
                                    if (obj instanceof Map<?, ?> rawMap) {
                                        Map<String, Object> cleanMap = new LinkedHashMap<>();
                                        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                                            if (entry.getKey() instanceof String key) {
                                                cleanMap.put(key, entry.getValue());
                                            }
                                        }
                                        inList.add(cleanMap);
                                    }
                                }
                            }
                            inList.add(shopItemMap);
                            Map<String, Object> sectionMap = new LinkedHashMap<>();
                            for (Map.Entry<?, ?> entry : section.entrySet()) {
                                if (entry.getKey() instanceof String key) {
                                    sectionMap.put(key, entry.getValue());
                                }
                            }
                            sectionMap.put("in", inList);
                            int index = mainMenu.indexOf(section);
                            mainMenu.set(index, sectionMap);
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("category", categoryInput);
                    player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                            Utility.getInstance().replace(Lang.getInstance().getConfig().getString("general.category-notfound"), placeholders)));
                    return false;
                }
                Shop.getInstance().getConfig().set("MainMenu", mainMenu);
                Shop.getInstance().save();

                // Sell List config
                List<Map<String, Object>> sellList = new ArrayList<>();
                for (Map<?, ?> rawMap : Sell.getInstance().getConfig().getMapList("mainmenu")) {
                    Map<String, Object> map = new LinkedHashMap<>();
                    for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                        if (entry.getKey() instanceof String key) {
                            map.put(key, entry.getValue());
                        }
                    }
                    sellList.add(map);
                }
                boolean alreadyExists = false;
                for (Map<String, Object> itemMap : sellList) {
                    Object typeObj = itemMap.get("type");
                    if (typeObj != null && typeObj.toString().equalsIgnoreCase(type)) {
                        alreadyExists = true;
                        break;
                    }
                }
                if (!alreadyExists) {
                    sellList.add(sellItemMap);
                }
                Sell.getInstance().getConfig().set("mainmenu", sellList);
                Sell.getInstance().save();
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("category", categoryInput);
                placeholders.put("item", name);
                placeholders.put("price", Utility.getInstance().formatToRupiah(price));

                player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                        Utility.getInstance().replace(Lang.getInstance().getConfig().getString("add.success"), placeholders)));

                break;

            case "set":
                Player pl = (Player) sender;
                if (args.length < 2) {
                    pl.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&', Lang.getInstance().getConfig().getString("set.usage")));
                    return false;
                }
                String categoryName = normalizeCategoryName(args[1]);
                ItemStack items = pl.getInventory().getItemInMainHand();
                if (items == null || items.getType() == Material.AIR) {
                    pl.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&', Lang.getInstance().getConfig().getString("add.noHandItem")));
                    return false;
                }
                String itemType = items.getType().name().toUpperCase();

                List<Map<?, ?>> mainMenuList = Shop.getInstance().getConfig().getMapList("MainMenu");
                boolean updated = false;

                for (int i = 0; i < mainMenuList.size(); i++) {
                    Map<?, ?> section = mainMenuList.get(i);
                    Object rawName = section.get("name");

                    if (rawName instanceof String sectionName) {
                        if (normalizeCategoryName(sectionName).equals(categoryName)) {
                            Map<String, Object> updatedSection = new LinkedHashMap<>();
                            for (Map.Entry<?, ?> entry : section.entrySet()) {
                                if (entry.getKey() instanceof String key) {
                                    updatedSection.put(key, entry.getValue());
                                }
                            }
                            updatedSection.put("items", itemType);
                            mainMenuList.set(i, updatedSection);
                            updated = true;
                            break;
                        }
                    }
                }

                if (updated) {
                    Shop.getInstance().getConfig().set("MainMenu", mainMenuList);
                    Shop.getInstance().save();

                    Map<String, String> placeholderss = new HashMap<>();
                    placeholderss.put("category", GUIHandler.getInstance().formatTitle(args[1]));
                    placeholderss.put("material", GUIHandler.getInstance().formatTitle(itemType));

                    pl.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                            Utility.getInstance().replace(Lang.getInstance().getConfig().getString("set.success"), placeholderss)));
                } else {
                    pl.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                            Utility.getInstance().replace(Lang.getInstance().getConfig().getString("general.category-notfound"),
                                    Collections.singletonMap("category", args[1]))));
                }
                break;
            default:
                Player players = (Player) sender;
                Inventory inven = GUIHandler.getInstance().openMainMenu(players);
                PlayerInventorys.create(players.getUniqueId(), inven, 0, null);
                InvenGUI.create(players.getUniqueId(), true, null, null, "openMainMenu");

                players.openInventory(inven);
                break;
        }
        return true;
    }

    private String normalizeCategoryName(String input) {
        return input.toLowerCase().replace(" ", "_");
    }

    private String capitalizeWords(String input) {
        String[] parts = input.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
