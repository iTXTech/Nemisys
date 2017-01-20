package org.itxtech.nemisys.synapse.network;

import org.itxtech.nemisys.network.protocol.spp.*;
import org.itxtech.nemisys.synapse.SynapseEntry;
import org.itxtech.nemisys.synapse.network.synlib.SynapseClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by boybook on 16/6/24.
 */
public class SynapseInterface {

    private SynapseEntry synapse;
    private SynapseClient client;
    private static Map<Byte, SynapseDataPacket> packetPool = new HashMap<>();
    private boolean connected = false;

    public SynapseInterface(SynapseEntry server, String ip, int port){
        this.synapse = server;
        this.registerPackets();
        this.client = new SynapseClient(server.getSynapse().getLogger(), port, ip);
    }

    public SynapseEntry getSynapse() {
        return synapse;
    }

    public void reconnect(){
        this.client.reconnect();
    }

    public void shutdown(){
        this.client.shutdown();
    }

    public void putPacket(SynapseDataPacket pk){
        if(!pk.isEncoded){
            pk.encode();
        }
        this.client.pushMainToThreadPacket(pk);
    }

    public boolean isConnected() {
        return connected;
    }

    public void process(){
        SynapseDataPacket pk = this.client.readThreadToMainPacket();

        while (pk != null) {
            this.handlePacket(pk);
            pk = this.client.readThreadToMainPacket();
        }

        this.connected = this.client.isConnected();
        if (this.connected && this.client.isNeedAuth()) {
            this.synapse.connect();
            this.client.setNeedAuth(false);
        }
    }

    public static SynapseDataPacket getPacket(byte pid, byte[] buffer) {
        SynapseDataPacket clazz = packetPool.get(pid);
        if (clazz != null) {
            SynapseDataPacket pk = clazz.clone();
            pk.setBuffer(buffer, 0);
            return pk;
        }
        return null;
    }

    public void handlePacket(SynapseDataPacket pk){
        if (pk != null) {
            pk.decode();
            this.synapse.handleDataPacket(pk);
        }
    }

    public static void registerPacket(byte id, SynapseDataPacket packet) {
        packetPool.put(id, packet);
    }

    private void registerPackets() {
        packetPool.clear();
        registerPacket(SynapseInfo.HEARTBEAT_PACKET, new HeartbeatPacket());
        registerPacket(SynapseInfo.CONNECT_PACKET, new ConnectPacket());
        registerPacket(SynapseInfo.DISCONNECT_PACKET, new DisconnectPacket());
        registerPacket(SynapseInfo.REDIRECT_PACKET, new RedirectPacket());
        registerPacket(SynapseInfo.PLAYER_LOGIN_PACKET, new PlayerLoginPacket());
        registerPacket(SynapseInfo.PLAYER_LOGOUT_PACKET, new PlayerLogoutPacket());
        registerPacket(SynapseInfo.INFORMATION_PACKET, new InformationPacket());
        registerPacket(SynapseInfo.TRANSFER_PACKET, new TransferPacket());
        registerPacket(SynapseInfo.BROADCAST_PACKET, new BroadcastPacket());
        registerPacket(SynapseInfo.FAST_PLAYER_LIST_PACKET, new FastPlayerListPacket());
    }
}
