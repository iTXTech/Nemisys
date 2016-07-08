package org.itxtech.nemisys.event.player;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;

public class PlayerGameModeChangeEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    protected int gamemode;

    public PlayerGameModeChangeEvent(Player player, int newGameMode) {
        this.player = player;
        this.gamemode = newGameMode;
    }

    public int getNewGamemode() {
        return gamemode;
    }
}
