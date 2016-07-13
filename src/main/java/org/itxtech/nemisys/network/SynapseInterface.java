package org.itxtech.nemisys.network;

import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.Server;
import org.itxtech.nemisys.network.protocol.spp.*;
import org.itxtech.nemisys.network.synlib.SynapseServer;
import org.itxtech.nemisys.utils.Binary;
import org.itxtech.nemisys.utils.BinaryStream;
import org.itxtech.nemisys.utils.Util;
import org.itxtech.nemisys.utils.Utils;

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
        this.interfaz.pushMainToThreadPacket(pk.getBuffer());  //TODO this.interface.pushMainToThreadPacket(client.getHash() . "|" . pk.buffer);
    }

    //TODO
    public void process() {
        byte[] open = this.interfaz.getClientOpenRequest();
        while (open != null && open.length > 0) {
            String hash = Utils.readClientHash(open);
            String[] arr = hash.split(":");
            this.addClient(arr[0], Integer.parseInt(arr[1]));
        }

        byte[] buffer = this.interfaz.readThreadToMainPacket();
        while (buffer != null && buffer.length > 0) {
            this.handlePacket(buffer);
            buffer = this.interfaz.readThreadToMainPacket();
        }

        byte[] close = this.interfaz.getInternalClientCloseRequest();
        while (close != null && close.length > 0) {
            //TODO
            close = this.interfaz.getInternalClientCloseRequest();
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
    }
}
