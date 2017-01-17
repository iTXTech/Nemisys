package org.itxtech.nemisys.synapse.network.synlib;

import org.itxtech.nemisys.utils.ThreadedLogger;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by boybook on 16/6/24.
 */
public class SynapseClient extends Thread {

    public static final String VERSION = "0.2.1";

    private ThreadedLogger logger;
    private String interfaz;
    private int port;
    private boolean shutdown = false;
    protected ConcurrentLinkedQueue<byte[]> externalQueue;
    protected ConcurrentLinkedQueue<byte[]> internalQueue;
    private boolean needAuth = false;
    private boolean connected = true;
    public boolean needReconnect = false;

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

    public ConcurrentLinkedQueue<byte[]> getExternalQueue() {
        return externalQueue;
    }

    public ConcurrentLinkedQueue<byte[]> getInternalQueue() {
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

    public void pushMainToThreadPacket(byte[] data) {
        this.internalQueue.offer(data);
    }

    public byte[] readMainToThreadPacket() {
        return this.internalQueue.poll();
    }

    public int getInternalQueueSize() {
        return this.internalQueue.size();
    }

    public void pushThreadToMainPacket(byte[] data) {
        this.externalQueue.offer(data);
    }

    public byte[] readThreadToMainPacket() {
        return this.externalQueue.poll();
    }

    public void run() {
        this.setName("SynLib Client Thread #" + Thread.currentThread().getId());
        Runtime.getRuntime().addShutdownHook(new ShutdownHandler());
        try {
            SynapseSocket socket = new SynapseSocket(this.getLogger(), this.port, this.interfaz);
            new Session(this, socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ShutdownHandler extends Thread {
        public void run() {
            if (!shutdown) {
                logger.emergency("SynLib Client crashed!");
            }
        }
    }

}
