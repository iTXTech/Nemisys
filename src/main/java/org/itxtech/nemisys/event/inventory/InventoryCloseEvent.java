package org.itxtech.nemisys.event.inventory;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.inventory.Inventory;

/**
 * author: Box
 * Nukkit Project
 */
public class InventoryCloseEvent extends InventoryEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private Player who;

    public InventoryCloseEvent(Inventory inventory, Player who) {
        super(inventory);
        this.who = who;
    }

    public Player getPlayer() {
        return this.who;
    }
}
