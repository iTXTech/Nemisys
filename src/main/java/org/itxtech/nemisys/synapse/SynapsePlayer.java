package org.itxtech.nemisys.synapse;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.server.DataPacketSendEvent;
import org.itxtech.nemisys.event.synapse.player.SynapsePlayerConnectEvent;
import org.itxtech.nemisys.network.SourceInterface;
import org.itxtech.nemisys.network.protocol.mcpe.DataPacket;
import org.itxtech.nemisys.network.protocol.spp.*;
import org.itxtech.nemisys.utils.ClientData;

import java.util.UUID;

public class SynapsePlayer extends Player {

    private boolean isFirstTimeLogin = false;

    protected SynapseEntry synapseEntry;

    public SynapsePlayer(SourceInterface interfaz, SynapseEntry synapseEntry, Long clientID, String ip, int port) {
        super(interfaz, clientID, ip, port);
        this.synapseEntry = synapseEntry;
    }

    public void handleLoginPacket(PlayerLoginPacket packet) {
        this.isFirstTimeLogin = packet.isFirstTime;
        SynapsePlayerConnectEvent ev;
        this.getServer().getPluginManager().callEvent(ev = new SynapsePlayerConnectEvent(this, this.isFirstTimeLogin));
        if (!ev.isCancelled()) {
            DataPacket pk = this.getSynapseEntry().getSynapse().getPacket(packet.cachedLoginPacket);
            pk.decode();
            this.handleDataPacket(pk);
        }
    }

    public SynapseEntry getSynapseEntry() {
        return synapseEntry;
    }

    public void transfer(String hash) {
        ClientData clients = this.getSynapseEntry().getClientData();
        if (clients.clientList.containsKey(hash)) {
            TransferPacket pk = new TransferPacket();
            pk.uuid = this.getUniqueId();
            pk.clientHash = hash;
            this.getSynapseEntry().sendDataPacket(pk);
        }
    }

    public void setUniqueId(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void sendDataPacket(DataPacket pk, boolean direct, boolean needACK){
        DataPacketSendEvent ev = new DataPacketSendEvent(this, pk);
        this.getServer().getPluginManager().callEvent(ev);
        if (!ev.isCancelled()) {
            super.sendDataPacket(pk, direct, needACK);
        }
    }

}
