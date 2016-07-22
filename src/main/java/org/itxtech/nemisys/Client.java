package org.itxtech.nemisys;

import org.itxtech.nemisys.event.client.ClientAuthEvent;
import org.itxtech.nemisys.event.client.ClientConnectEvent;
import org.itxtech.nemisys.event.client.ClientDisconnectEvent;
import org.itxtech.nemisys.network.SynapseInterface;
import org.itxtech.nemisys.network.protocol.mcpe.DataPacket;
import org.itxtech.nemisys.network.protocol.mcpe.GenericPacket;
import org.itxtech.nemisys.network.protocol.mcpe.PlayerListPacket;
import org.itxtech.nemisys.network.protocol.spp.*;

import java.util.*;

/**
 * Author: PeratX
 * Nemisys Project
 */
public class Client {
    private Server server;
    private SynapseInterface interfaz;
    private String ip;
    private int port;
    private Map<UUID, Player> players = new HashMap<>();
    private boolean verified = false;
    private boolean isMainServer = false;
    private int maxPlayers;
    private long lastUpdate;
    private String description;
    private float tps;
    private float load;
    private long upTime;

    public Client(SynapseInterface interfaz, String ip, int port) {
        this.server = interfaz.getServer();
        this.interfaz = interfaz;
        this.ip = ip;
        this.port = port;
        this.lastUpdate = System.currentTimeMillis();

        this.server.getPluginManager().callEvent(new ClientConnectEvent(this));
    }

    public boolean isMainServer() {
        return this.isMainServer;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public String getHash() {
        return this.ip + ':' + this.port;
    }

    public String getDescription() {
        return this.description;
    }

    public float getTicksPerSecond(){
        return this.tps;
    }

    public float getTickUsage(){
        return this.load;
    }

    public long getUpTime(){
        return this.upTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void onUpdate(int currentTick) {
        if ((System.currentTimeMillis() - this.lastUpdate) >= 30 * 1000) {//30 seconds timeout
            this.close("timeout");
        }
    }

    public void handleDataPacket(DataPacket packet) {
        /*this.server.getPluginManager().callEvent(ev = new ClientRecvPacketEvent(this, packet));
		if(ev.isCancelled()){
			return;
		}*/

        switch (packet.pid()) {
            case SynapseInfo.BROADCAST_PACKET:
                GenericPacket gPacket = new GenericPacket();
                gPacket.setBuffer(((BroadcastPacket) packet).payload);
                for (UUID uniqueId : ((BroadcastPacket) packet).entries){
                    if(this.players.containsKey(uniqueId)){
                        this.players.get(uniqueId).sendDataPacket(gPacket, ((BroadcastPacket) packet).direct);
                    }
                }
                break;
            case SynapseInfo.HEARTBEAT_PACKET:
                if (!this.isVerified()) {
                    this.server.getLogger().error("Client " + this.getIp() + ":" + this.getPort() + " is not verified");
                    return;
                }
                HeartbeatPacket heartbeatPacket = (HeartbeatPacket) packet;
                this.lastUpdate = System.currentTimeMillis();
                this.server.getLogger().debug("Received Heartbeat Packet from " + this.getIp() + ":" + this.getPort());
                this.tps = heartbeatPacket.tps;
                this.load = heartbeatPacket.load;
                this.upTime = heartbeatPacket.upTime;

                InformationPacket pk = new InformationPacket();
                pk.type = InformationPacket.TYPE_CLIENT_DATA;
                pk.message = this.server.getClientDataJson();
                this.sendDataPacket(pk);

                break;
            case SynapseInfo.CONNECT_PACKET:
                ConnectPacket connectPacket = (ConnectPacket)packet;
                if (connectPacket.protocol != SynapseInfo.CURRENT_PROTOCOL) {
                    this.close("Incompatible SPP version! Require SPP version: " + SynapseInfo.CURRENT_PROTOCOL, true, org.itxtech.nemisys.network.protocol.spp.DisconnectPacket.TYPE_WRONG_PROTOCOL);
                    return;
                }
                pk = new InformationPacket();
                pk.type = InformationPacket.TYPE_LOGIN;
                if (this.server.comparePassword(connectPacket.password)) {
                    this.setVerified();
                    pk.message = InformationPacket.INFO_LOGIN_SUCCESS;
                    this.isMainServer = connectPacket.isMainServer;
                    this.description = connectPacket.description;
                    this.maxPlayers = connectPacket.maxPlayers;
                    this.server.addClient(this);
                    this.server.getLogger().notice("Client " + this.getIp() + ":" + this.getPort() + " has connected successfully");
                    this.server.getLogger().notice("mainServer: " + (this.isMainServer ? "true" : "false"));
                    this.server.getLogger().notice("description: " + this.description);
                    this.server.getLogger().notice("maxPlayers: " + this.maxPlayers);
                    this.server.updateClientData();
                    this.sendDataPacket(pk);
                } else {
                    pk.message = InformationPacket.INFO_LOGIN_FAILED;
                    this.server.getLogger().emergency("Client " + this.getIp() + ":" + this.getPort() + " tried to connect with wrong password!");
                    this.sendDataPacket(pk);
                    this.close("Auth failed!");
                }
                this.server.getPluginManager().callEvent(new ClientAuthEvent(this, connectPacket.password));
                break;
            case SynapseInfo.DISCONNECT_PACKET:
                this.close(((DisconnectPacket) packet).message, false);
                break;
            case SynapseInfo.REDIRECT_PACKET:
                UUID uuid = ((RedirectPacket) packet).uuid;
                if (this.players.containsKey(uuid)) {
                    GenericPacket genericPacket = new GenericPacket();
                    genericPacket.setBuffer(((RedirectPacket) packet).mcpeBuffer);
                    this.players.get(uuid).sendDataPacket(genericPacket, ((RedirectPacket) packet).direct);
                }/*else{
					this.server.getLogger().error("Error RedirectPacket 0x" + bin2hex(packet.buffer));
				}*/
                break;
            case SynapseInfo.TRANSFER_PACKET:
                Map<String, Client> clients = this.server.getClients();
                UUID uuid0 = ((TransferPacket)packet).uuid;
                if (this.players.containsKey(uuid0) && clients.containsKey(((TransferPacket)packet).clientHash)){
                this.players.get(uuid0).transfer(clients.get(((TransferPacket)packet).clientHash), true);
            }
            break;
            case SynapseInfo.FAST_PLAYER_LIST_PACKET:
                this.server.getScheduler().scheduleTask(new HandleFastPlayerListPacketRunnable((FastPlayerListPacket)packet), true);
                break;
            default:
                this.server.getLogger().error("Client " + this.getIp() + ":" + this.getPort() + " has sent an unknown packet " + packet.pid());
        }
    }

    public class HandleFastPlayerListPacketRunnable implements Runnable {
        private FastPlayerListPacket pk;
        public HandleFastPlayerListPacketRunnable(FastPlayerListPacket pk) {
            this.pk = pk;
        }
        @Override
        public void run() {
            handleFastPlayerListPacket(pk);
        }
    }

    public void handleFastPlayerListPacket(FastPlayerListPacket fastPlayerListPacket) {
        Player sendTo = this.getPlayers().get(fastPlayerListPacket.sendTo);
        if (sendTo != null) {
            PlayerListPacket playerListPacket = new PlayerListPacket();
            playerListPacket.type = fastPlayerListPacket.type;
            List<PlayerListPacket.Entry> entries = new ArrayList<>();
            if (fastPlayerListPacket.type == FastPlayerListPacket.TYPE_ADD) {
                for (FastPlayerListPacket.Entry entry: fastPlayerListPacket.entries) {
                    Player player = this.getPlayers().get(entry.uuid);
                    if (player != null && player.getSkin() != null && player.getSkin().isValid())
                        entries.add(new PlayerListPacket.Entry(entry.uuid, entry.entityId, entry.name, player.getSkin()));
                }
            } else {
                for (FastPlayerListPacket.Entry entry: fastPlayerListPacket.entries) {
                    entries.add(new PlayerListPacket.Entry(entry.uuid));
                }
            }
            playerListPacket.entries = entries.stream().toArray(PlayerListPacket.Entry[]::new);
            sendTo.sendDataPacket(playerListPacket);
        }
    }

    public void sendDataPacket(SynapseDataPacket pk) {
        this.interfaz.putPacket(this, pk);
		/*this.server.getPluginManager().callEvent(ev = new ClientSendPacketEvent(this, pk));
		if(!ev.isCancelled()){
			this.interfaz.putPacket(this, pk);
		}*/
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public boolean isVerified() {
        return this.verified;
    }

    public void setVerified() {
        this.verified = true;
    }

    public Map<UUID, Player> getPlayers() {
        return this.players;
    }

    public void addPlayer(Player player) {
        this.players.put(player.getUUID(), player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player.getUUID());
    }

    public void closeAllPlayers() {
        for (Player player : new ArrayList<>(this.players.values())) {
            player.close("Server Closed");
        }
    }

    public void close() {
        this.close("Generic reason");
    }

    public void close(String reason) {
        this.close(reason, true);
    }

    public void close(String reason, boolean needPk) {
        this.close(reason, needPk, DisconnectPacket.TYPE_GENERIC);
    }

    public void close(String reason, boolean needPk, byte type) {
        ClientDisconnectEvent ev;
        this.server.getPluginManager().callEvent(ev = new ClientDisconnectEvent(this, reason, type));
        reason = ev.getReason();
        this.server.getLogger().info("Client " + this.ip + ":" + this.port + " has disconnected due to " + reason);
        if (needPk) {
            DisconnectPacket pk = new DisconnectPacket();
            pk.type = type;
            pk.message = reason;
            this.sendDataPacket(pk);
        }
        this.closeAllPlayers();
        this.interfaz.removeClient(this);
        this.server.removeClient(this);
    }

}
