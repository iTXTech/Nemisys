package org.itxtech.nemisys.event.entity;

import org.itxtech.nemisys.entity.Entity;
import org.itxtech.nemisys.entity.projectile.EntityProjectile;
import org.itxtech.nemisys.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ProjectileHitEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ProjectileHitEvent(EntityProjectile entity) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return super.getEntity();
    }
}
