package org.ibenrm01.shopGuiX;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ibenrm01.shopGuiX.YAMLConfig.Shop;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Utility {

    private final static Utility instance = new Utility();

    private Utility() {
    }

    public static String replace(String message, Map<String, String> keys) {
        for (Map.Entry<String, String> entry : keys.entrySet()) {
            message = message.replace("{" + entry.getKey().toLowerCase() + "}", entry.getValue());
        }
        return message;
    }

    public static String[] payment(String UID, int nominal) {
        String[] result = ShopGuiX.getInstance().getEcoSystem().myMoney(UID);
        if (!result[0].equals("success")) {
            return new String[]{"notfound"};
        }
        int mymoney;
        try {
            mymoney = Integer.parseInt(result[1]);
        } catch (NumberFormatException e) {
            return new String[]{"must-int"};
        }

        if (mymoney < nominal) {
            return new String[]{"notenough"};
        }
        String[] setting = ShopGuiX.getInstance().getEcoSystem().setMoney(UID, mymoney - nominal);
        if (!setting[0].equals("success")) {
            return new String[]{"notfound"};
        }
        return new String[]{"success", String.valueOf(nominal)};
    }

    public static String[] sellPayment(String UID, int nominal) {
        String[] result = ShopGuiX.getInstance().getEcoSystem().myMoney(UID);
        if (!result[0].equals("success")) {
            return new String[]{"notfound"};
        }
        int mymoney;
        try {
            mymoney = Integer.parseInt(result[1]);
        } catch (NumberFormatException e) {
            return new String[]{"must-int"};
        }
        
        String[] setting = ShopGuiX.getInstance().getEcoSystem().setMoney(UID, mymoney + nominal);
        if (!setting[0].equals("success")) {
            return new String[]{"notfound"};
        }
        return new String[]{"success", String.valueOf(nominal)};
    }

    public int findItemPrice(String category, String itemType) {
        if (category == null || itemType == null) {
            return 0;
        }

        String categoryLower = category.toLowerCase();
        String itemTypeLower = itemType.toLowerCase();

        List<Map<?, ?>> mainMenu = Shop.getInstance().getConfig().getMapList("MainMenu");
        for (Map<?, ?> cat : mainMenu) {
            String catName = ((String) cat.get("name")).toLowerCase();
            if (!catName.equals(categoryLower)) continue;

            List<Map<?, ?>> items = (List<Map<?, ?>>) cat.get("in");
            if (items == null) continue;

            for (Map<?, ?> item : items) {
                String type = ((String) item.get("type")).toLowerCase();
                if (type.equals(itemTypeLower)) {
                    Object priceObj = item.get("price");
                    if (priceObj instanceof Number) {
                        return ((Number) priceObj).intValue();
                    }
                }
            }
        }
        return 0;
    }

    public boolean hasAvaliableSlot(Player player) {
        Inventory inv = player.getInventory();
        for (ItemStack item : inv.getContents()) {
            if (item == null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAvailableSlots(Player player, int requiredSlots) {
        int emptyCount = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                emptyCount++;
                if (emptyCount >= requiredSlots) {
                    return true;
                }
            }
        }

        return false;
    }

    public String setColor(String chat) {
        return ChatColor.translateAlternateColorCodes('&', chat);
    }


    public String formatToRupiah(Integer amount) {
        NumberFormat format = NumberFormat.getInstance(new Locale("id", "ID"));
        return format.format(amount);
    }

    public static Utility getInstance() {
        return instance;
    }

}
