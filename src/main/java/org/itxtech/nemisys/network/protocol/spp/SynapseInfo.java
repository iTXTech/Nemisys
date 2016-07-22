package org.itxtech.nemisys.network.protocol.spp;

/**
 * Created by boybook on 16/6/24.
 */
public class SynapseInfo {

    public static final int CURRENT_PROTOCOL = 6;

    public static final byte HEARTBEAT_PACKET = 0x01;
    public static final byte CONNECT_PACKET = 0x02;
    public static final byte DISCONNECT_PACKET = 0x03;
    public static final byte REDIRECT_PACKET = 0x04;
    public static final byte PLAYER_LOGIN_PACKET = 0x05;
    public static final byte PLAYER_LOGOUT_PACKET = 0x06;
    public static final byte INFORMATION_PACKET = 0x07;
    public static final byte TRANSFER_PACKET = 0x08;
    public static final byte BROADCAST_PACKET = 0x09;
    public static final byte FAST_PLAYER_LIST_PACKET = 0x0a;

}
