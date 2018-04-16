package org.itxtech.nemisys.network.protocol.mcpe;

public class ServerSettingsRequestPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.SERVER_SETTINGS_REQUEST_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {

    }
}
