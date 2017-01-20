package org.itxtech.nemisys.network.synlib;

/**
 * SynapseProtocolHeader
 * ===============
 * author: boybook
 * Synapse Protocol Header
 * nemisys
 * ===============
 */
public class SynapseProtocolHeader {

    /** Head Length */
    public static final int HEAD_LENGTH = 7;

    /** Magic */
    public static final short MAGIC = (short) 0xbabe;

    private int pid;
    private int bodyLength;

    public int pid() {
        return pid;
    }

    public void pid(int pid) {
        this.pid = pid;
    }

    public int bodyLength() {
        return bodyLength;
    }

    public void bodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

}
