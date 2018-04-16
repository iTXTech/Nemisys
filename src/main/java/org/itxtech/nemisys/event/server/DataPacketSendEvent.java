package org.itxtech.nemisys.event.server;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.network.protocol.mcpe.DataPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class DataPacketSendEvent extends ServerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private DataPacket packet;
    private Player player;

    public DataPacketSendEvent(Player player, DataPacket packet) {
        this.packet = packet;
        this.player = player;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public DataPacket getPacket() {
        return packet;
    }
}
