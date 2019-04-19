package org.itxtech.nemisys.network.protocol.mcpe.types.entity.metadata;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class StringEntityData extends EntityData<String> {
    public String data;

    public StringEntityData(int id, String data) {
        super(id);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int getType() {
        return EntityMetadata.DATA_TYPE_STRING;
    }

    @Override
    public String toString() {
        return data;
    }
}
