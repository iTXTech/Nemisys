package org.itxtech.nemisys.network.synlib;

import org.itxtech.nemisys.InterruptibleThread;
import org.itxtech.nemisys.Nemisys;
import org.itxtech.nemisys.network.SynapseInterface;
import org.itxtech.nemisys.utils.ThreadedLogger;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by boybook on 16/6/24.
 */
public class SynapseServer extends Thread implements InterruptibleThread{

    public static final String VERSION = "0.2.1";

    private ThreadedLogger logger;
    private String interfaz;
    private int port;
    private boolean shutdown = false;
    protected ConcurrentLinkedQueue<byte[]> externalQueue;
    protected ConcurrentLinkedQueue<byte[]> internalQueue;
    protected ConcurrentLinkedQueue<byte[]> clientOpenQueue;
    protected ConcurrentLinkedQueue<byte[]> internalClientCloseQueue;
    protected ConcurrentLinkedQueue<byte[]> externalClientCloseQueue;
    private String mainPath;
    private SynapseInterface server;

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

    public ConcurrentLinkedQueue<byte[]> getExternalQueue() {
        return externalQueue;
    }

    public ConcurrentLinkedQueue<byte[]> getInternalQueue() {
        return internalQueue;
    }

    public byte[] getInternalClientCloseRequest(){
        return this.internalClientCloseQueue.poll();
    }

    public void addInternalClientCloseRequest(byte[] hash){
        this.internalClientCloseQueue.add(hash);
    }

    public byte[] getExternalClientCloseRequest(){
        return this.externalClientCloseQueue.poll();
    }

    public void addExternalClientCloseRequest(byte[] hash){
        this.externalClientCloseQueue.add(hash);
    }

    public byte[] getClientOpenRequest(){
        return this.clientOpenQueue.poll();
    }

    public void addClientOpenRequest(byte[] hash){
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

    public void pushMainToThreadPacket(byte[] data) {
        this.internalQueue.add(data);
    }

    public byte[] readMainToThreadPacket() {
        return this.internalQueue.poll();
    }

    public void pushThreadToMainPacket(byte[] data) {
        this.externalQueue.add(data);
    }

    public byte[] readThreadToMainPacket() {
        return this.externalQueue.poll();
    }

    public void run() {
        this.setName("SynLib Thread #" + Thread.currentThread().getId());
        Runtime.getRuntime().addShutdownHook(new ShutdownHandler());
        try {
            SynapseSocket socket = new SynapseSocket(this.getLogger(), this.port, this.interfaz);
            this.sessionManager = new SessionManager(this, socket);
            this.sessionManager.run();
        } catch (Exception e) {
            e.printStackTrace();
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
