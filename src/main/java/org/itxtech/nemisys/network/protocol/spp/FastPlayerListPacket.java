package org.itxtech.nemisys.network.protocol.spp;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
public class FastPlayerListPacket extends SynapseDataPacket {

    public static final byte NETWORK_ID = SynapseInfo.FAST_PLAYER_LIST_PACKET;

    public static final byte TYPE_ADD = 0;
    public static final byte TYPE_REMOVE = 1;

    public UUID sendTo;
    public byte type;
    public FastPlayerListPacket.Entry[] entries = new FastPlayerListPacket.Entry[0];

    @Override
    public void decode() {
        this.sendTo = this.getUUID();
        this.type = (byte)this.getByte();
        int len = this.getInt();
        this.entries = new FastPlayerListPacket.Entry[len];
        for (int i = 0; i < len; i++) {
            if (this.type == TYPE_ADD) {
                this.entries[i] = new Entry(this.getUUID(), this.getLong(), this.getString());
            } else {
                this.entries[i] = new Entry(this.getUUID());
            }
        }
    }

    @Override
    public void encode() {
        this.reset();
        this.putUUID(this.sendTo);
        this.putByte(this.type);
        this.putInt(this.entries.length);
        for (FastPlayerListPacket.Entry entry : this.entries) {
            if (type == TYPE_ADD) {
                this.putUUID(entry.uuid);
                this.putLong(entry.entityId);
                this.putString(entry.name);
            } else {
                this.putUUID(entry.uuid);
            }
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public static class Entry {

        public final UUID uuid;
        public long entityId = 0;
        public String name = "";

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityId, String name) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
        }
    }

}
