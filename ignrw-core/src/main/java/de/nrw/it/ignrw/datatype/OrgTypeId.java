package de.nrw.it.ignrw.datatype;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public enum OrgTypeId {
    UNKNOWN(-1L),
    BERUFS_FEUERWEHR(1L),
    FREIWILLIGE_FEUERWEHR(2L),
    WERKS_FEUERWEHR(3L),
    VERWALTUNG(4L),
    KRISENSTAB(5L),
    LEITSTELLE(6L),
    SAE(7L),
    BETRIEBS_FEUERWEHR(8L),
    HILFSORGANISATION(13L),
    SONSTIGE(14L),
    KRANKENHAUS(15L),
    LZPD(16L);

    private static final Map<Long, OrgTypeId> BY_ORG_TYPE_ID = new HashMap<>();

    static {
        for (OrgTypeId otid : values()) {
            BY_ORG_TYPE_ID.put(otid.getOrgTypeId(), otid);
        }
    }

    @JsonValue
    public final Long orgTypeId;

    OrgTypeId(Long orgTypeId) {
        this.orgTypeId = orgTypeId;
    }

    public static OrgTypeId fromOrgTypeId(Long orgTypeId) {
        OrgTypeId oTypeId = BY_ORG_TYPE_ID.get(orgTypeId);
        return oTypeId != null ? oTypeId : UNKNOWN;
    }
}
