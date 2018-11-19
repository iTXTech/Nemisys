package org.itxtech.nemisys.network.protocol.mcpe;

/**
 * @author CreeperFace
 */
public class RemoveObjectivePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REMOVE_OBJECTIVE_PACKET;

    public String objective;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        reset();
        putString(objective);
    }

    @Override
    public void decode() {
        objective = getString();
    }
}
