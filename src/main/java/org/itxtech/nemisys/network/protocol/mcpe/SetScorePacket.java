package org.itxtech.nemisys.network.protocol.mcpe;

import org.itxtech.nemisys.network.protocol.mcpe.types.ScoreInfo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author CreeperFace
 */
public class SetScorePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.SET_SCORE_PACKET;

    public Action action;
    public List<ScoreInfo> infos;

    public void encode() {
        reset();
        putByte((byte) action.ordinal());

        putUnsignedVarInt(infos.size());
        for (ScoreInfo it : infos) {
            putVarLong(it.scoreId);
            putString(it.objective);
            putLInt(it.score);
            if(action == Action.SET) {
                putByte((byte) it.type.ordinal());
                switch(it.type) {
                    case PLAYER:
                    case ENTITY:
                        putEntityUniqueId(it.entityId);
                        break;
                    case FAKE:
                        putString(it.name);
                        break;
                }
            }
        }
    }

    @Override
    public void decode() {
        action = Action.values()[getByte()];

        int length = (int) getUnsignedVarInt();
        List<ScoreInfo> infos = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            long id = getVarLong();
            String obj = getString();
            int score = getLInt();

            ScoreInfo info = new ScoreInfo(id, obj, score);

            if(action == Action.SET) {
                Type type = Type.values()[getByte()];

                info.type(type);

                switch (type) {
                    case ENTITY:
                    case PLAYER:
                        info.entityId = getEntityUniqueId();
                    case FAKE:
                        info.name = getString();
                }
            }

            infos.add(info);
        }

        this.infos = infos;
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    public enum Action {
        SET,
        REMOVE
    }

    public enum Type {
        INVALID,
        PLAYER,
        ENTITY,
        FAKE
    }
}
