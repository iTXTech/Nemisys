package org.itxtech.nemisys.network.protocol.spp;

/**
 * Created by boybook on 16/6/24.
 */
public class ConnectPacket extends SynapseDataPacket {

    public static final byte NETWORK_ID = SynapseInfo.CONNECT_PACKET;

    public int protocol = SynapseInfo.CURRENT_PROTOCOL;
    public int maxPlayers;
    public boolean isMainServer;
    public boolean isLobbyServer;
    public boolean transferShutdown;
    public String description;
    public String password;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(this.protocol);
        this.putInt(this.maxPlayers);
        this.putBoolean(this.isMainServer);
        this.putBoolean(this.isLobbyServer);
        this.putBoolean(transferShutdown);
        this.putString(this.description);
        this.putString(this.password);
    }

    @Override
    public void decode() {
        this.protocol = this.getInt();
        this.maxPlayers = this.getInt();
        this.isMainServer = this.getBoolean();
        this.isLobbyServer = this.getBoolean();
        this.transferShutdown = getBoolean();
        this.description = this.getString();
        this.password = this.getString();
    }
}
