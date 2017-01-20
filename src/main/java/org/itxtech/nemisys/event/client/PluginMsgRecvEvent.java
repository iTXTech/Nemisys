package org.itxtech.nemisys.event.client;

import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.event.HandlerList;

/**
 * @author PeratX
 */
public class PluginMsgRecvEvent extends ClientEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private String message;

    public PluginMsgRecvEvent(Client client, String message) {
        super(client);
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
