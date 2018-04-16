package org.itxtech.nemisys.network.protocol.mcpe;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AddEntityPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.ADD_ENTITY_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public long entityUniqueId;
    public long entityRuntimeId;
    public int type;
    public float x;
    public float y;
    public float z;
    public float speedX = 0f;
    public float speedY = 0f;
    public float speedZ = 0f;
    public float yaw;
    public float pitch;

    @Override
    public void decode() {
        entityUniqueId = getEntityUniqueId();
        entityRuntimeId = getEntityRuntimeId();
        type = (int) getUnsignedVarInt();
        /*Vector3f pos = getVector3f();
        x = pos.x;
        y = pos.y;
        z = pos.z;

        pos = getVector3f();
        speedX = pos.x;
        speedY = pos.y;
        speedZ = pos.z;
        pitch = getLFloat();
        yaw = getLFloat();*/
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putUnsignedVarInt(this.type);
        this.putVector3f(this.x, this.y, this.z);
        this.putVector3f(this.speedX, this.speedY, this.speedZ);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw);
    }
}
