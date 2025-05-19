package org.ibenrm01.shopGuiX.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.ibenrm01.shopGuiX.Inventory.GUIHandler;
import org.ibenrm01.shopGuiX.Inventory.GUISellHandler;
import org.ibenrm01.shopGuiX.Utility;
import org.ibenrm01.shopGuiX.YAMLConfig.Lang;
import org.ibenrm01.shopGuiX.YAMLConfig.Sell;
import org.ibenrm01.shopGuiX.YAMLConfig.Settings;
import org.ibenrm01.shopGuiX.player.sell.PlayerSell;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellComand implements CommandExecutor {

    private Settings settings = Settings.getInstance();
    private String prefix = ChatColor.translateAlternateColorCodes('&', settings.getInstance().getConfig().getString("serverName"));
    private Lang lang = Lang.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(prefix + " " + Lang.getInstance().getConfig().getString("add.onlyPlayer"));
            return false;
        }

        Player player = (Player) sender;

        switch (args.length > 0 ? args[0].toLowerCase() : "") {
            case "hand":
                ItemStack hand = player.getInventory().getItemInMainHand();
                if (hand == null || hand.getType() == Material.AIR) {
                    player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                            Lang.getInstance().getConfig().getString("add.noHandItem")));
                    return false;
                }
                int amount = hand.getAmount();
                if (amount <= 0) {
                    player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                            Lang.getInstance().getConfig().getString("add.noHandItem")));
                    return false;
                }
                YamlConfiguration sellConfig = Sell.getInstance().getConfig();
                String itemType = hand.getType().name();
                List<Map<?, ?>> items = sellConfig.getMapList("mainmenu");
                Map<?, ?> matchedItem = null;
                for (Map<?, ?> item : items) {
                    if (itemType.equalsIgnoreCase(String.valueOf(item.get("type")))) {
                        matchedItem = item;
                        break;
                    }
                }
                if (matchedItem == null) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("item", GUIHandler.getInstance().formatTitle(itemType));
                    player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                            Utility.getInstance().replace(lang.getConfig().getString("sell.no-sell"), placeholders)));
                    return false;
                }
                int pricePerItem = Integer.parseInt(matchedItem.get("price").toString());
                int totalPrice = pricePerItem * amount;
                player.getInventory().setItemInMainHand(null);

                String[] statue = Utility.getInstance().sellPayment(player.getUniqueId().toString(), totalPrice);

                if (!statue[0].equals("success")) {
                    player.sendMessage(prefix + " " + Utility.getInstance().setColor(lang.getConfig().getString("add.onlyPlayer")));
                    return false;
                }
                Map<String, String> placeholderss = new HashMap<>();
                placeholderss.put("item", GUIHandler.getInstance().formatTitle(itemType));
                placeholderss.put("price", String.valueOf(totalPrice));
                placeholderss.put("amount", String.valueOf(amount));
                player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&', Utility.getInstance().replace(lang.getConfig().getString("sell.success"), placeholderss)));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                break;
            case "all":
                ItemStack handItem = player.getInventory().getItemInMainHand();
                if (handItem == null || handItem.getType() == Material.AIR) {
                    player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                            Lang.getInstance().getConfig().getString("add.noHandItem")));
                    return false;
                }

                Material sellType = handItem.getType();
                YamlConfiguration sellConfigs = Sell.getInstance().getConfig();
                String itemTypes = sellType.name();
                List<Map<?, ?>> itemss = sellConfigs.getMapList("mainmenu");

                Map<?, ?> matchedItems = null;
                for (Map<?, ?> item : itemss) {
                    if (itemTypes.equalsIgnoreCase(String.valueOf(item.get("type")))) {
                        matchedItems = item;
                        break;
                    }
                }

                if (matchedItems == null) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("item", itemTypes);
                    player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                            Utility.getInstance().replace(lang.getConfig().getString("sell.no-sell"), placeholders)));
                    return false;
                }

                int pricePerItems = Integer.parseInt(matchedItems.get("price").toString());
                int totalAmount = 0;
                PlayerInventory inventory = player.getInventory();
                for (ItemStack item : inventory.getContents()) {
                    if (item != null && item.getType() == sellType) {
                        totalAmount += item.getAmount();
                    }
                }

                if (totalAmount == 0) {
                    player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&',
                            Lang.getInstance().getConfig().getString("add.noHandItem")));
                    return false;
                }

                int amountToRemove = totalAmount;
                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack item = inventory.getItem(i);
                    if (item != null && item.getType() == sellType) {
                        int itemAmount = item.getAmount();
                        if (itemAmount <= amountToRemove) {
                            inventory.setItem(i, null);
                            amountToRemove -= itemAmount;
                        } else {
                            item.setAmount(itemAmount - amountToRemove);
                            inventory.setItem(i, item);
                            break;
                        }
                    }
                }

                int totalPrices = pricePerItems * totalAmount;

                String[] statues = Utility.getInstance().sellPayment(player.getUniqueId().toString(), totalPrices);

                if (!statues[0].equals("success")) {
                    player.sendMessage(prefix + " " + Utility.getInstance().setColor(lang.getConfig().getString("add.onlyPlayer")));
                    return false;
                }

                Map<String, String> placeholdersss = new HashMap<>();
                placeholdersss.put("item", GUIHandler.getInstance().formatTitle(itemTypes));
                placeholdersss.put("price", String.valueOf(totalPrices));
                placeholdersss.put("amount", String.valueOf(totalAmount));
                player.sendMessage(prefix + " " + ChatColor.translateAlternateColorCodes('&', Utility.getInstance().replace(lang.getConfig().getString("sell.success"), placeholdersss)));
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                break;
            default:
                Inventory inv = GUISellHandler.getInstance().sellGUI(player);
                PlayerSell.create(player.getUniqueId(), true);
                player.openInventory(inv);
                break;
        }
        return true;
    }
}
