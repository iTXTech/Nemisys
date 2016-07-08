package org.itxtech.nemisys.event.redstone;

import org.itxtech.nemisys.block.Block;
import org.itxtech.nemisys.event.block.BlockUpdateEvent;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class RedstoneUpdateEvent extends BlockUpdateEvent {

    public RedstoneUpdateEvent(Block source) {
        super(source);
    }

}

