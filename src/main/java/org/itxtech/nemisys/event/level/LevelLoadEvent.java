package org.itxtech.nemisys.event.level;

import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LevelLoadEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public LevelLoadEvent(Level level) {
        super(level);
    }

}
