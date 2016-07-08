package org.itxtech.nemisys.event.block;

import org.itxtech.nemisys.block.Block;
import org.itxtech.nemisys.event.Event;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BlockEvent extends Event {

    protected Block block;

    public BlockEvent(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
