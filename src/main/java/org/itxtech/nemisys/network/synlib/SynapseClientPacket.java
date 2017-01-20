package org.itxtech.nemisys.network.synlib;

import org.itxtech.nemisys.network.protocol.spp.SynapseDataPacket;

/**
 * SynapseClientPacket
 * ===============
 * author: boybook
 * Nukkit Project
 * ===============
 */
public class SynapseClientPacket {

    private String hash;
    private SynapseDataPacket packet;

    public SynapseClientPacket(String hash, SynapseDataPacket packet) {
        this.hash = hash;
        this.packet = packet;
    }

    public String getHash() {
        return hash;
    }

    public SynapseDataPacket getPacket() {
        return packet;
    }
}
