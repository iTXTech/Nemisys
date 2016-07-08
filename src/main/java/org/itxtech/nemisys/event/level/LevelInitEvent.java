package org.itxtech.nemisys.event.level;

import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LevelInitEvent extends LevelEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public LevelInitEvent(Level level) {
        super(level);
    }

}
