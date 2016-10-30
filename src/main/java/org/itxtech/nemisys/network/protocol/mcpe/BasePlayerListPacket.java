package org.itxtech.nemisys.network.protocol.mcpe;

import org.itxtech.nemisys.utils.Skin;

import java.util.UUID;

/**
 * @author Nukkit Project Team
 */
public abstract class BasePlayerListPacket extends DataPacket {

    public static final byte TYPE_ADD = 0;
    public static final byte TYPE_REMOVE = 1;

    public byte type;
    public Entry[] entries = new Entry[0];

    @Override
    public void decode() {

    }

    public static class Entry {
        public final UUID uuid;
        public long entityId = 0;
        public String name = "";
        public Skin skin;

        public Entry(UUID uuid) {
            this.uuid = uuid;
        }

        public Entry(UUID uuid, long entityId, String name, Skin skin) {
            this.uuid = uuid;
            this.entityId = entityId;
            this.name = name;
            this.skin = skin;
        }
    }

}
