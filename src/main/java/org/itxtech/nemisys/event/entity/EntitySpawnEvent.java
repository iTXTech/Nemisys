package org.itxtech.nemisys.event.entity;

import org.itxtech.nemisys.entity.Entity;
import org.itxtech.nemisys.entity.EntityCreature;
import org.itxtech.nemisys.entity.EntityHuman;
import org.itxtech.nemisys.entity.item.EntityItem;
import org.itxtech.nemisys.entity.item.EntityVehicle;
import org.itxtech.nemisys.entity.projectile.EntityProjectile;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.level.Position;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntitySpawnEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private int entityType;

    public EntitySpawnEvent(Entity entity) {
        this.entity = entity;
        this.entityType = entity.getNetworkId();
    }

    public Position getPosition() {
        return this.entity.getPosition();
    }

    public int getType() {
        return this.entityType;
    }

    public boolean isCreature() {
        return this.entity instanceof EntityCreature;
    }

    public boolean isHuman() {
        return this.entity instanceof EntityHuman;
    }

    public boolean isProjectile() {
        return this.entity instanceof EntityProjectile;
    }

    public boolean isVehicle() {
        return this.entity instanceof EntityVehicle;
    }

    public boolean isItem() {
        return this.entity instanceof EntityItem;
    }

}
