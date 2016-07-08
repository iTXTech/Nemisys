package org.itxtech.nemisys.event.inventory;

import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.Event;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.inventory.TransactionGroup;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class InventoryTransactionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private TransactionGroup transaction;

    public InventoryTransactionEvent(TransactionGroup transaction) {
        this.transaction = transaction;
    }

    public TransactionGroup getTransaction() {
        return transaction;
    }

}