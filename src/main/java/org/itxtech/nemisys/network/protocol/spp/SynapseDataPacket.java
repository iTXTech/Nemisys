package org.itxtech.nemisys.network.protocol.spp;


import org.itxtech.nemisys.network.protocol.mcpe.DataPacket;

/**
 * Created by boybook on 16/6/25.
 */
public abstract class SynapseDataPacket extends DataPacket {

    @Override
    public SynapseDataPacket clone() {
        return (SynapseDataPacket) super.clone();
    }

}
