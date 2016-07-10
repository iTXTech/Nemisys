package org.itxtech.nemisys.event.client;

import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.Event;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ClientEvent extends Event {
    protected Client client;

    public ClientEvent(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }
}
