package org.itxtech.nemisys.network.protocol.spp;

/**
 * @author CreeperFace
 */
public class PluginMessagePacket extends SynapseDataPacket {

    public static final int NETWORK_ID = SynapseInfo.PLUGIN_MESSAGE_PACKET;

    public String channel;
    public byte[] data;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.channel);
        this.putByteArray(this.data);
    }

    @Override
    public void decode() {
        this.channel = this.getString();
        this.data = this.getByteArray();
    }
}
