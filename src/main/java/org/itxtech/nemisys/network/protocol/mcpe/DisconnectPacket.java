package org.itxtech.nemisys.network.protocol.mcpe;

/**
 * Created by on 15-10-12.
 */
public class DisconnectPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.DISCONNECT_PACKET;

    public boolean hideDisconnectionScreen = false;
    public String message;
    public boolean isOld = false;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        if (!this.isOld) this.hideDisconnectionScreen = this.getBoolean();
        this.message = this.getString();
    }

    @Override
    public void encode() {
        this.reset();
        if (!this.isOld) this.putBoolean(this.hideDisconnectionScreen);
        this.putString(this.message);
    }


}
