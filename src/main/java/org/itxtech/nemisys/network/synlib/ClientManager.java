package org.itxtech.nemisys.network.synlib;

import org.itxtech.nemisys.utils.MainLogger;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Author: PeratX
 * Nemisys Project
 */
public class ClientManager {
    private SynapseServer server;
    private SynapseSocket socket;
    private Map<String, ClientSession>clients = new HashMap<>();

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
        for(ClientSession connection : this.clients.values()) {
            connection.close();
        }
        this.socket.close();;
    }

    public Map<String, ClientSession> getClients(){
        return this.clients;
    }

    public SynapseServer getServer(){
        return this.server;
    }

    public void tick(){
        try{
            while(this.socket.getSelector().selectNow() > 0){
                Set readyKeys = this.socket.getSelector().selectedKeys();
                Iterator it = readyKeys.iterator();

                while(it.hasNext()){
                    SelectionKey key = (SelectionKey) it.next();
                    if(key.isAcceptable()){//Connect!

                    }
                }
            }
        }catch (IOException e){
            MainLogger.getLogger().logException(e);
        }
    }
}
