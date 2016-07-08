package org.itxtech.nemisys.event.entity;

import org.itxtech.nemisys.entity.Entity;
import org.itxtech.nemisys.entity.EntityLiving;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EntityDeathEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Item[] drops = new Item[0];

    public EntityDeathEvent(EntityLiving entity) {
        this(entity, new Item[0]);
    }

    public EntityDeathEvent(EntityLiving entity, Item[] drops) {
        this.entity = entity;
        this.drops = drops;
    }

    @Override
    public Entity getEntity() {
        return super.getEntity();
    }

    public Item[] getDrops() {
        return drops;
    }

    public void setDrops(Item[] drops) {
        this.drops = drops;
    }
}
