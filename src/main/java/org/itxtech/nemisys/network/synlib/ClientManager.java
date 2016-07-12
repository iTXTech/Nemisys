package org.itxtech.nemisys.network.synlib;

import org.itxtech.nemisys.utils.MainLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: PeratX
 * Nemisys Project
 */
public class ClientManager {
    private SynapseServer server;
    private SynapseSocket socket;
    private Map<String, ClientConnection>clients = new HashMap<>();

    public ClientManager(SynapseServer server, SynapseSocket socket){
        this.server = server;
        this.socket = socket;
        this.run();
    }

    public void run(){
        this.tickProcessor();
    }

    public void tickProcessor(){
        while(!this.server.isShutdown()){
            long start = System.currentTimeMillis();
            this.tick();
            long time = System.currentTimeMillis() - start;
            if(time < 10){
                try {
                    Thread.sleep(10 - time);
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        }
        this.tick();
        for(ClientConnection connection : this.clients.values()) {
            connection.close();
        }
        this.socket.close();;
    }

    public Map<String, ClientConnection> getClients(){
        return this.clients;
    }

    public SynapseServer getServer(){
        return this.server;
    }

    public void tick(){
        try{

        }catch (Exception e){
            MainLogger.getLogger().logException(e);
        }
    }
}
