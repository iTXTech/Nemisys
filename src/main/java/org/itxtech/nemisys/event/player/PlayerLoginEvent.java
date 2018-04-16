package org.itxtech.nemisys.event.player;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;

public class PlayerLoginEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    protected String kickMessage;
    private String clientHash;

    public PlayerLoginEvent(Player player, String kickMessage, String clientHash) {
        super(player);
        this.kickMessage = kickMessage;
        this.clientHash = clientHash;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }

    public String getClientHash() {
        return clientHash;
    }

    public void setClientHash(String clientHash) {
        this.clientHash = clientHash;
    }

    @Override
    public void setCancelled(boolean value) {
        super.setCancelled(value);
    }
}
