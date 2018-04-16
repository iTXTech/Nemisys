package org.itxtech.nemisys.network.protocol.mcpe;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class RemoveEntityPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.REMOVE_ENTITY_PACKET;

    public long eid;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.eid = getEntityUniqueId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.eid);
    }
}
