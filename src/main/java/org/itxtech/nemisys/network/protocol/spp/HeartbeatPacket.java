package org.itxtech.nemisys.network.protocol.spp;

/**
 * Created by boybook on 16/6/24.
 */
public class HeartbeatPacket extends SynapseDataPacket {

    public static final byte NETWORK_ID = SynapseInfo.HEARTBEAT_PACKET;
    public float tps;
    public float load;
    public long upTime;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void encode() {
        this.reset();
        this.putFloat(this.tps);
        this.putFloat(this.load);
        this.putLong(this.upTime);
    }

    @Override
    public void decode() {
        this.tps = this.getFloat();
        this.load = this.getFloat();
        this.upTime = this.getLong();
    }
}
