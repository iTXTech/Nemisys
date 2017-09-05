package org.itxtech.nemisys.event.client;

import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.event.HandlerList;

/**
 * Created by boybook on 16/7/11.
 */
public class ClientAuthEvent extends ClientEvent {

    private static final HandlerList handlers = new HandlerList();
    private String password;

    public ClientAuthEvent(Client client, String password) {
        super(client);
        this.password = password;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public String getPassword() {
        return password;
    }
}
