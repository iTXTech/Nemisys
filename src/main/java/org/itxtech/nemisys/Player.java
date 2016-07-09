package org.itxtech.nemisys;

import org.itxtech.nemisys.network.SourceInterface;
import org.itxtech.nemisys.network.protocol.spp.PlayerLoginPacket;

import java.util.UUID;

/**
 * Author: PeratX
 * Nemisys Project
 */
public class Player {
    private PlayerLoginPacket cachedLoginPacket = null;
    private String name;
    private String ip;
    private int port;
    private long clientId;
    private long randomClientId;
    private int protocol;
    private UUID uuid;
    private SourceInterface $interface;
    private Client client;
    private Server server;
    private byte[] rawUUID;
    private boolean isFirstTimeLogin = true;
    private long lastUpdate;
    private boolean closed;

    public Player(SourceInterface interfaz, ){

    }
}
