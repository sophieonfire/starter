package de.nrw.it.ignrw.datatype;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum LevelTypeId {
    UNKNOWN(-1L),
    GEMEINDE(1L),
    KREIS(2L),
    KREISFREIE_STADT(3L),
    REGIERUNGSBEZIRK(4L),
    LAND(5L),
    KRANKENHAUS_VERBUND(6L),
    KRANKENHAUS(7L),
    HIO_VERBUND(8L),
    PRIVATE_FEUERWEHR(9L),
    STAEDTE_REGION(10L);

    private static final Map<Long, LevelTypeId> BY_LEVEL_TYPE_ID = new HashMap<>();

    static {
        for (LevelTypeId ltid : values()) {
            BY_LEVEL_TYPE_ID.put(ltid.getLevelTypeId(), ltid);
        }
    }

    @JsonValue
    public final Long levelTypeId;


    LevelTypeId(Long levelTypeId) {
        this.levelTypeId = levelTypeId;
    }

    public static LevelTypeId fromLevelTypeId(Long levelTypeId) {
        LevelTypeId lTypeId = BY_LEVEL_TYPE_ID.get(levelTypeId);
        return lTypeId != null ? lTypeId : UNKNOWN;
    }
}
