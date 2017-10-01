package org.itxtech.nemisys.raknet.protocol.packet;

import org.itxtech.nemisys.raknet.RakNet;
import org.itxtech.nemisys.raknet.protocol.Packet;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class UNCONNECTED_PING extends Packet {
    public static byte ID = (byte) 0x01;

    @Override
    public byte getID() {
        return ID;
    }

    public long pingID;
    public byte[] magic;

    @Override
    public void encode() {
        super.encode();
        this.putLong(this.pingID);
        this.put(RakNet.MAGIC);
    }

    @Override
    public void decode() {
        super.decode();
        this.pingID = this.getLong();
        this.magic = this.get();
    }

    public static final class Factory implements Packet.PacketFactory {

        @Override
        public Packet create() {
            return new UNCONNECTED_PING();
        }

    }
}
