package org.itxtech.nemisys.network.synlib;

import org.itxtech.nemisys.utils.ThreadedLogger;

import java.net.*;
import java.nio.channels.*;
import java.io.*;

/**
 * Created by boybook on 16/6/24.
 */
public class SynapseSocket {

    private ServerSocketChannel socket;
    private Selector selector = null;
    private ThreadedLogger logger;
    private String interfaz;
    private boolean connected = false;
    private int port;

    public SynapseSocket(ThreadedLogger logger, int port) {
        this(logger, port, "0.0.0.0");
    }

    public SynapseSocket(ThreadedLogger logger, String interfaz) {
        this(logger, 10305, interfaz);
    }

    public SynapseSocket(ThreadedLogger logger) {
        this(logger, 10305, "0.0.0.0");
    }

    public SynapseSocket(ThreadedLogger logger, int port, String interfaz) {
        this.logger = logger;
        this.interfaz = interfaz;
        this.port = port;
        this.connect();
    }

    public boolean connect() {
        try {
            this.selector = Selector.open();
            InetSocketAddress isa = new InetSocketAddress(this.interfaz, this.port);
            this.socket = ServerSocketChannel.open();
            this.socket.socket().setReuseAddress(true);
            this.socket.configureBlocking(false);
            this.socket.socket().bind(isa);
            this.socket.register(selector, SelectionKey.OP_ACCEPT);
            this.logger.notice("SynapseAPI has connected to " + this.interfaz + ":" + this.port);
            this.connected = true;
        } catch (IOException e) {
            this.logger.critical("Synapse Server can't bind to " + this.interfaz + ":" + this.port);
            this.logger.error("Socket error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Selector getSelector(){
        return this.selector;
    }

    public ServerSocketChannel getSocket() {
        return this.socket;
    }

    public int getPort() {
        return this.port;
    }

    public void close() {
        try {
            if(this.connected){
                this.socket.close();
            }
        } catch (IOException e) {
            this.logger.critical("Synapse Client can't close!");
        }

    }
}
