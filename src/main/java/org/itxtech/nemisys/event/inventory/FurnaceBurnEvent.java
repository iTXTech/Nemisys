package org.itxtech.nemisys.event.inventory;

import org.itxtech.nemisys.blockentity.BlockEntityFurnace;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.event.block.BlockEvent;
import org.itxtech.nemisys.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class FurnaceBurnEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private BlockEntityFurnace furnace;
    private Item fuel;
    private short burnTime;
    private boolean burning = true;

    public FurnaceBurnEvent(BlockEntityFurnace furnace, Item fuel, short burnTime) {
        super(furnace.getBlock());
        this.fuel = fuel;
        this.burnTime = burnTime;
        this.furnace = furnace;
    }

    public BlockEntityFurnace getFurnace() {
        return furnace;
    }

    public Item getFuel() {
        return fuel;
    }

    public short getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(short burnTime) {
        this.burnTime = burnTime;
    }

    public boolean isBurning() {
        return burning;
    }

    public void setBurning(boolean burning) {
        this.burning = burning;
    }
}