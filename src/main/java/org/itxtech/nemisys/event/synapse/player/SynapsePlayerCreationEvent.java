package org.itxtech.nemisys.event.synapse.player;

import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.event.synapse.SynapseEvent;
import org.itxtech.nemisys.network.SourceInterface;
import org.itxtech.nemisys.synapse.SynapsePlayer;

/**
 */
public class SynapsePlayerCreationEvent extends SynapseEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private SourceInterface interfaz;

    private Long clientId;

    private String address;

    private int port;

    private Class<? extends SynapsePlayer> baseClass;

    private Class<? extends SynapsePlayer> playerClass;

    public SynapsePlayerCreationEvent(SourceInterface interfaz, Class<? extends SynapsePlayer> baseClass, Class<? extends SynapsePlayer> playerClass, Long clientId, String address, int port) {
        this.interfaz = interfaz;
        this.clientId = clientId;
        this.address = address;
        this.port = port;

        this.baseClass = baseClass;
        this.playerClass = playerClass;
    }

    public SourceInterface getInterface() {
        return interfaz;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public Long getClientId() {
        return clientId;
    }

    public Class<? extends SynapsePlayer> getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(Class<? extends SynapsePlayer> baseClass) {
        this.baseClass = baseClass;
    }

    public Class<? extends SynapsePlayer> getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(Class<? extends SynapsePlayer> playerClass) {
        this.playerClass = playerClass;
    }
}
