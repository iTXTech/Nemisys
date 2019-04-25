package org.itxtech.nemisys.command.data;

import io.netty.util.collection.IntObjectHashMap;

import static org.itxtech.nemisys.network.protocol.mcpe.AvailableCommandsPacket.*;

/**
 * @author CreeperFace
 */
public enum CommandParamType {
    INT(ARG_TYPE_INT),
    FLOAT(ARG_TYPE_FLOAT),
    VALUE(ARG_TYPE_VALUE),
    WILDCARD_INT(ARG_TYPE_WILDCARD_INT),
    TARGET(ARG_TYPE_TARGET),
    WILDCARD_TARGET(ARG_TYPE_WILDCARD_TARGET),
    STRING(ARG_TYPE_STRING),
    POSITION(ARG_TYPE_POSITION),
    MESSAGE(ARG_TYPE_MESSAGE),
    RAWTEXT(ARG_TYPE_RAWTEXT),
    JSON(ARG_TYPE_JSON),
    TEXT(ARG_TYPE_RAWTEXT), // backwards compatibility
    COMMAND(ARG_TYPE_COMMAND),
    FILE_PATH(ARG_TYPE_FILE_PATH),
    INT_RANGE(ARG_TYPE_INT_RANGE),
    OPERATOR(ARG_TYPE_OPERATOR);

    private static final IntObjectHashMap<CommandParamType> BY_ID = new IntObjectHashMap<>();

    static {
        for (CommandParamType type : values()) {
            BY_ID.put(type.id, type);
        }
    }

    private final int id;

    CommandParamType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static CommandParamType fromId(int id) {
        return BY_ID.get(id);
    }
}
