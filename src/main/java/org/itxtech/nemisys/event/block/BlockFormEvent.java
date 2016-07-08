package org.itxtech.nemisys.event.block;

import org.itxtech.nemisys.block.Block;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockFormEvent extends BlockGrowEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public BlockFormEvent(Block block, Block newState) {
        super(block, newState);
    }

}
