package org.itxtech.nemisys.event.level;

import org.itxtech.nemisys.level.format.FullChunk;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class ChunkEvent extends LevelEvent {

    private FullChunk chunk;

    public ChunkEvent(FullChunk chunk) {
        super(chunk.getProvider().getLevel());
        this.chunk = chunk;
    }

    public FullChunk getChunk() {
        return chunk;
    }
}
