package org.itxtech.nemisys.event.block;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.block.Block;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SignChangeEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Player player;

    private String[] lines = new String[4];

    public SignChangeEvent(Block block, Player player, String[] lines) {
        super(block);
        this.player = player;
        this.lines = lines;
    }

    public Player getPlayer() {
        return player;
    }

    public String[] getLines() {
        return lines;
    }

    public String getLine(int index) {
        return this.lines[index];
    }

    public void setLine(int index, String line) {
        this.lines[index] = line;
    }
}
