package org.itxtech.nemisys;

import org.itxtech.nemisys.event.player.PlayerLoginEvent;
import org.itxtech.nemisys.event.player.PlayerLogoutEvent;
import org.itxtech.nemisys.event.player.PlayerTransferEvent;
import org.itxtech.nemisys.network.SourceInterface;
import org.itxtech.nemisys.network.protocol.mcpe.*;
import org.itxtech.nemisys.network.protocol.spp.PlayerLoginPacket;
import org.itxtech.nemisys.network.protocol.spp.PlayerLogoutPacket;
import org.itxtech.nemisys.network.protocol.spp.RedirectPacket;
import org.itxtech.nemisys.utils.Binary;
import org.itxtech.nemisys.utils.Skin;
import org.itxtech.nemisys.utils.TextFormat;

import java.util.*;

/**
 * Author: PeratX
 * Nemisys Project
 */
public class Player {
    private byte[] cachedLoginPacket = new byte[0];
    private String name;
    private String ip;
    private int port;
    private long clientId;
    private long randomClientId;
    private int protocol;
    private UUID uuid;
    private SourceInterface interfaz;
    private Client client;
    private Server server;
    private byte[] rawUUID;
    private boolean isFirstTimeLogin = true;
    private long lastUpdate;
    public boolean closed;
    private Skin skin;

    public Player(SourceInterface interfaz, long clientId, String ip, int port){
        this.interfaz = interfaz;
        this.clientId = clientId;
        this.ip = ip;
        this.port = port;
        this.name = "null";
        this.server = Server.getInstance();
        this.lastUpdate = System.currentTimeMillis();
    }

    public long getClientId(){
        return this.clientId;
    }

    public UUID getUniqueId(){
        return this.uuid;
    }

    public byte[] getRawUUID(){
        return this.rawUUID;
    }

    public Server getServer(){
        return this.server;
    }

    public void handleDataPacket(DataPacket packet){
        if(this.closed){
            return;
        }
        this.lastUpdate = System.currentTimeMillis();

        switch (packet.pid()){
            case ProtocolInfo.BATCH_PACKET:
                if(this.cachedLoginPacket.length == 0){
                    this.getServer().getNetwork().processBatch((BatchPacket)packet, this);
                }else{
                    this.redirectPacket(packet.getBuffer());
                }
                break;
            case ProtocolInfo.LOGIN_PACKET:
                LoginPacket loginPacket = (LoginPacket)packet; 
                this.cachedLoginPacket = loginPacket.cacheBuffer;
                this.skin = loginPacket.skin;
                this.name = loginPacket.username;
                this.uuid = loginPacket.clientUUID;
                this.rawUUID = Binary.writeUUID(this.uuid);
                this.randomClientId = loginPacket.clientId;
                this.protocol = loginPacket.protocol;

                this.server.getLogger().info(this.getServer().getLanguage().translateString("nemisys.player.logIn", new String[]{
                        TextFormat.AQUA + this.name + TextFormat.WHITE,
                        this.ip,
                        String.valueOf(this.port),
                        TextFormat.GREEN + this.getRandomClientId() + TextFormat.WHITE,
                }));

                Map<String, Client> c = this.server.getMainClients();

                String clientHash;
                if(c.size() > 0){
                    clientHash = new ArrayList<>(c.keySet()).get(new Random().nextInt(c.size()));
                }else{
                    clientHash = "";
                }

                PlayerLoginEvent ev;
                this.server.getPluginManager().callEvent(ev = new PlayerLoginEvent(this, "Plugin Reason", clientHash));
                if(ev.isCancelled()){
                    this.close(ev.getKickMessage());
                    break;
                }

                if(!this.server.getClients().containsKey(ev.getClientHash())){
                    this.close("Synapse Server: " + TextFormat.RED + "No server online!");
                    break;
                }

                this.transfer(this.server.getClients().get(ev.getClientHash()));
                break;
            default:
                if (this.client != null) this.redirectPacket(packet.getBuffer());
        }
    }

    public void redirectPacket(byte[] buffer){
        RedirectPacket pk = new RedirectPacket();
        pk.uuid = this.uuid;
        pk.direct = false;
        pk.mcpeBuffer = buffer;
        this.client.sendDataPacket(pk);
    }

    public String getIp(){
        return this.ip;
    }

    public int getPort(){
        return this.port;
    }

    public UUID getUUID(){
        return this.uuid;
    }

    public String getName(){
        return this.name;
    }

    public void onUpdate(long currentTick){
        if((System.currentTimeMillis() - this.lastUpdate) > 5 * 60 * 1000){//timeout
            this.close("timeout");
        }
    }

    public void removeAllPlayers(){
        PlayerListPacket pk = new PlayerListPacket();
        pk.type = PlayerListPacket.TYPE_REMOVE;
        List<PlayerListPacket.Entry> entries = new ArrayList<>();
        for (Player p : this.client.getPlayers().values()) {
            if (p == this) {
                continue;
            }
            entries.add(new PlayerListPacket.Entry(p.getUUID()));
        }

        pk.entries = entries.stream().toArray(PlayerListPacket.Entry[]::new);
        this.sendDataPacket(pk);
    }

    public void transfer(Client client) {
        this.transfer(client, false);
    }

    public void transfer(Client client, boolean needDisconnect){
        PlayerTransferEvent ev;
        this.server.getPluginManager().callEvent(ev = new PlayerTransferEvent(this, client, needDisconnect));
        if(!ev.isCancelled()){
            if(this.client != null && needDisconnect){
                PlayerLogoutPacket pk = new PlayerLogoutPacket();
                pk.uuid = this.uuid;
                pk.reason = "Player has been transferred";
                this.client.sendDataPacket(pk);
                this.client.removePlayer(this);
                this.removeAllPlayers();
            }
            this.client = ev.getTargetClient();
            this.client.addPlayer(this);
            PlayerLoginPacket pk = new PlayerLoginPacket();
            pk.uuid = this.uuid;
            pk.address = this.ip;
            pk.port = this.port;
            pk.isFirstTime = this.isFirstTimeLogin;
            pk.cachedLoginPacket = this.cachedLoginPacket;
            this.client.sendDataPacket(pk);

            this.isFirstTimeLogin = false;

            this.server.getLogger().info(this.name + " has been transferred to " + this.client.getIp() + ":" + this.client.getPort());
        }
    }

    public void sendDataPacket(DataPacket pk){
        this.sendDataPacket(pk, false);
    }

    public void sendDataPacket(DataPacket pk, boolean direct){
        this.sendDataPacket(pk, direct, false);
    }

    public void sendDataPacket(DataPacket pk, boolean direct, boolean needACK){
        this.interfaz.putPacket(this, pk, needACK, direct);
    }

    public void close(){
        this.close("Generic Reason");
    }

    public void close(String reason){
        this.close(reason, true);
    }

    public void close(String reason, boolean notify){
        if(!this.closed){
            if(notify && reason.length() > 0){
                DisconnectPacket pk = new DisconnectPacket();
                pk.message = reason;
                this.sendDataPacket(pk, true);
            }

            this.server.getPluginManager().callEvent(new PlayerLogoutEvent(this));
            this.closed = true;

            if(this.client != null){
                PlayerLogoutPacket pk = new PlayerLogoutPacket();
                pk.uuid = this.uuid;
                pk.reason = reason;
                this.client.sendDataPacket(pk);
                this.client.removePlayer(this);
            }

            this.server.getLogger().info(this.getServer().getLanguage().translateString("nemisys.player.logOut", new String[]{
                            TextFormat.AQUA + this.getName() + TextFormat.WHITE,
                            this.ip,
                            String.valueOf(this.port),
                            this.getServer().getLanguage().translateString(reason)
            }));

            this.interfaz.close(this, notify ? reason : "");
            this.getServer().removePlayer(this);
        }
    }

    public int rawHashCode() {
        return super.hashCode();
    }

    public int getProtocol() {
        return protocol;
    }

    public long getRandomClientId() {
        return randomClientId;
    }

    public Skin getSkin() {
        return this.skin;
    }

}
