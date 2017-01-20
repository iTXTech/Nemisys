package org.itxtech.nemisys.network.synlib;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.itxtech.nemisys.InterruptibleThread;
import org.itxtech.nemisys.Nemisys;
import org.itxtech.nemisys.Server;
import org.itxtech.nemisys.network.SynapseInterface;
import org.itxtech.nemisys.utils.ThreadedLogger;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SynapseServer extends Thread implements InterruptibleThread{

    public static final String VERSION = "0.3.0";

    private ThreadedLogger logger;
    private String interfaz;
    private int port;
    private boolean shutdown = false;
    protected ConcurrentLinkedQueue<SynapseClientPacket> externalQueue;
    protected ConcurrentLinkedQueue<SynapseClientPacket> internalQueue;
    protected ConcurrentLinkedQueue<String> clientOpenQueue;
    protected ConcurrentLinkedQueue<String> internalClientCloseQueue;
    protected ConcurrentLinkedQueue<String> externalClientCloseQueue;
    private String mainPath;
    private SynapseInterface server;

    public EventLoopGroup bossGroup = new NioEventLoopGroup();
    public EventLoopGroup workerGroup = new NioEventLoopGroup();

    private SessionManager sessionManager;

    public SynapseServer(ThreadedLogger logger, SynapseInterface server, int port) {
        this(logger, server, port, "0.0.0.0");
    }

    public SynapseServer(ThreadedLogger logger, SynapseInterface server, int port, String interfaz) {
        this.logger = logger;
        this.server = server;
        this.interfaz = interfaz;
        this.port = port;
        if (port < 1 || port > 65536) {
            throw new IllegalArgumentException("Invalid port range");
        }
        this.shutdown = false;
        this.externalQueue = new ConcurrentLinkedQueue<>();
        this.internalQueue = new ConcurrentLinkedQueue<>();
        this.clientOpenQueue = new ConcurrentLinkedQueue<>();
        this.internalClientCloseQueue = new ConcurrentLinkedQueue<>();
        this.externalClientCloseQueue = new ConcurrentLinkedQueue<>();
        this.mainPath = Nemisys.PATH;
        this.start();
    }

    public ConcurrentLinkedQueue<SynapseClientPacket> getExternalQueue() {
        return externalQueue;
    }

    public ConcurrentLinkedQueue<SynapseClientPacket> getInternalQueue() {
        return internalQueue;
    }

    public String getInternalClientCloseRequest(){
        return this.internalClientCloseQueue.poll();
    }

    public void addInternalClientCloseRequest(String hash){
        this.internalClientCloseQueue.add(hash);
    }

    public String getExternalClientCloseRequest(){
        return this.externalClientCloseQueue.poll();
    }

    public void addExternalClientCloseRequest(String hash){
        this.externalClientCloseQueue.add(hash);
    }

    public String getClientOpenRequest(){
        return this.clientOpenQueue.poll();
    }

    public void addClientOpenRequest(String hash){
        this.clientOpenQueue.add(hash);
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

    public void pushMainToThreadPacket(SynapseClientPacket data) {
        this.internalQueue.offer(data);
    }

    public SynapseClientPacket readMainToThreadPacket() {
        return this.internalQueue.poll();
    }

    public void pushThreadToMainPacket(SynapseClientPacket data) {
        this.externalQueue.offer(data);
    }

    public SynapseClientPacket readThreadToMainPacket() {
        return this.externalQueue.poll();
    }

    public void run() {
        this.setName("SynLib Thread #" + Thread.currentThread().getId());
        Runtime.getRuntime().addShutdownHook(new ShutdownHandler());
        try {
            this.sessionManager = new SessionManager(this);
            if (this.bind()) {
                this.sessionManager.run();
            } else {
                Server.getInstance().shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean bind() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();  //服务引导程序，服务器端快速启动程序
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new SynapseServerInitializer(this.sessionManager));

            b.bind(this.interfaz, this.port).get();
            // 等待服务端监听端口关闭，等待服务端链路关闭之后main函数才退出
            //uture.channel().closeFuture().sync();
            return true;
        } catch (Exception e) {
            Server.getInstance().getLogger().alert("Synapse Server can't bind to: " + this.interfaz + ":" + this.port);
            Server.getInstance().getLogger().alert("Reason: " + e.getLocalizedMessage());
            Server.getInstance().getLogger().warning("Server will shutdown.");
            return false;
        }
    }

    private class ShutdownHandler extends Thread {
        public void run() {
            if (!shutdown) {
                logger.emergency("SynLib crashed!");
            }
        }
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
