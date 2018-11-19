package org.itxtech.nemisys.network.protocol.mcpe;

/**
 * @author CreeperFace
 */
public class SetDisplayObjectivePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_DISPLAY_OBJECTIVE_PACKET;

    public String objective;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {

    }

    @Override
    public void decode() {
        getString(); // display slot
        objective = getString();
    }
}
