package org.itxtech.nemisys.event.player;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.HandlerList;

public class PlayerLogoutEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    public PlayerLogoutEvent(Player player) {
        super(player);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
