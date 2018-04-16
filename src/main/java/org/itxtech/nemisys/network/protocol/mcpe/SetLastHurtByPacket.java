package org.itxtech.nemisys.network.protocol.mcpe;

public class SetLastHurtByPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.SET_LAST_HURT_BY_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        //TODO
    }
}
