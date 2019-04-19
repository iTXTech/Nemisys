package org.itxtech.nemisys.network.protocol.mcpe.types.entity.metadata;

import org.itxtech.nemisys.network.protocol.mcpe.types.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SlotEntityData extends EntityData<Item> {
    public int blockId;
    public int meta;
    public int count;
    public byte[] tag;

    public SlotEntityData(int id, int blockId, int meta, int count) {
        super(id);
        this.blockId = blockId;
        this.meta = meta;
        this.count = count;
    }

    public SlotEntityData(int id, int blockId, int meta, int count, byte[] tag) {
        super(id);
        this.blockId = blockId;
        this.meta = meta;
        this.count = count;
        this.tag = tag;
    }


    public SlotEntityData(int id, Item item) {
        this(id, item.getId(), (byte) (item.hasMeta() ? item.getDamage() : 0), item.getCount(), item.getCompoundTag());
    }

    @Override
    public Item getData() {
        return new Item(blockId, meta, count, tag);
    }

    @Override
    public void setData(Item data) {
        this.blockId = data.getId();
        this.meta = (data.hasMeta() ? data.getDamage() : 0);
        this.count = data.getCount();
    }

    @Override
    public int getType() {
        return EntityMetadata.DATA_TYPE_SLOT;
    }
}
