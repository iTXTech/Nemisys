package org.itxtech.nemisys.network.protocol.mcpe.types;

import lombok.RequiredArgsConstructor;
import org.itxtech.nemisys.network.protocol.mcpe.SetScorePacket;

/**
 * @author CreeperFace
 */
@RequiredArgsConstructor
public class ScoreInfo {

    public final long scoreId;
    public final String objective;
    public final int score;
    public SetScorePacket.Type type;
    public long entityId;
    public String name;

    public ScoreInfo type(SetScorePacket.Type type) {
        this.type = type;
        return this;
    }

    public ScoreInfo entityId(long entityId) {
        this.entityId = entityId;
        return this;
    }

    public ScoreInfo name(String name) {
        this.name = name;
        return this;
    }

}
