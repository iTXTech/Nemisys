package org.itxtech.nemisys.synapse.network.synlib;

import org.itxtech.nemisys.utils.ThreadedLogger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SynapseSocket {

    private SocketChannel socket;
    private Selector selector = null;
    private ThreadedLogger logger;
    private String interfaz;
    private boolean connected = false;
    private int port;

    public SynapseSocket(ThreadedLogger logger, int port) {
        this(logger, port, "127.0.0.1");
    }

    public SynapseSocket(ThreadedLogger logger, String interfaz) {
        this(logger, 10305, interfaz);
    }

    public SynapseSocket(ThreadedLogger logger) {
        this(logger, 10305, "127.0.0.1");
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
            this.socket = SocketChannel.open(isa);
            this.socket.socket().setSendBufferSize(1024 * 1024 * 3);
            this.socket.socket().setReceiveBufferSize(1024 * 1024 * 3);
            this.socket.configureBlocking(false);
            this.socket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT);
            this.logger.notice("SynapseAPI has connected to " + this.interfaz + ":" + this.port);
            this.connected = true;
        } catch (IOException e) {
            this.logger.critical("Synapse Client can't connect " + this.interfaz + ":" + this.port);
            this.logger.error("Socket error: " + e.getMessage());
            this.connected = false;
            //SynapseAPI.enable = false;
            return false;
        }
        return true;
    }

    public boolean isConnected(){
        return this.connected;
    }

    public Selector getSelector(){
        return this.selector;
    }

    public SocketChannel getSocket() {
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
