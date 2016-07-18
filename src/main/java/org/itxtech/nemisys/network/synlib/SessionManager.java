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

    public void tickProcessor(){
        while(!this.server.isShutdown()){
            //long start = System.currentTimeMillis();
            this.tick();
            //long time = System.currentTimeMillis() - start;
            //if(time < 10){
                try {
                    //Thread.sleep(10 - time);
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    //ignore
                }
            //}
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

    private int tickCounter;
    private long nextTick;
    private float[] tickAverage = {100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
    private float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private float maxTick = 100;
    private float maxUse = 0;

    public void tick() {
        long tickTime = System.currentTimeMillis();
        long tickTimeNano = System.nanoTime();
        if ((tickTime - this.nextTick) < -5) {
            return;
        }

        ++this.tickCounter;
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

        //long now = System.currentTimeMillis();
        long nowNano = System.nanoTime();
        //float tick = Math.min(100, 1000 / Math.max(1, now - tickTime));
        //float use = Math.min(1, (now - tickTime) / 50);

        float tick = (float) Math.min(100, 1000000000 / Math.max(1000000, ((double) nowNano - tickTimeNano)));
        float use = (float) Math.min(1, ((double) (nowNano - tickTimeNano)) / 50000000);

        if (this.maxTick > tick) {
            this.maxTick = tick;
        }

        if (this.maxUse < use) {
            this.maxUse = use;
        }

        System.arraycopy(this.tickAverage, 1, this.tickAverage, 0, this.tickAverage.length - 1);
        this.tickAverage[this.tickAverage.length - 1] = tick;

        System.arraycopy(this.useAverage, 1, this.useAverage, 0, this.useAverage.length - 1);
        this.useAverage[this.useAverage.length - 1] = use;

        if ((this.nextTick - tickTime) < -1000) {
            this.nextTick = tickTime;
        } else {
            this.nextTick += 10;
        }
    }

    public int getTick() {
        return tickCounter;
    }

    public float getTicksPerSecond() {
        return ((float) Math.round(this.maxTick * 100)) / 100;
    }

    public float getTicksPerSecondAverage() {
        float sum = 0;
        int count = this.tickAverage.length;
        for (float aTickAverage : this.tickAverage) {
            sum += aTickAverage;
        }
        return (float) NemisysMath.round(sum / count, 2);
    }

    public float getTickUsage() {
        return (float) NemisysMath.round(this.maxUse * 100, 2);
    }

    public float getTickUsageAverage() {
        float sum = 0;
        int count = this.useAverage.length;
        for (float aUseAverage : this.useAverage) {
            sum += aUseAverage;
        }
        return ((float) Math.round(sum / count * 100)) / 100;
    }

}
