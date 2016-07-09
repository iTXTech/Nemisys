package org.itxtech.nemisys.event.player;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.item.food.Food;

/**
 * Created by Snake1999 on 2016/1/14.
 * Package org.itxtech.nemisys.event.player in project nemisys.
 */
public class PlayerEatFoodEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Food food;

    public static HandlerList getHandlers() {
        return handlers;
    }

    public PlayerEatFoodEvent(Player player, Food food) {
        this.player = player;
        this.food = food;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

}
