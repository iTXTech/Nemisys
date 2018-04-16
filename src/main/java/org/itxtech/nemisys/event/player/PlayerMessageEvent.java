package org.itxtech.nemisys.event.player;

import org.itxtech.nemisys.Player;

/**
 * @author CreeperFace
 */
public abstract class PlayerMessageEvent extends PlayerEvent {

    protected String message;

    public PlayerMessageEvent(Player player, String message) {
        super(player);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}