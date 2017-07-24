package org.itxtech.nemisys.network.protocol.spp;

import java.util.UUID;

/**
 * Created by boybook on 16/6/24.
 */
public class TransferPacket extends SynapseDataPacket {

    public static final byte NETWORK_ID = SynapseInfo.TRANSFER_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public UUID uuid;
    public String clientHash;
    public byte[] afterLoginPacket;

    @Override
    public void encode(){
        this.reset();
        this.putUUID(this.uuid);
        this.putString(this.clientHash);
        this.put(this.afterLoginPacket);
    }
    
    @Override
    public void decode(){
        this.uuid = this.getUUID();
        this.clientHash = this.getString();
        this.afterLoginPacket = this.get();
    }
}
