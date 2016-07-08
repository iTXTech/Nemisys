package org.itxtech.nemisys.event.potion;

import org.itxtech.nemisys.entity.item.EntityPotion;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.potion.Potion;

/**
 * Created by Snake1999 on 2016/1/12.
 * Package org.itxtech.nemisys.event.potion in project nukkit
 */
public class PotionCollideEvent extends PotionEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private EntityPotion thrownPotion;

    public PotionCollideEvent(Potion potion, EntityPotion thrownPotion) {
        super(potion);
        this.thrownPotion = thrownPotion;
    }

    public EntityPotion getThrownPotion() {
        return thrownPotion;
    }
}
