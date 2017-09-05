package org.itxtech.nemisys.event.client;

import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.event.HandlerList;

/**
 * Created by boybook on 16/7/11.
 */
public class ClientConnectEvent extends ClientEvent {

    private static final HandlerList handlers = new HandlerList();

    public ClientConnectEvent(Client client) {
        super(client);
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

}
