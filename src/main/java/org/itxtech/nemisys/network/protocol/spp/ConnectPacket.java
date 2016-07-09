package org.itxtech.nemisys.network.protocol.spp;

import cn.nukkit.network.protocol.DataPacket;

/**
 * Created by boybook on 16/6/24.
 */
public class ConnectPacket extends SynapseDataPacket {

    public static final byte NETWORK_ID = SynapseInfo.CONNECT_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public int protocol = SynapseInfo.CURRENT_PROTOCOL;
    public int maxPlayers;
    public boolean isMainServer;
    public String description;
    public String encodedPassword;

    @Override
    public void encode(){
        this.reset();
        this.putInt(this.protocol);
        this.putInt(this.maxPlayers);
        this.putByte(this.isMainServer ? (byte)1 : (byte)0);
        this.putString(this.description);
        this.putString(this.encodedPassword);
    }
    
    @Override
    public void decode(){
        this.protocol = this.getInt();
        this.maxPlayers = this.getInt();
        this.isMainServer = this.getByte() == 1;
        this.description = this.getString();
        this.encodedPassword = this.getString();
    }
}
