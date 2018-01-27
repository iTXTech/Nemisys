package org.itxtech.nemisys.network.protocol.spp;

import java.util.UUID;

/**
 * Created by boybook on 16/6/24.
 */
public class PlayerLoginPacket extends SynapseDataPacket {

    public static final byte NETWORK_ID = SynapseInfo.PLAYER_LOGIN_PACKET;
    public UUID uuid;
    public String address;
    public int port;
    public boolean isFirstTime;
    public byte[] cachedLoginPacket;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        this.reset();
        this.putUUID(this.uuid);
        this.putString(this.address);
        this.putInt(this.port);
        this.putByte(this.isFirstTime ? (byte) 1 : (byte) 0);
        this.putInt(this.cachedLoginPacket.length);
        this.put(this.cachedLoginPacket);
    }

    @Override
    public void decode() {
        this.uuid = this.getUUID();
        this.address = this.getString();
        this.port = this.getInt();
        this.isFirstTime = this.getByte() == 1;
        this.cachedLoginPacket = this.get(this.getInt());
    }
}
