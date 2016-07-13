package org.itxtech.nemisys.network.synlib;

import org.itxtech.nemisys.utils.Binary;
import org.itxtech.nemisys.utils.Util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Created by boybook on 16/6/24.
 */
public class Session {
    public static final byte[] MAGIC_BYTES = new byte[]{
            (byte) 0x35, (byte) 0xac, (byte) 0x66, (byte) 0xbf
    };

    private byte[] receiveBuffer = new byte[0];
    private byte[] sendBuffer = new byte[0];
    private SessionManager sessionManager;
    private SocketChannel socket;
    private Selector selector;
    private String ip;
    private int port;
    private String magicBytes;

    public Session(SessionManager sessionManager, SocketChannel socket, Selector selector) {
        this.sessionManager = sessionManager;
        this.socket = socket;
        this.selector = selector;
        this.ip = socket.socket().getInetAddress().getHostAddress();
        this.port = socket.socket().getPort();

        this.magicBytes = Util.bytesToHexString(MAGIC_BYTES);
    }

    public void close(){
        try{
            this.socket.close();
        }catch (IOException e){
            //Ignore
        }
    }

    private boolean process() throws Exception {
        if (this.update()) {
            while (this.receivePacket()) ;
            while (this.sendPacket()) ;
            return true;
        }
        return false;
    }

    private boolean receivePacket() throws Exception {
        byte[] packet = this.readPacket();
        if (packet != null && packet.length > 0) {
            this.sessionManager.getServer().pushThreadToMainPacket(packet);
            return true;
        }
        return false;
    }

    private boolean sendPacket() throws Exception {
        byte[] packet = this.sessionManager.getServer().readMainToThreadPacket();
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

    public SocketChannel getSocket() {
        return socket;
    }

    public boolean update() throws Exception {
        try{
            if (this.selector.selectNow() > 0) {
                for (SelectionKey sk : this.selector.selectedKeys()) {
                    this.selector.selectedKeys().remove(sk);
                    if (sk.isReadable()) {
                        SocketChannel sc = (SocketChannel) sk.channel();
                        ByteBuffer buff = ByteBuffer.allocate(65535);
                        int n = sc.read(buff);
                        buff.flip();
                        sk.interestOps(SelectionKey.OP_READ);
                        if(n > 0) {
                            byte[] buffer = Arrays.copyOfRange(buff.array(), 0, n);
                            this.receiveBuffer = Binary.appendBytes(buffer, this.receiveBuffer);
                        }
                    }
                }
            }
            if (this.sendBuffer.length > 0) {
                this.socket.write(ByteBuffer.wrap(this.sendBuffer));
                this.sendBuffer = new byte[0];
            }
            return true;
        }catch (IOException e){
            return false;
        }
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
        byte[] buffer = Util.concatByte(Binary.writeLInt(data.length), data, Session.MAGIC_BYTES);
        this.sendBuffer = Binary.appendBytes(buffer, this.sendBuffer);
    }

}
