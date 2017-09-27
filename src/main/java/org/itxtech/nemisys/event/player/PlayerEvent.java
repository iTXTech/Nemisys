package org.itxtech.nemisys.event.player;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.Event;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class PlayerEvent extends Event {
    protected Player player;

    public PlayerEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
