package org.ibenrm01.shopGuiX.player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InvenGUI {

    private static final Map<UUID, InvenGUI> playerData = new HashMap<>();

    private boolean openShop;
    private int items_amount;
    private String type_items;
    private String stage;

    private InvenGUI() {
    }

    public static InvenGUI get(UUID playerId) {
        return playerData.computeIfAbsent(playerId, k -> new InvenGUI());
    }

    public static InvenGUI create(UUID playerId, boolean openShop, @Nullable Integer items_amount, @Nullable String type_items, String stage) {
        InvenGUI gui = new InvenGUI();
        gui.setOpenShop(openShop);
        if (items_amount != null) {
            gui.setItemsAmount(items_amount);
        }
        if (type_items != null) {
            gui.setTypeItems(type_items);
        }
        gui.setStage(stage);
        playerData.put(playerId, gui);
        return gui;
    }

    public static void remove(UUID playerId) {
        playerData.remove(playerId);
    }

    public boolean getOpenShop() {
        return openShop;
    }

    public void setOpenShop(boolean openShop) {
        this.openShop = openShop;
    }

    public int getItemsAmount() {
        return items_amount;
    }

    public void setItemsAmount(int items_amount) {
        this.items_amount = items_amount;
    }

    public String getTypeItems() {
        return type_items;
    }

    public void setTypeItems(String type_items) {
        this.type_items = type_items;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }
}
