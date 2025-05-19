package org.ibenrm01.shopGuiX.player;

import org.bukkit.inventory.Inventory;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInventorys {

    private static final Map<UUID, PlayerInventorys> playerData = new HashMap<>();

    public static PlayerInventorys get(UUID playerId) {
        return playerData.computeIfAbsent(playerId, id -> new PlayerInventorys());
    }
    

    public static PlayerInventorys create(UUID playerId, Inventory inventory, int pages, @Nullable String category) {
        PlayerInventorys p = new PlayerInventorys();
        p.setInventory(inventory);
        p.setPages(pages);
        if (category != null) {
            p.setCategory(category);
        }
        playerData.put(playerId, p);
        return p;
    }


    public static void remove(UUID playerId) {
        playerData.remove(playerId);
    }

    private Inventory inventory;
    private int pages;
    private String category;

    private PlayerInventorys() {
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
