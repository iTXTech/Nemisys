package org.itxtech.nemisys.network.protocol.spp;

/**
 * Created by boybook on 16/6/24.
 */
public class InformationPacket extends SynapseDataPacket {

    public static final byte NETWORK_ID = SynapseInfo.INFORMATION_PACKET;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public static final byte TYPE_LOGIN = 0;
    public static final byte TYPE_CLIENT_DATA = 1;
    public static final byte TYPE_PLUGIN_MESSAGE = 2;

    public static final String INFO_LOGIN_SUCCESS = "success";
    public static final String INFO_LOGIN_FAILED = "failed";

    public byte type;
    public String message;

    @Override
    public void encode(){
        this.reset();
        this.putByte(this.type);
        this.putString(this.message);
    }
    
    @Override
    public void decode(){
        this.type = (byte)this.getByte();
        this.message = this.getString();
    }
}
