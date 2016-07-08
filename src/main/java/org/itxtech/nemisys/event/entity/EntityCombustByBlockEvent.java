package org.itxtech.nemisys.event.entity;

import org.itxtech.nemisys.block.Block;
import org.itxtech.nemisys.entity.Entity;

/**
 * author: Box
 * Nukkit Project
 */
public class EntityCombustByBlockEvent extends EntityCombustEvent {

    protected Block combuster;

    public EntityCombustByBlockEvent(Block combuster, Entity combustee, int duration) {
        super(combustee, duration);
        this.combuster = combuster;
    }

    public Block getCombuster() {
        return combuster;
    }
}
