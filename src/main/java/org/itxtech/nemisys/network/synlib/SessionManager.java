package org.itxtech.nemisys.network.synlib;

import org.itxtech.nemisys.utils.MainLogger;
import org.itxtech.nemisys.utils.Utils;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Author: PeratX
 * Nemisys Project
 */
public class SessionManager {
    private SynapseServer server;
    private SynapseSocket socket;
    private Map<String, Session> sessions = new HashMap<>();

    public SessionManager(SynapseServer server, SynapseSocket socket){
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
        for(Session connection : this.sessions.values()) {
            connection.close();
        }
        this.socket.close();
    }

    public Map<String, Session> getSessions(){
        return this.sessions;
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
                    it.remove();
                    if(key.isAcceptable()){//Connect!
                        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = ssc.accept();
                        socketChannel.configureBlocking(false);
                        Selector selector = Selector.open();
                        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT);
                        Session session = new Session(this, socketChannel, selector);
                        this.sessions.put(session.getHash(), session);
                        this.server.addClientOpenRequest(Utils.writeClientHash(session.getHash()));
                    }
                }
            }
        }catch (IOException e){
            MainLogger.getLogger().logException(e);
        }
    }
}
