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
public class FurnaceSmeltEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private BlockEntityFurnace furnace;
    private Item source;
    private Item result;

    public FurnaceSmeltEvent(BlockEntityFurnace furnace, Item source, Item result) {
        super(furnace.getBlock());
        this.source = source.clone();
        this.source.setCount(1);
        this.result = result;
        this.furnace = furnace;
    }

    public BlockEntityFurnace getFurnace() {
        return furnace;
    }

    public Item getSource() {
        return source;
    }

    public Item getResult() {
        return result;
    }

    public void setResult(Item result) {
        this.result = result;
    }
}