package org.itxtech.nemisys;

import org.itxtech.nemisys.network.SynapseInterface;
import org.itxtech.nemisys.network.protocol.mcpe.*;
import org.itxtech.nemisys.network.protocol.spp.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: PeratX
 * Nemisys Project
 */
public class Client {

    /** @var Server */
    private Server server;
    /** @var SynapseInterface */
    private SynapseInterface interfaz;
    private String ip;
    private int port;
    /** @var Player[] */
    private Map<byte[], Player> players = new HashMap<>();
    private boolean verified = false;
    private boolean isMainServer = false;
    private int maxPlayers;
    private long lastUpdate;
    private String description;

    public Client(SynapseInterface interfaz, String ip, int port){
        this.server = interfaz.getServer();
        this.interfaz = interfaz;
        this.ip = ip;
        this.port = port;
        this.lastUpdate = System.currentTimeMillis();

        //TODO this.server.getPluginManager().callEvent(new ClientConnectEvent(this));
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

    public String getDescription()  {
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void onUpdate(int currentTick){
        if((System.currentTimeMillis() - this.lastUpdate) >= 30 * 1000){//30 seconds timeout
            this.close("timeout");
        }
    }

    public void handleDataPacket(DataPacket packet){
		/*this.server.getPluginManager().callEvent(ev = new ClientRecvPacketEvent(this, packet));
		if(ev.isCancelled()){
			return;
		}*/

        switch(packet.pid()){
            case SynapseInfo.HEARTBEAT_PACKET:
                if(!this.isVerified()){
                    this.server.getLogger().error("Client " + this.getIp() + ":" + this.getPort() + " is not verified");
                    return;
                }
                this.lastUpdate = System.currentTimeMillis();
                this.server.getLogger().notice("Received Heartbeat Packet from " + this.getIp() + ":" + this.getPort());

                InformationPacket pk = new InformationPacket();
                pk.type = InformationPacket.TYPE_CLIENT_DATA;
                pk.message = this.server.getClientData();
                this.sendDataPacket(pk);

                break;
            case SynapseInfo.CONNECT_PACKET:
                /** @var ConnectPacket packet */

                if(((ConnectPacket)packet).protocol != SynapseInfo.CURRENT_PROTOCOL){
                    this.close("Incompatible SPP version! Require SPP version: " + SynapseInfo.CURRENT_PROTOCOL, true, DisconnectPacket.TYPE_WRONG_PROTOCOL);
                    return;
                }
                pk = new InformationPacket();
                pk.type = InformationPacket.TYPE_LOGIN;
                if(this.server.comparePassword(base64_decode(packet.encodedPassword))){
                    this.setVerified();
                    pk.message = InformationPacket.INFO_LOGIN_SUCCESS;
                    this.isMainServer = packet.isMainServer;
                    this.description = packet.description;
                    this.maxPlayers = packet.maxPlayers;
                    this.server.addClient(this);
                    this.server.getLogger().notice("Client {this.getIp()}:{this.getPort()} has connected successfully");
                    this.server.getLogger().notice("mainServer: " + (this.isMainServer ? "true" : "false"));
                    this.server.getLogger().notice("description: this.description");
                    this.server.getLogger().notice("maxPlayers: this.maxPlayers");
                    this.server.updateClientData();
                    this.sendDataPacket(pk);
                }else{
                    pk.message = InformationPacket.INFO_LOGIN_FAILED;
                    this.server.getLogger().emergency("Client {this.getIp()}:{this.getPort()} tried to connect with wrong password!");
                    this.sendDataPacket(pk);
                    this.close("Auth failed!");
                }
                this.server.getPluginManager().callEvent(new ClientAuthEvent(this, packet.encodedPassword));
                break;
            case SynapseInfo.DISCONNECT_PACKET:
                /** @var DisconnectPacket packet */
                this.close(packet.message, false);
                break;
            case SynapseInfo.REDIRECT_PACKET:
                /** @var RedirectPacket packet */
                byte[] uuid = ((RedirectPacket)packet).uuid;
                if(this.players[uuid = packet.uuid.toBinary()])){
                    GenericPacket pk0 = new GenericPacket();
                    pk0.buffer = packet.mcpeBuffer;
                    this.players[uuid].sendDataPacket(pk, packet.direct);
                }/*else{
					this.server.getLogger().error("Error RedirectPacket 0x" + bin2hex(packet.buffer));
				}*/
                break;
            case Info.TRANSFER_PACKET:
                /** @var TransferPacket pk */
                clients = this.server.getClients();
                if(isset(this.players[uuid = packet.uuid.toBinary()]) and isset(clients[packet.clientHash])){
                this.players[uuid].transfer(clients[packet.clientHash], true);
            }
            break;
            default:
                this.server.getLogger().error("Client {this.getIp()}:{this.getPort()} send an unknown packet " + packet.NETWORK_ID);
        }
    }

    public void sendDataPacket(DataPacket pk){
        this.interfaz.putPacket(this, pk);
		/*this.server.getPluginManager().callEvent(ev = new ClientSendPacketEvent(this, pk));
		if(!ev.isCancelled()){
			this.interfaz.putPacket(this, pk);
		}*/
    }

    public String getIp(){
        return this.ip;
    }

    public int getPort(){
        return this.port;
    }

    public boolean isVerified(){
        return this.verified;
    }

    public void setVerified(){
        this.verified = true;
    }

    public Map<byte[], Player> getPlayers(){
        return this.players;
    }

    public void addPlayer(Player player){
        this.players.put(player.getRawUUID(), player);
    }

    public void removePlayer(Player player){
        this.players.remove(player.getRawUUID());
    }

    public void closeAllPlayers(){
        for (Player player: new ArrayList<>(this.players.values())){
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
        this.close(reason, needPk, DisconnectPacket.NETWORK_ID);
    }

    public void close(String reason, boolean needPk, byte type){
        //TODO this.server.getPluginManager().callEvent(ev = new ClientDisconnectEvent(this, reason, type));
        reason = ev.getReason();
        this.server.getLogger().info("Client this.ip:this.port has disconnected due to reason");
        if(needPk){
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
