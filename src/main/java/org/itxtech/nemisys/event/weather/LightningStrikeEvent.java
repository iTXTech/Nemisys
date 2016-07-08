package org.itxtech.nemisys.event.weather;

import org.itxtech.nemisys.entity.weather.EntityLightningStrike;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.level.Level;

/**
 * author: funcraft
 * Nukkit Project
 */
public class LightningStrikeEvent extends WeatherEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final EntityLightningStrike bolt;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public LightningStrikeEvent(Level level, final EntityLightningStrike bolt) {
        super(level);
        this.bolt = bolt;
    }

    /**
     * * Gets the bolt which is striking the earth.
     * * @return lightning entity
     */
    public EntityLightningStrike getLightning() {
        return bolt;
    }

}
