package org.itxtech.nemisys.network.synlib;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.itxtech.nemisys.Server;
import org.itxtech.nemisys.math.NemisysMath;
import org.itxtech.nemisys.utils.MainLogger;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * Author: PeratX
 * Nemisys Project
 */
public class SessionManager {
    private SynapseServer server;
    private Map<String, Channel> sessions = new HashMap<>();

    public SessionManager(SynapseServer server){
        this.server = server;
    }

    public void run(){
        this.tickProcessor();
        for(Channel channel: this.sessions.values()) {
            channel.close();
        }
    }

    private long nextTick;
    private int tickCounter;
    private float[] tickAverage = {100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
    private float[] useAverage = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private float maxTick = 100;
    private float maxUse = 0;

    public void tickProcessor(){
        this.nextTick = System.currentTimeMillis();
        while (!this.server.isShutdown()) {
            try {
                this.tick();
            } catch (RuntimeException e) {
                Server.getInstance().getLogger().logException(e);
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Server.getInstance().getLogger().logException(e);
            }
        }
        this.server.bossGroup.shutdownGracefully();
        this.server.workerGroup.shutdownGracefully();
    }

    public Map<String, Channel> getSessions(){
        return this.sessions;
    }

    public SynapseServer getServer(){
        return this.server;
    }

    private boolean sendPacket(){
        SynapseClientPacket data = this.server.readMainToThreadPacket();
        if(data != null){
            String hash = data.getHash();
            if(this.sessions.containsKey(hash)){
                this.sessions.get(hash).writeAndFlush(data.getPacket());
                Server.getInstance().getLogger().debug("server-writeAndFlush: hash=" + hash);
            }
            return true;
        }
        return false;
    }

    private boolean closeSessions(){
        String hash = this.server.getExternalClientCloseRequest();
        if(hash != null){
            if(this.sessions.containsKey(hash)){
                this.sessions.get(hash).close();
                this.sessions.remove(hash);
            }
            return true;
        }
        return false;
    }

    public void tick() {
        long tickTime = System.currentTimeMillis();
        long tickTimeNano = System.nanoTime();
        if ((tickTime - this.nextTick) < -5) {
            return;
        }

        ++this.tickCounter;
        try {
            while (this.sendPacket());
            while (this.closeSessions()) ;
        } catch (Exception e) {
            MainLogger.getLogger().logException(e);
        }

        if ((this.tickCounter & 0b1111) == 0) {
            this.maxTick = 100;
            this.maxUse = 0;
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

    public static String getChannelHash(Channel channel) {
        InetSocketAddress address = (InetSocketAddress)channel.remoteAddress();
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

}
