package org.itxtech.nemisys.event.weather;

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
