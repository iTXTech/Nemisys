package org.itxtech.nemisys.synapse;

import org.itxtech.nemisys.Server;
import org.itxtech.nemisys.network.RakNetInterface;
import org.itxtech.nemisys.network.SourceInterface;
import org.itxtech.nemisys.network.protocol.mcpe.DataPacket;
import org.itxtech.nemisys.utils.Config;
import org.itxtech.nemisys.utils.MainLogger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Synapse
 * ===============
 * author: boybook
 * EaseCation Network Project
 * nemisys
 * ===============
 */
public class Synapse {

    private Server server;
    private Config config;

    private Map<String, SynapseEntry> synapseEntries = new HashMap<>();

    public Synapse(Server server) {
        this.server = server;
        this.server.getLogger().notice("Enabling Synapse Client...");
        this.config = new Config(new File(server.getFilePath() + "/synapse.yml"), Config.YAML);

        String ip = this.getConfig().getString("server-ip", "127.0.0.1");
        int port = this.getConfig().getInt("server-port", 10305);
        boolean isMainServer = this.getConfig().getBoolean("isMainServer");
        String password = this.getConfig().getString("password");
        String serverDescription = this.getConfig().getString("description");

        for(SourceInterface interfaz : this.getServer().getNetwork().getInterfaces()){
            if(interfaz instanceof RakNetInterface){
                if(this.getConfig().getBoolean("disable-rak")){
                    interfaz.shutdown();
                    break;
                }
            }
        }

        SynapseEntry entry = new SynapseEntry(this, ip, port, isMainServer, password, serverDescription);
        this.addSynapseEntry(entry);

        this.server.getLogger().notice("Enabled Synapse Client");
    }

    public Config getConfig() {
        return config;
    }

    public Server getServer() {
        return server;
    }

    public MainLogger getLogger() {
        return this.server.getLogger();
    }

    public Map<String, SynapseEntry> getSynapseEntries() {
        return synapseEntries;
    }

    public void addSynapseEntry(SynapseEntry entry) {
        this.synapseEntries.put(entry.getHash(), entry);
    }

    public SynapseEntry getSynapseEntry(String hash) {
        return this.synapseEntries.get(hash);
    }

    public DataPacket getPacket(byte[] buffer){
        byte pid = buffer[0];
        byte start = 1;
        if(pid == (byte) 0xfe){
            pid = buffer[1];
            start++;
        }
        DataPacket data = this.getServer().getNetwork().getPacket(pid);
        if(data == null){
            return null;
        }
        data.setBuffer(buffer, start);
        return data;
    }

}
