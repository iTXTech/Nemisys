package org.itxtech.nemisys.event.level;

import org.itxtech.nemisys.event.Event;
import org.itxtech.nemisys.level.Level;

/**
 * author: funcraft
 * Nukkit Project
 */
public abstract class WeatherEvent extends Event {

    private Level level;

    public WeatherEvent(Level level) {
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }
}
