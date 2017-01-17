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
    private Map<Byte, SynapseDataPacket> packetPool = new HashMap<>();
    private boolean connected = true;

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
        this.client.pushMainToThreadPacket(pk.getBuffer());
    }

    public boolean isConnected() {
        return connected;
    }

    public void process(){
        byte[] buffer = this.client.readThreadToMainPacket();

        while (buffer != null && buffer.length > 0) {
            this.handlePacket(buffer);
            buffer = this.client.readThreadToMainPacket();
        }

        this.connected = this.client.isConnected();
        if (this.client.isNeedAuth()) {
            this.synapse.connect();
            this.client.setNeedAuth(false);
        }
    }

    public SynapseDataPacket getPacket(byte[] buffer) {
        byte pid = buffer[0];
        SynapseDataPacket clazz = this.packetPool.get(pid);
        if (clazz != null) {
            SynapseDataPacket pk = clazz.clone();
            pk.setBuffer(buffer, 1);
            return pk;
        }
        return null;
    }

    public void handlePacket(byte[] buffer){
        SynapseDataPacket pk;
        if((pk = this.getPacket(buffer)) != null){
            pk.decode();
            this.synapse.handleDataPacket(pk);
        }
    }
    
    public void registerPacket(byte id, SynapseDataPacket packet) {
        this.packetPool.put(id, packet);
    }

    private void registerPackets() {
        this.packetPool.clear();
        this.registerPacket(SynapseInfo.HEARTBEAT_PACKET, new HeartbeatPacket());
        this.registerPacket(SynapseInfo.CONNECT_PACKET, new ConnectPacket());
        this.registerPacket(SynapseInfo.DISCONNECT_PACKET, new DisconnectPacket());
        this.registerPacket(SynapseInfo.REDIRECT_PACKET, new RedirectPacket());
        this.registerPacket(SynapseInfo.PLAYER_LOGIN_PACKET, new PlayerLoginPacket());
        this.registerPacket(SynapseInfo.PLAYER_LOGOUT_PACKET, new PlayerLogoutPacket());
        this.registerPacket(SynapseInfo.INFORMATION_PACKET, new InformationPacket());
        this.registerPacket(SynapseInfo.TRANSFER_PACKET, new TransferPacket());
        this.registerPacket(SynapseInfo.BROADCAST_PACKET, new BroadcastPacket());
        this.registerPacket(SynapseInfo.FAST_PLAYER_LIST_PACKET, new FastPlayerListPacket());
    }
}
