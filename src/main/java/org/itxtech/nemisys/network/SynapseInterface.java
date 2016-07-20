package org.itxtech.nemisys.network;

import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.Server;
import org.itxtech.nemisys.network.protocol.spp.*;
import org.itxtech.nemisys.network.synlib.SynapseServer;
import org.itxtech.nemisys.utils.Binary;
import org.itxtech.nemisys.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by boybook on 16/6/24.
 */
public class SynapseInterface {

    private Server server;
    private String ip;
    private int port;
    private Map<String, Client> clients = new HashMap<>();
    private Map<Byte, SynapseDataPacket> packetPool = new HashMap<>();
    private SynapseServer interfaz;

    public SynapseInterface(Server server, String ip, int port) {
        this.server = server;
        this.ip = ip;
        this.port = port;
        this.registerPackets();
        this.interfaz = new SynapseServer(server.getLogger(), this, port, ip);
    }

    public SynapseServer getInterface(){
        return this.interfaz;
    }

    public Server getServer() {
        return server;
    }

    public void addClient(String ip, int port) {
        this.clients.put(ip + ":" + port, new Client(this, ip, port));
    }

    public void removeClient(Client client) {
        this.interfaz.addExternalClientCloseRequest(Utils.writeClientHash(client.getHash()));
        this.clients.remove(client.getHash());
    }

    public void putPacket(Client client, SynapseDataPacket pk) {
        if (!pk.isEncoded) {
            pk.encode();
        }
        this.interfaz.pushMainToThreadPacket(Binary.appendBytes(
                new byte[]{(byte) (client.getHash().length() & 0xff)},
                client.getHash().getBytes(StandardCharsets.UTF_8),
                pk.getBuffer()
        ));
    }

    private boolean openClients(){
        byte[] open = this.interfaz.getClientOpenRequest();
        if(open != null && open.length > 0) {
            String hash = Utils.readClientHash(open);
            String[] arr = hash.split(":");
            this.addClient(arr[0], Integer.parseInt(arr[1]));
            return true;
        }
        return false;
    }

    private boolean processPackets(){
        byte[] buffer = this.interfaz.readThreadToMainPacket();
        if(buffer != null && buffer.length > 0) {
            int offset = 0;
            int len = buffer[offset++];
            String hash = new String(Binary.subBytes(buffer, offset, len), StandardCharsets.UTF_8);
            offset += len;
            byte[] payload = Binary.subBytes(buffer, offset);
            this.handlePacket(hash, payload);
            return true;
        }
        return false;
    }

    private boolean closeClients(){
        byte[] close = this.interfaz.getInternalClientCloseRequest();
        if(close != null && close.length > 0) {
            String hash = Utils.readClientHash(close);
            if(this.clients.containsKey(hash)){
                this.clients.get(hash).close();
                this.clients.remove(hash);
            }
            return true;
        }
        return false;
    }

    public void process() {
        while(this.openClients());
        while(this.processPackets());
        while(this.closeClients());
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

    public void handlePacket(String hash, byte[] buffer) {
        if (!this.clients.containsKey(hash)) return;
        Client client = this.clients.get(hash);
        SynapseDataPacket pk;
        if ((pk = this.getPacket(buffer)) != null) {
            pk.decode();
            client.handleDataPacket(pk);
        } else {
            this.server.getLogger().critical("Error packet: " + Binary.bytesToHexString(buffer));
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
    }
}
