package org.itxtech.nemisys.network.synlib;

import org.itxtech.nemisys.math.NemisysMath;
import org.itxtech.nemisys.utils.Binary;
import org.itxtech.nemisys.utils.MainLogger;
import org.itxtech.nemisys.utils.ThreadedLogger;
import org.itxtech.nemisys.utils.Utils;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    }

    public void run(){
        this.tickProcessor();
    }

    private long tickUseTime = 0;
    private long tickUseNano = 0;

    public void tickProcessor(){
        while(!this.server.isShutdown()){
            long start = System.currentTimeMillis();
            long startNano = System.nanoTime();
            this.tick();
            long time = System.currentTimeMillis() - start;
            this.tickUseTime = time;
            this.tickUseNano = System.nanoTime() - startNano;
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

    private boolean sendPacket(){
        byte[] data = this.server.readMainToThreadPacket();
        if(data != null && data.length > 0){
            int offset = 0;
            int len = data[offset++];
            String hash = new String(Binary.subBytes(data, offset, len), StandardCharsets.UTF_8);
            if(this.sessions.containsKey(hash)){
                offset += len;
                byte[] payload = Binary.subBytes(data, offset);
                this.sessions.get(hash).writePacket(payload);
            }
            return true;
        }
        return false;
    }

    private boolean closeSessions(){
        byte[] data = this.server.getExternalClientCloseRequest();
        if(data != null && data.length > 0){
            String hash = Utils.readClientHash(data);
            if(this.sessions.containsKey(hash)){
                this.sessions.get(hash).close();
                this.sessions.remove(hash);
            }
            return true;
        }
        return false;
    }

    public void tick() {
        try {
            while (this.socket.getSelector().selectNow() > 0) {
                Set readyKeys = this.socket.getSelector().selectedKeys();
                Iterator it = readyKeys.iterator();

                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();
                    if (key.isAcceptable()) {//Connect!
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

            while (this.sendPacket()) ;
            for (Session ses : new ArrayList<>(this.sessions.values())) {
                try {
                    if (ses.process()) {

                    } else {
                        ses.close();
                        this.server.addInternalClientCloseRequest(Utils.writeClientHash(ses.getHash()));
                        this.sessions.remove(ses.getHash());
                    }
                } catch (Exception e) {

                }
            }
            while (this.closeSessions()) ;
        } catch (IOException e) {
            MainLogger.getLogger().logException(e);
        }
    }

    public float getTicksPerSecond() {
        long more = this.tickUseTime - 10;
        if (more < 0) return 100;
        return Math.round(10f / (float)this.tickUseTime) * 100;
    }

    public float getTickUsage() {
        return (float)NemisysMath.round((float)this.tickUseNano / 10000f, 2);
    }

}
