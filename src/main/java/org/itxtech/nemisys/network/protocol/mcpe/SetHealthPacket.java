package org.itxtech.nemisys.network.protocol.mcpe;

import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;

public class SetHealthPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_HEALTH_PACKET;

    public int health;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.health = this.getInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(this.health);
    }

}
