package org.itxtech.nemisys.network.synlib;

import org.itxtech.nemisys.Server;
import org.itxtech.nemisys.utils.Binary;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Session {

    private byte[] receiveBuffer = new byte[0];
    //private byte[] sendBuffer = new byte[0];
    private SessionManager sessionManager;
    private SocketChannel socket;
    private Selector selector;
    private String ip;
    private int port;

    public Session(SessionManager sessionManager, SocketChannel socket, Selector selector) {
        this.sessionManager = sessionManager;
        this.socket = socket;
        this.selector = selector;
        this.ip = socket.socket().getInetAddress().getHostAddress();
        this.port = socket.socket().getPort();
    }

    public void close(){
        try{
            this.socket.close();
        }catch (IOException e){
            //Ignore
        }
    }

    public boolean process() throws Exception {
        if (this.update()) {
            this.receivePacket();
            return true;
        }
        return false;
    }

    private void receivePacket() throws Exception {
        List<byte[]> packets = this.readPacket();
        for (byte[] packet: packets) {
            if (packet != null && packet.length > 0) {
                Server.getInstance().getLogger().debug("[SynapseInterface Got packet form SynLib] hash=" + this.getHash() + " pkLen=" + packet.length + " pkID=" + packet[0]);
                byte[] buffer = Binary.appendBytes(
                        new byte[]{(byte) (this.getHash().length() & 0xff)},
                        this.getHash().getBytes(StandardCharsets.UTF_8),
                        packet);
                this.sessionManager.getServer().pushThreadToMainPacket(buffer);
            }
        }
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
                        ByteBuffer buff = ByteBuffer.allocate(1024 * 64);
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
                this.socket.write(ByteBuffer.wrap(this.sendBuffer));
                this.sendBuffer = new byte[0];
            }*/
            return true;
        }catch (IOException e){
            return false;
        }
    }

    public List<byte[]> readPacket() throws Exception {
        List<byte[]> packets = new ArrayList<>();
        if(this.receiveBuffer != null && this.receiveBuffer.length > 0) {
            int len = this.receiveBuffer.length;
            Server.getInstance().getLogger().debug("readPacket >> this.receiveBuffer.length=" + len);
            int offset = 0;
            while (offset < len) {
                if (offset > len - 4) break;
                int pkLen = Binary.readInt(Binary.subBytes(this.receiveBuffer, offset, 4));
                Server.getInstance().getLogger().debug("readPacket >> Next pkLen=" + pkLen);
                offset += 4;

                if(pkLen <= (len - offset)) {
                    byte[] buf = Binary.subBytes(this.receiveBuffer, offset, pkLen);
                    offset += pkLen;

                    packets.add(buf);
                }else{
                    offset -= 4;
                    Server.getInstance().getLogger().debug("readPacket >> The packet is cut off in offset=" + offset);
                    break;
                }
            }
            if (offset < len){
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
            this.socket.write(ByteBuffer.wrap(Binary.appendBytes(Binary.writeInt(data.length), data)));
        }catch (IOException e){
            //
        }
    }

}
