package org.itxtech.nemisys.event.level;

import org.itxtech.nemisys.event.Event;
import org.itxtech.nemisys.level.Level;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class LevelEvent extends Event {

    private Level level;

    public LevelEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
