package org.itxtech.nemisys.event.player;

import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;

public class PlayerTransferEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Client targetClient;
    private boolean needDisconnect;

    public PlayerTransferEvent(Player player, Client targetClient, boolean needDisconnect) {
        super(player);
        this.targetClient = targetClient;
        this.needDisconnect = needDisconnect;
    }

    public boolean isNeedDisconnect() {
        return needDisconnect;
    }

    public Client getTargetClient() {
        return targetClient;
    }

    public void setTargetClient(Client targetClient) {
        this.targetClient = targetClient;
    }

}
