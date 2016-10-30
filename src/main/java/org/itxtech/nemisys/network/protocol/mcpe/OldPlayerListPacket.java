package org.itxtech.nemisys.network.protocol.mcpe;

/**
 * @author Nukkit Project Team
 */
public class OldPlayerListPacket extends BasePlayerListPacket {

    public static final byte NETWORK_ID = ProtocolInfo.OLD_PLAYER_LIST_PACKET;

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
        this.putInt(this.entries.length);
        for (BasePlayerListPacket.Entry entry : this.entries) {
            if (type == TYPE_ADD) {
                this.putUUID(entry.uuid);
                this.putLong(entry.entityId);
                this.putString(entry.name);
                this.putSkin(entry.skin);
            } else {
                this.putUUID(entry.uuid);
            }
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}
