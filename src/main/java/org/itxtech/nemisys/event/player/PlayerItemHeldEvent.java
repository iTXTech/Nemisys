package org.itxtech.nemisys.event.player;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerItemHeldEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Item item;
    private int slot;
    private int inventorySlot;

    public PlayerItemHeldEvent(Player player, Item item, int inventorySlot, int slot) {
        this.player = player;
        this.item = item;
        this.inventorySlot = inventorySlot;
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    public Item getItem() {
        return item;
    }

}
