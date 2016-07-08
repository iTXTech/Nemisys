package org.itxtech.nemisys.event.level;

import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.level.format.FullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ChunkPopulateEvent extends ChunkEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ChunkPopulateEvent(FullChunk chunk) {
        super(chunk);
    }

}