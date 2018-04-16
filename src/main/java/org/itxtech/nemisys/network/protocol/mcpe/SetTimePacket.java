package org.itxtech.nemisys.network.protocol.mcpe;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetTimePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_TIME_PACKET;

    public int time;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putVarInt(this.time);
    }

}
