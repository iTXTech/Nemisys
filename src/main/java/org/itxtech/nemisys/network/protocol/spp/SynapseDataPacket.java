package org.itxtech.nemisys.network.protocol.spp;

import cn.nukkit.network.protocol.DataPacket;

/**
 * Created by boybook on 16/6/25.
 */
public abstract class SynapseDataPacket extends DataPacket {

    @Override
    public SynapseDataPacket clone() {
        return (SynapseDataPacket) super.clone();
    }

}
