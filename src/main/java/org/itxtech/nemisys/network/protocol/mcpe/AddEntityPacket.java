package org.itxtech.nemisys.network.protocol.mcpe;

import org.itxtech.nemisys.math.Vector3f;
import org.itxtech.nemisys.network.protocol.mcpe.types.entity.Attribute;
import org.itxtech.nemisys.network.protocol.mcpe.types.entity.EntityLink;
import org.itxtech.nemisys.network.protocol.mcpe.types.entity.metadata.EntityMetadata;
import org.itxtech.nemisys.utils.Binary;

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
    public String type;
    public float x;
    public float y;
    public float z;
    public float speedX = 0f;
    public float speedY = 0f;
    public float speedZ = 0f;
    public float yaw;
    public float pitch;
    public float headYaw;
    public EntityMetadata metadata = new EntityMetadata();
    public Attribute[] attributes = new Attribute[0];
    public EntityLink[] links = new EntityLink[0];

    @Override
    public void decode() {
        entityUniqueId = getEntityUniqueId();
        entityRuntimeId = getEntityRuntimeId();
        type = getString();

        Vector3f pos = getVector3f();
        x = pos.x;
        y = pos.y;
        z = pos.z;

        pos = getVector3f();
        speedX = pos.x;
        speedY = pos.y;
        speedZ = pos.z;

        pitch = getLFloat();
        yaw = getLFloat();
        headYaw = getLFloat();

        attributes = getAttributeList();
        metadata = Binary.readMetadata(this);

        int linkCount = (int) getUnsignedVarInt();
        links = new EntityLink[linkCount];

        for (int i = 0; i < linkCount; i++) {
            links[i] = getEntityLink();
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(this.entityUniqueId);
        this.putEntityRuntimeId(this.entityRuntimeId);
        this.putString(this.type);
        this.putVector3f(this.x, this.y, this.z);
        this.putVector3f(this.speedX, this.speedY, this.speedZ);
        this.putLFloat(this.pitch);
        this.putLFloat(this.yaw);
        this.putFloat(this.headYaw);
        this.putAttributeList(this.attributes);
        this.put(Binary.writeMetadata(this.metadata));

        this.putUnsignedVarInt(this.links.length);

        for (EntityLink link : this.links) {
            putEntityLink(link);
        }
    }
}
