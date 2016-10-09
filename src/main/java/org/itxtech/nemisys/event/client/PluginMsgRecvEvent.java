package org.itxtech.nemisys.event.client;

import org.itxtech.nemisys.Client;

/**
 * @author PeratX
 */
public class PluginMsgRecvEvent extends ClientEvent {
    private String message;

    public PluginMsgRecvEvent(Client client, String message) {
        super(client);
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
