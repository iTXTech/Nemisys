package org.itxtech.nemisys.network;

import org.itxtech.nemisys.SynapseAPI;
import org.itxtech.nemisys.network.protocol.spp.*;
import org.itxtech.nemisys.network.synlib.SynapseClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by boybook on 16/6/24.
 */
public class SynapseInterface {

    private SynapseAPI synapse;
    private String ip;
    private int port;
    private SynapseClient client;
    private Map<Byte, SynapseDataPacket> packetPool = new HashMap<>();
    private boolean connected = true;
    private boolean needReconnect = false;

    public SynapseInterface(SynapseAPI server, String ip, int port){
        this.synapse = server;
        this.ip = ip;
        this.port = port;
        this.registerPackets();
        this.client = new SynapseClient(server.getServer().getLogger(), port, ip);
    }

    public SynapseAPI getSynapse() {
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
        /** @var DataPacket class */
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
    }
}
