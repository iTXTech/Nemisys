package org.itxtech.nemisys.event.client;

import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.event.HandlerList;
import org.omg.CORBA.StringHolder;

/**
 * Created by boybook on 16/7/11.
 */
public class ClientDisconnectEvent extends ClientEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private String reason;
    private int type;

    public ClientDisconnectEvent(Client client, String reason, int type) {
        super(client);
        this.reason = reason;
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public int getType() {
        return type;
    }

}
