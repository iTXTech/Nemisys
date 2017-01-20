package org.itxtech.nemisys.network.synlib;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.itxtech.nemisys.Server;
import org.itxtech.nemisys.network.protocol.spp.SynapseDataPacket;

/**
 * Handles a server-side channel.
 */
public class SynapseServerHandler extends ChannelInboundHandlerAdapter{

    //static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SessionManager sessionManager;

    public SynapseServerHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {  //客户端启动时调用该方法
        String hash = SessionManager.getChannelHash(ctx.channel());
        Server.getInstance().getLogger().debug("server-ChannelActive: hash=" + hash);
        this.getSessionManager().getSessions().put(hash, ctx.channel());
        this.getSessionManager().getServer().addClientOpenRequest(hash);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String hash = SessionManager.getChannelHash(ctx.channel());
        Server.getInstance().getLogger().debug("server-ChannelInactive: hash=" + hash);
        this.getSessionManager().getServer().addInternalClientCloseRequest(hash);
        this.getSessionManager().getSessions().remove(hash);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof SynapseDataPacket) {
            SynapseDataPacket packet = (SynapseDataPacket) msg;
            String hash = SessionManager.getChannelHash(ctx.channel());
            Server.getInstance().getLogger().debug("server-ChannelRead: hash=" + hash + " pk=" + packet.getClass().getSimpleName() + " pkLen=" + packet.getBuffer().length);
            this.getSessionManager().getServer().pushThreadToMainPacket(new SynapseClientPacket(hash, packet));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
