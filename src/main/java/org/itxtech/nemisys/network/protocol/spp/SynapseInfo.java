package org.itxtech.nemisys.network.protocol.spp;

public interface SynapseInfo {

    int CURRENT_PROTOCOL = 8;

    byte HEARTBEAT_PACKET = 0x01;
    byte CONNECT_PACKET = 0x02;
    byte DISCONNECT_PACKET = 0x03;
    byte REDIRECT_PACKET = 0x04;
    byte PLAYER_LOGIN_PACKET = 0x05;
    byte PLAYER_LOGOUT_PACKET = 0x06;
    byte INFORMATION_PACKET = 0x07;
    byte TRANSFER_PACKET = 0x08;
    byte BROADCAST_PACKET = 0x09;
    byte FAST_PLAYER_LIST_PACKET = 0x0a;

}
