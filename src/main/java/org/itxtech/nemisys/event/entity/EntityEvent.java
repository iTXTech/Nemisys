package org.itxtech.nemisys.event.entity;

import org.itxtech.nemisys.entity.Entity;
import org.itxtech.nemisys.event.Event;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EntityEvent extends Event {

    protected Entity entity;

    public Entity getEntity() {
        return entity;
    }
}
