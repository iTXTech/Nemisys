package org.itxtech.nemisys.network.protocol.spp;

/**
 * Created by boybook on 16/6/24.
 */
public class ConnectPacket extends SynapseDataPacket {

    public static final byte NETWORK_ID = SynapseInfo.CONNECT_PACKET;
    public int protocol = SynapseInfo.CURRENT_PROTOCOL;
    public int maxPlayers;
    public boolean isMainServer;
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
        this.putByte(this.isMainServer ? (byte) 1 : (byte) 0);
        this.putString(this.description);
        this.putString(this.password);
    }

    @Override
    public void decode() {
        this.protocol = this.getInt();
        this.maxPlayers = this.getInt();
        this.isMainServer = this.getByte() == 1;
        this.description = this.getString();
        this.password = this.getString();
    }
}
