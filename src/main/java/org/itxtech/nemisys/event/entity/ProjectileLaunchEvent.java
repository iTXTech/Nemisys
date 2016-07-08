package org.itxtech.nemisys.event.entity;

import org.itxtech.nemisys.entity.projectile.EntityProjectile;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;

public class ProjectileLaunchEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ProjectileLaunchEvent(EntityProjectile entity) {
        this.entity = entity;
    }

    public EntityProjectile getEntity() {
        return (EntityProjectile) this.entity;
    }
}
