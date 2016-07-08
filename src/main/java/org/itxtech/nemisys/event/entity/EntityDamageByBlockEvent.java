package org.itxtech.nemisys.event.entity;

import org.itxtech.nemisys.block.Block;
import org.itxtech.nemisys.entity.Entity;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDamageByBlockEvent extends EntityDamageEvent {

    private Block damager;

    public EntityDamageByBlockEvent(Block damager, Entity entity, int cause, float damage) {
        super(entity, cause, damage);
        this.damager = damager;
    }

    public Block getDamager() {
        return damager;
    }

}
