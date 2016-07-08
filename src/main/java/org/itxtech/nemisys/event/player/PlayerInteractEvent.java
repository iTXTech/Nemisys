package org.itxtech.nemisys.event.player;

import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.block.Block;
import org.itxtech.nemisys.event.Cancellable;
import org.itxtech.nemisys.event.HandlerList;
import org.itxtech.nemisys.item.Item;
import org.itxtech.nemisys.level.Position;
import org.itxtech.nemisys.math.Vector3;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class PlayerInteractEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public static final int LEFT_CLICK_BLOCK = 0;
    public static final int RIGHT_CLICK_BLOCK = 1;
    public static final int LEFT_CLICK_AIR = 2;
    public static final int RIGHT_CLICK_AIR = 3;
    public static final int PHYSICAL = 4;

    protected Block blockTouched;

    protected Vector3 touchVector;

    protected int blockFace;

    protected Item item;

    protected int action;

    public PlayerInteractEvent(Player player, Item item, Vector3 block, int face) {
        this(player, item, block, face, RIGHT_CLICK_BLOCK);
    }

    public PlayerInteractEvent(Player player, Item item, Vector3 block, int face, int action) {
        if (block instanceof Block) {
            this.blockTouched = (Block) block;
            this.touchVector = new Vector3(0, 0, 0);
        } else {
            this.touchVector = block;
            this.blockTouched = Block.get(Block.AIR, 0, new Position(0, 0, 0, player.level));
        }

        this.player = player;
        this.item = item;
        this.blockFace = face;
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public Item getItem() {
        return item;
    }

    public Block getBlock() {
        return blockTouched;
    }

    public Vector3 getTouchVector() {
        return touchVector;
    }

    public int getFace() {
        return blockFace;
    }
}
