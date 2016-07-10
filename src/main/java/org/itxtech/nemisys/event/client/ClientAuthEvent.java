package org.itxtech.nemisys.event.client;

import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.event.HandlerList;

/**
 * Created by boybook on 16/7/11.
 */
public class ClientAuthEvent extends ClientEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private String password;

    public ClientAuthEvent(Client client, String password) {
        super(client);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
