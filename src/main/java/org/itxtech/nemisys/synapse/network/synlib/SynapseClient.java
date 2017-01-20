package org.itxtech.nemisys.synapse.network.synlib;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.itxtech.nemisys.Server;
import org.itxtech.nemisys.math.NemisysMath;
import org.itxtech.nemisys.network.protocol.spp.SynapseDataPacket;
import org.itxtech.nemisys.network.synlib.SynapseClientPacket;
import org.itxtech.nemisys.network.synlib.SynapseServerInitializer;
import org.itxtech.nemisys.utils.MainLogger;
import org.itxtech.nemisys.utils.ThreadedLogger;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

/**
 * Created by boybook on 16/6/24.
 */
public class SynapseClient extends Thread {

    public static final String VERSION = "0.3.0";

    private ThreadedLogger logger;
    private String interfaz;
    private int port;
    private boolean shutdown = false;
    protected ConcurrentLinkedQueue<SynapseDataPacket> externalQueue;
    protected ConcurrentLinkedQueue<SynapseDataPacket> internalQueue;
    private boolean needAuth = true;
    private boolean connected = false;
    public boolean needReconnect = false;

    private EventLoopGroup clientGroup;
    private Session session;

    public SynapseClient(ThreadedLogger logger, int port) {
        this(logger, port, "127.0.0.1");
    }

    public SynapseClient(ThreadedLogger logger, int port, String interfaz) {
        this.logger = logger;
        this.interfaz = interfaz;
        this.port = port;
        if (port < 1 || port > 65536) {
            throw new IllegalArgumentException("Invalid port range");
        }
        this.shutdown = false;
        this.externalQueue = new ConcurrentLinkedQueue<>();
        this.internalQueue = new ConcurrentLinkedQueue<>();

        this.start();
    }

    public void reconnect() {
        this.needReconnect = true;
    }

    public boolean isNeedAuth() {
        return needAuth;
    }

    public void setNeedAuth(boolean needAuth) {
        this.needAuth = needAuth;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public ConcurrentLinkedQueue<SynapseDataPacket> getExternalQueue() {
        return externalQueue;
    }

    public ConcurrentLinkedQueue<SynapseDataPacket> getInternalQueue() {
        return internalQueue;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void shutdown() {
        this.shutdown = true;
    }

    public int getPort() {
        return port;
    }

    public String getInterface() {
        return interfaz;
    }

    public ThreadedLogger getLogger() {
        return logger;
    }

    public void quit() {
        this.shutdown();
    }

    public void pushMainToThreadPacket(SynapseDataPacket data) {
        this.internalQueue.offer(data);
    }

    public SynapseDataPacket readMainToThreadPacket() {
        return this.internalQueue.poll();
    }

    public int getInternalQueueSize() {
        return this.internalQueue.size();
    }

    public void pushThreadToMainPacket(SynapseDataPacket data) {
        this.externalQueue.offer(data);
    }

    public SynapseDataPacket readThreadToMainPacket() {
        return this.externalQueue.poll();
    }

    public Session getSession() {
        return session;
    }

    public void run() {
        this.setName("SynLib Client Thread #" + Thread.currentThread().getId());
        Runtime.getRuntime().addShutdownHook(new ShutdownHandler());
        try {
            this.session = new Session(this);
            this.connect();
            this.session.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean connect() {
        clientGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();  //服务引导程序，服务器端快速启动程序
            b.group(clientGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new SynapseClientInitializer(this));

            b.connect(this.interfaz, this.port).get();
            // 等待服务端监听端口关闭，等待服务端链路关闭之后main函数才退出
            //future.channel().closeFuture().sync();
            return true;
        } catch (Exception e) {
            Server.getInstance().getLogger().alert("Synapse Client can't connect to server: " + this.interfaz + ":" + this.port);
            Server.getInstance().getLogger().alert("Reason: " + e.getLocalizedMessage());
            Server.getInstance().getLogger().warning("We will reconnect in 3 seconds");
            this.reconnect();
            return false;
        }
    }

    public EventLoopGroup getClientGroup() {
        return clientGroup;
    }

    public class ShutdownHandler extends Thread {
        public void run() {
            if (!shutdown) {
                logger.emergency("SynLib Client crashed!");
            }
        }
    }

}
