package org.itxtech.nemisys.network.synlib;

import org.itxtech.nemisys.utils.Binary;
import org.itxtech.nemisys.utils.Util;

import java.util.Arrays;

/**
 * Created by boybook on 16/6/24.
 */
public class ClientConnection {
    public static final byte[] MAGIC_BYTES = new byte[]{
            (byte) 0x35, (byte) 0xac, (byte) 0x66, (byte) 0xbf
    };

    private byte[] receiveBuffer = new byte[0];
    private byte[] sendBuffer = new byte[0];
    private SynapseSocket socket;
    private String ip;
    private int port;
    private SynapseServer server;
    private long lastCheck;
    private boolean connected;
    private String magicBytes;

    public ClientConnection(SynapseServer server, SynapseSocket socket) {
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

        this.magicBytes = Util.bytesToHexString(MAGIC_BYTES);

        this.run();
    }

    public void run() {
        this.tickProcessor();
    }

    private void tickProcessor() {
        while (!this.server.isShutdown()) {
            long start = System.currentTimeMillis();
            try {
                this.tick();
            } catch (Exception e) {
                Server.getInstance().getLogger().logException(e);
            }

            long time = System.currentTimeMillis();
            if (time - start < 1) {  //todo TPS ???
                try {
                    Thread.sleep(1 - time + start);
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        }
        try {
            this.tick();
        } catch (Exception e) {
            Server.getInstance().getLogger().logException(e);
        }
        if(this.connected){
            this.socket.close();
        }
    }

    private void tick() throws Exception {
        if (this.update()) {
            while (this.receivePacket()) ;
            while (this.sendPacket()) ;
        }
    }

    private boolean receivePacket() throws Exception {
        byte[] packet = this.readPacket();
        if (packet != null && packet.length > 0) {
            this.server.pushThreadToMainPacket(packet);
            return true;
        }
        return false;
    }

    private boolean sendPacket() throws Exception {
        byte[] packet = this.server.readMainToThreadPacket();
        if (packet != null && packet.length > 0) {
            this.writePacket(packet);
            return true;
        }
        return false;
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

    }

    public byte[] readPacket() throws Exception {
        String str = Util.bytesToHexString(this.receiveBuffer);
        if(str != null) {
            String[] arr = str.split(this.magicBytes, 2);
            if (arr.length <= 2) {
                if (arr.length == 1) {
                    if (arr[0].endsWith(this.magicBytes)) {
                        this.receiveBuffer = new byte[0];
                        arr[0] = arr[0].substring(arr[0].length() - this.magicBytes.length(), arr[0].length());
                    }else{
                        return new byte[0];
                    }
                } else {
                    byte[] newBuffer = Util.hexStringToBytes(arr[1]);
                    if(newBuffer != null){
                        this.receiveBuffer = newBuffer;
                    }else{
                        this.receiveBuffer = new byte[0];
                    }
                }
                byte[] buffer;
                buffer = Util.hexStringToBytes(arr[0]);
                if (buffer.length < 4) {
                    return new byte[0];
                }
                int len = Binary.readLInt(Arrays.copyOfRange(buffer, 0, 4));
                byte[] real = Arrays.copyOfRange(buffer, 4, buffer.length);
                if (len != real.length) {
                    throw new Exception("Wrong packet buffer");
                }
                return real;
            }
        }
        return new byte[0];
    }

    public void writePacket(byte[] data) {
        byte[] buffer = Util.concatByte(Binary.writeLInt(data.length), data, ClientConnection.MAGIC_BYTES);
        this.sendBuffer = Binary.appendBytes(buffer, this.sendBuffer);
    }

}
