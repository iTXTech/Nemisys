package org.itxtech.nemisys.network.protocol.spp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Author: PeratX
 * Nemisys Project
 */
public class BroadcastPacket extends SynapseDataPacket{
    public static final byte NETWORK_ID = SynapseInfo.BROADCAST_PACKET;

    public List<UUID> entries;
    public boolean direct;
    public byte[] payload;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        this.putBoolean(this.direct);
        this.putShort(this.entries.size());
        for(UUID uniqueId : this.entries){
            this.putUUID(uniqueId);
        }
        this.putShort(this.payload.length);
        this.put(this.payload);
    }

    @Override
    public void decode() {
        this.direct = this.getBoolean();
        int len = this.getShort();
        this.entries = new ArrayList<>();
        for(int i = 0; i < len; i++){
            this.entries.add(this.getUUID());
        }
        this.payload = this.get(this.getShort());
    }
}
