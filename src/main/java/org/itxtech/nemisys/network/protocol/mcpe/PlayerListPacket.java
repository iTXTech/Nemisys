package org.itxtech.nemisys.network.protocol.mcpe;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
public class PlayerListPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_LIST_PACKET;

    public static final byte TYPE_REMOVE = 1;

    public byte type;
    public Entry[] entries = new Entry[0];

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.type);
        this.putInt(this.entries.length);
        for (Entry entry : this.entries) {
            this.putUUID(entry.uuid);
        }

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public static class Entry {

        public UUID uuid;
        public long entityId = 0;
        public String name = "";

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }
    }

}
