package org.itxtech.nemisys.network.protocol.mcpe;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AddItemEntityPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.ADD_ITEM_ENTITY_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long entityUniqueId;
    public long entityRuntimeId;
    public float x;
    public float y;
    public float z;
    public float speedX;
    public float speedY;
    public float speedZ;

    @Override
    public void decode() {
        entityUniqueId = getEntityUniqueId();
        entityRuntimeId = getEntityRuntimeId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
    }
}
