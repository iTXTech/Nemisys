package org.itxtech.nemisys.event.entity;

import org.itxtech.nemisys.entity.Entity;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;

public class EntityPortalEnterEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static final int TYPE_NETHER = 0;
    public static final int TYPE_END = 1;

    private int type;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityPortalEnterEvent(Entity entity, int type) {
        this.entity = entity;
        this.type = type;
    }

    public int getPortalType() {
        return type;
    }
}
