package org.itxtech.nemisys.synapse.network.synlib;

import org.itxtech.nemisys.math.NemisysMath;
import org.itxtech.nemisys.utils.Binary;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Session {

    private byte[] receiveBuffer = new byte[0];
    //private byte[] sendBuffer = new byte[0];
    private SynapseSocket socket;
    private String ip;
    private int port;
    private SynapseClient server;
    private long lastCheck;
    private boolean connected;

    public Session(SynapseClient server, SynapseSocket socket) {
        this.server = server;
        this.socket = socket;
        this.connected = socket.isConnected();
        if(this.connected) {
            this.ip = socket.getSocket().socket().getInetAddress().getHostAddress();
            this.port = socket.getSocket().socket().getPort();
        }else{ //default
            this.ip = "127.0.0.1";
            this.port = 20000;
        }
        this.lastCheck = System.currentTimeMillis();

        this.run();
    }

    public void run() {
        this.tickProcessor();
    }

    private long tickUseTime = 0;
    private long tickUseNano = 0;

    private void tickProcessor() {
        while (!this.server.isShutdown()) {
            long start = System.currentTimeMillis();
            long startNano = System.nanoTime();
            try {
                this.tick();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        try {
            this.tick();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(this.connected){
            this.socket.close();
        }
    }

    private void tick() throws Exception {
        if (this.update()) {
            this.receivePacket();
            int sendLen = 0;
            do {
                int len = this.sendPacket();
                if (len > 0) {
                    sendLen += len;
                } else {
                    break;
                }
            } while (sendLen < 65535);
        }
    }

    private void receivePacket() throws Exception {
        List<byte[]> packets = this.readPacket();
        for (byte[] packet: packets) {
            if (packet != null && packet.length > 0) {
                this.server.pushThreadToMainPacket(packet);
            }
        }
    }

    private int sendPacket() throws Exception {
        byte[] packet = this.server.readMainToThreadPacket();
        if (packet != null && packet.length > 0) {
            this.writePacket(packet);
            return packet.length;
        }
        return -1;
    }

    public String getHash() {
        return this.getIp() + ":" + this.getPort();
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public SynapseSocket getSocket() {
        return socket;
    }

    public boolean update() throws Exception {
        if (this.server.needReconnect && this.connected) {
            this.connected = false;
            this.server.needReconnect = false;
        }
        if (this.connected) {
            try {
                Selector selector = this.socket.getSelector();
                if (selector.selectNow() > 0) {
                    for (SelectionKey sk : selector.selectedKeys()) {
                        selector.selectedKeys().remove(sk);
                        if (sk.isReadable()) {
                            SocketChannel sc = (SocketChannel) sk.channel();
                            ByteBuffer buff = ByteBuffer.allocate(65535);
                            int n = sc.read(buff);
                            buff.flip();
                            sk.interestOps(SelectionKey.OP_READ);
                            if(n > 0) {
                                byte[] buffer = Arrays.copyOfRange(buff.array(), 0, n);
                                this.receiveBuffer = Binary.appendBytes(this.receiveBuffer, buffer);
                            }
                        }
                    }
                }
                /*if (this.sendBuffer.length > 0) {
                    this.socket.getSocket().write(ByteBuffer.wrap(this.sendBuffer));
                    this.sendBuffer = new byte[0];
                }*/
                return true;
            } catch (IOException e) {
                this.server.getLogger().error("Synapse connection has disconnected unexpectedly");
                this.connected = false;
                this.server.setConnected(false);
                return false;
            }
        } else {
            long time;
            if (((time = System.currentTimeMillis()) - this.lastCheck) >= 3000) {//re-connect
                this.server.getLogger().notice("Trying to re-connect to Synapse Server");
                if (this.socket.connect()) {
                    this.connected = true;
                    this.port = this.socket.getPort();
                    this.server.setConnected(true);
                    this.server.setNeedAuth(true);
                }
                this.lastCheck = time;
            }
            return false;
        }
    }

    public List<byte[]> readPacket() throws Exception {
        List<byte[]> packets = new ArrayList<>();
        if(this.receiveBuffer != null && this.receiveBuffer.length > 0) {
            int len = this.receiveBuffer.length;
            int offset = 0;
            while (offset < len) {
                if (offset > len - 4) break;
                int pkLen = Binary.readInt(Binary.subBytes(this.receiveBuffer, offset, 4));
                offset += 4;

                if(pkLen <= (len - offset)) {
                    byte[] buf = Binary.subBytes(this.receiveBuffer, offset, pkLen);
                    offset += pkLen;

                    packets.add(buf);
                }else{
                    offset -= 4;
                    break;
                }
            }
            if(offset < len){
                this.receiveBuffer = Binary.subBytes(this.receiveBuffer, offset);
            }else{
                this.receiveBuffer = new byte[0];
            }
        }
        return packets;
    }

    public void writePacket(byte[] data) {
        /*byte[] buffer = Binary.appendBytes(Binary.writeInt(data.length), data);
        this.sendBuffer = Binary.appendBytes(this.sendBuffer, buffer);*/
        try {
            this.socket.getSocket().write(ByteBuffer.wrap(Binary.appendBytes(Binary.writeInt(data.length), data)));
        }catch (IOException e){
            //
        }
    }

    public float getTicksPerSecond() {
        long more = this.tickUseTime - 10;
        if (more < 0) return 100;
        return Math.round(10f / (float)this.tickUseTime) * 100;
    }

    public float getTickUsage() {
        return (float) NemisysMath.round((float)this.tickUseNano / 10000f, 2);
    }

}
