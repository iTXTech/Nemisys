package org.itxtech.nemisys.synapse.network.synlib;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.itxtech.nemisys.Server;
import org.itxtech.nemisys.network.protocol.spp.SynapseDataPacket;

import java.net.InetSocketAddress;

/**
 * Handles a server-side channel.
 */
public class SynapseClientHandler extends ChannelInboundHandlerAdapter{

    //static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SynapseClient synapseClient;

    public SynapseClientHandler(SynapseClient synapseClient) {
        this.synapseClient = synapseClient;
    }

    public SynapseClient getSynapseClient() {
        return synapseClient;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {  //客户端启动时调用该方法
        Server.getInstance().getLogger().debug("client-ChannelActive");
        this.getSynapseClient().getSession().channel = ctx.channel();
        InetSocketAddress address = (InetSocketAddress)ctx.channel().remoteAddress();
        this.getSynapseClient().getSession().updateAddress(address);
        this.getSynapseClient().getSession().setConnected(true);
        this.getSynapseClient().setConnected(true);
        Server.getInstance().getLogger().notice("Synapse Client has connected to " + address.getAddress().getHostAddress() + ":" + address.getPort());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Server.getInstance().getLogger().debug("client-ChannelInactive");
        this.getSynapseClient().setConnected(false);
        this.getSynapseClient().reconnect();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof SynapseDataPacket) {
            SynapseDataPacket packet = (SynapseDataPacket) msg;
            this.getSynapseClient().pushThreadToMainPacket(packet);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof Exception) Server.getInstance().getLogger().logException((Exception) cause);
        ctx.close();
        this.getSynapseClient().setConnected(false);
    }
}
