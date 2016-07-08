package org.itxtech.nemisys.event.entity;

import org.itxtech.nemisys.block.Block;
import org.itxtech.nemisys.entity.Entity;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;

/**
 * Created on 15-10-26.
 */
public class EntityBlockChangeEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Block from;
    private Block to;

    public EntityBlockChangeEvent(Entity entity, Block from, Block to) {
        this.entity = entity;
        this.from = from;
        this.to = to;
    }

    public Block getFrom() {
        return from;
    }

    public Block getTo() {
        return to;
    }

}
