package org.itxtech.nemisys.event.potion;

import org.itxtech.nemisys.entity.Entity;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.potion.Potion;

/**
 * Created by Snake1999 on 2016/1/12.
 * Package org.itxtech.nemisys.event.potion in project nukkit
 */
public class PotionApplyEvent extends PotionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Entity entity;

    public PotionApplyEvent(Potion potion, Entity entity) {
        super(potion);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

}
