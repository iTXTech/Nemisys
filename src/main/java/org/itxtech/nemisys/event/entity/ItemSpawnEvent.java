package org.itxtech.nemisys.event.entity;

import org.itxtech.nemisys.entity.item.EntityItem;
import org.itxtech.nemisys.event.HandlerList;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class ItemSpawnEvent extends EntityEvent {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public ItemSpawnEvent(EntityItem item) {
        this.entity = item;
    }

    @Override
    public EntityItem getEntity() {
        return (EntityItem) this.entity;
    }
}
