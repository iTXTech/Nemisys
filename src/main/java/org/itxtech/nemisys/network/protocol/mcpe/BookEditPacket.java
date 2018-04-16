package org.itxtech.nemisys.network.protocol.mcpe;

public class BookEditPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.BOOK_EDIT_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        //TODO
    }
}
