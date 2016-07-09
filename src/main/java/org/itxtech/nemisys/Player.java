package org.itxtech.nemisys;

import org.itxtech.nemisys.event.Timings;
import org.itxtech.nemisys.event.TimingsHandler;
import org.itxtech.nemisys.network.SourceInterface;
import org.itxtech.nemisys.network.protocol.mcpe.DataPacket;
import org.itxtech.nemisys.network.protocol.mcpe.PlayerListPacket;
import org.itxtech.nemisys.network.protocol.spp.PlayerLoginPacket;
import org.itxtech.nemisys.network.protocol.spp.RedirectPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Author: PeratX
 * Nemisys Project
 */
public class Player {
    private PlayerLoginPacket cachedLoginPacket = null;
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
    private boolean closed;

    public Player(SourceInterface interfaz, long clientId, String ip, int port){
        this.interfaz = interfaz;
        this.clientId = clientId;
        this.ip = ip;
        this.port = port;
        this.name = "Unknown";
        this.server = Server.getInstance();
        this.lastUpdate = System.currentTimeMillis();
    }

    public long getClientId(){
        return this.clientId;
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
        TimingsHandler timings = Timings.getReceiveDataPacketTimings(packet);
        timings.startTiming();
        this.lastUpdate = System.currentTimeMillis();

        switch (packet.pid()){
            case
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

    public UUID getUuid(){
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
        for (Player p : this.client.getPlayers()) {
            if (p == player) {
                continue;
            }

            entries.add(new PlayerListPacket.Entry(p.getUuid()));
        }

        pk.entries = entries.stream().toArray(PlayerListPacket.Entry[]::new);
        this.sendDataPacket(pk);
    }

    public void sendDataPacket(DataPacket pk){
        this.sendDataPacket(pk, false);
    }

    public void sendDataPacket(DataPacket pk, boolean direct){
        this.sendDataPacket(pk, direct, false);
    }

    public void sendDataPacket(DataPacket pk, boolean direct, boolean needACK){
        this.interfaz.putPacket(this,pk, needACK, direct);
    }

    public void close(){
        this.close("Generic Reason");
    }

    public void close(String reason){
        this.close(reason, true);
    }

    public void close(String reason, boolean notify){

    }

    public int rawHashCode() {
        return super.hashCode();
    }
}
