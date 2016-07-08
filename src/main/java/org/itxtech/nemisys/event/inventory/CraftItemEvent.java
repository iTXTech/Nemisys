package org.itxtech.nemisys.event.inventory;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.Event;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.inventory.Recipe;
import org.itxtech.nemisys.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class CraftItemEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Item[] input = new Item[0];

    private Recipe recipe;

    private Player player;

    public CraftItemEvent(Player player, Item[] input, Recipe recipe) {
        this.player = player;
        this.input = input;
        this.recipe = recipe;
    }

    public Item[] getInput() {
        Item[] items = new Item[this.input.length];
        for (int i = 0; i < this.input.length; i++) {
            items[i] = this.input[i].clone();
        }

        return items;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Player getPlayer() {
        return player;
    }
}