package org.ibenrm01.shopGuiX.player.sell;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSell {

    private static final Map<UUID, PlayerSell> playerData = new HashMap<>();

    public static PlayerSell get(UUID playerId) {
        return playerData.get(playerId);
    }

    public static PlayerSell create(UUID playerId, boolean activeGUI) {
        PlayerSell p = new PlayerSell();
        playerData.put(playerId, p);
        p.setActiveGUI(activeGUI);
        return p;
    }

    public static void remove(UUID playerId) {
        playerData.remove(playerId);
    }

    private boolean activeGUI;

    public boolean getActiveGUI() {
        return activeGUI;
    }

    public void setActiveGUI(boolean activeGUI) {
        this.activeGUI = activeGUI;
    }
}
