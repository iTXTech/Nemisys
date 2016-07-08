package org.itxtech.nemisys.event.entity;

import org.itxtech.nemisys.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityCombustByEntityEvent extends EntityCombustEvent {

    protected Entity combuster;

    public EntityCombustByEntityEvent(Entity combuster, Entity combustee, int duration) {
        super(combustee, duration);
        this.combuster = combuster;
    }

    public Entity getCombuster() {
        return combuster;
    }
}
