package org.itxtech.nemisys.network;

import org.itxtech.nemisys.Nemisys;
import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.Server;
import org.itxtech.nemisys.network.protocol.mcpe.*;
import org.itxtech.nemisys.utils.Binary;
import org.itxtech.nemisys.utils.BinaryStream;
import org.itxtech.nemisys.utils.Zlib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Network {

    private Class<? extends DataPacket>[] packetPool = new Class[256];

    private Server server;

    private Set<SourceInterface> interfaces = new HashSet<>();

    private Set<AdvancedSourceInterface> advancedInterfaces = new HashSet<>();

    private double upload = 0;
    private double download = 0;

    private String name;

    public Network(Server server) {
        this.registerPackets();
        this.server = server;
    }

    public void addStatistics(double upload, double download) {
        this.upload += upload;
        this.download += download;
    }

    public double getUpload() {
        return upload;
    }

    public double getDownload() {
        return download;
    }

    public void resetStatistics() {
        this.upload = 0;
        this.download = 0;
    }

    public Set<SourceInterface> getInterfaces() {
        return interfaces;
    }

    public void processInterfaces() {
        for (SourceInterface interfaz : this.interfaces) {
            try {
                interfaz.process();
            } catch (Exception e) {
                if (Nemisys.DEBUG > 1) {
                    this.server.getLogger().logException(e);
                }

                interfaz.emergencyShutdown();
                this.unregisterInterface(interfaz);
                this.server.getLogger().critical(this.server.getLanguage().translateString("nemisys.server.networkError", new String[]{interfaz.getClass().getName(), e.getMessage()}));
            }
        }
    }

    public void registerInterface(SourceInterface interfaz) {
        this.interfaces.add(interfaz);
        if (interfaz instanceof AdvancedSourceInterface) {
            this.advancedInterfaces.add((AdvancedSourceInterface) interfaz);
            ((AdvancedSourceInterface) interfaz).setNetwork(this);
        }
        interfaz.setName(this.name);
    }

    public void unregisterInterface(SourceInterface interfaz) {
        this.interfaces.remove(interfaz);
        this.advancedInterfaces.remove(interfaz);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updateName();
    }

    public void updateName() {
        for (SourceInterface interfaz : this.interfaces) {
            interfaz.setName(this.name);
        }
    }

    public void registerPacket(byte id, Class<? extends DataPacket> clazz) {
        this.packetPool[id & 0xff] = clazz;
    }

    public Server getServer() {
        return server;
    }

    public void processBatch(BatchPacket packet, Player player) {
        byte[] data;
        try {
            data = Zlib.inflate(packet.payload, 64 * 1024 * 1024);
        } catch (Exception e) {
            return;
        }

        int len = data.length;
        BinaryStream stream = new BinaryStream(data);
        try {
            List<DataPacket> packets = new ArrayList<>();
            while (stream.offset < len) {
                byte[] buf = stream.getByteArray();

                DataPacket pk;

                if ((pk = this.getPacket(buf[0])) != null) {
                    pk.setBuffer(buf, 3); //skip 2 more bytes

                    pk.decode();

                    packets.add(pk);
                }
            }

            processPackets(player, packets);

        } catch (Exception e) {
            if (Nemisys.DEBUG > 0) {
                this.server.getLogger().debug("BatchPacket 0x" + Binary.bytesToHexString(packet.payload));
                this.server.getLogger().logException(e);
            }
        }
    }

    /**
     * Process packets obtained from batch packets
     * Required to perform additional analyses and filter unnecessary packets
     *
     * @param packets
     */
    public void processPackets(Player player, List<DataPacket> packets) {
        if (packets.isEmpty()) return;
        packets.forEach(player::addOutgoingPacket);
    }


    public DataPacket getPacket(byte id) {
        Class<? extends DataPacket> clazz = this.packetPool[id & 0xff];
        if (clazz != null) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                Server.getInstance().getLogger().logException(e);
            }
        }
        return new GenericPacket();
    }

    public void sendPacket(String address, int port, byte[] payload) {
        for (AdvancedSourceInterface interfaz : this.advancedInterfaces) {
            interfaz.sendRawPacket(address, port, payload);
        }
    }

    public void blockAddress(String address) {
        this.blockAddress(address, 300);
    }

    public void blockAddress(String address, int timeout) {
        for (AdvancedSourceInterface interfaz : this.advancedInterfaces) {
            interfaz.blockAddress(address, timeout);
        }
    }

    private void registerPackets() {
        this.packetPool = new Class[256];

        this.registerPacket(ProtocolInfo.LOGIN_PACKET, LoginPacket.class);
        this.registerPacket(ProtocolInfo.DISCONNECT_PACKET, DisconnectPacket.class);
        this.registerPacket(ProtocolInfo.BATCH_PACKET, BatchPacket.class);
        this.registerPacket(ProtocolInfo.PLAYER_LIST_PACKET, PlayerListPacket.class);
        this.registerPacket(ProtocolInfo.AVAILABLE_COMMANDS_PACKET, AvailableCommandsPacket.class);
        this.registerPacket(ProtocolInfo.ADD_ENTITY_PACKET, AddEntityPacket.class);
        this.registerPacket(ProtocolInfo.ADD_PLAYER_PACKET, AddPlayerPacket.class);
        this.registerPacket(ProtocolInfo.ADD_ITEM_ENTITY_PACKET, AddItemEntityPacket.class);
        this.registerPacket(ProtocolInfo.ADD_PAINTING_PACKET, AddPaintingPacket.class);
        this.registerPacket(ProtocolInfo.REMOVE_ENTITY_PACKET, RemoveEntityPacket.class);
        this.registerPacket(ProtocolInfo.TEXT_PACKET, TextPacket.class);
        this.registerPacket(ProtocolInfo.COMMAND_REQUEST_PACKET, CommandRequestPacket.class);
    }
}
