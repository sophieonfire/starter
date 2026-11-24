package de.nrw.it.ignrw.datatype;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum RoleId {
    UNKNOWN(-1L),
    AUSWERTUNG(2L),
    ERFASSUNG(3L),
    FACHLICHE_BETREUUNG(4L),
    ADMINISTRATION(5L),
    SUPERADMINISTRATION(6L),
    ALRD(7L),
    JAHRESSTATISTIK(8L),
    KRANKENHAUSKONFIGURATION(9L),
    LEITSTELLE(10L),
    LEITSTELLENADMINISTRATION(11L),
    OPTA_VERWALTUNG(12L),
    KRANKENHAUS(13L),
    OPTA_ERFASSUNG(14L),
    KRANKENHAUS_VERWALTUNG(15L),
    KRANKENAHUS_BETREUUNG(18L),
    SCHADENSLAGE(19L),
    LEITSTELLE_LESEND(20L),
    KISKAB(21L),
    KISKAB_ADMINISTRATION(23L),
    HILFSORGANISATION(24L),
    KRANKENHAUS_LESEND(25L),
    KATS_PLANUNG(26L),
    PSU_PSNV(27L),
    LZPD(29L),
    LAGEDIENST_LEITSTELLE(34L),
    JAHRESSTATISTIK_AUSWERTUNG(35L),
    PLS(36L),
    //    IDEEN_ERFASSUNG_KH(38L),
//    IDEEN_ERFASSUNG_LS(39L),
    RETTUNGSDIENST_KH(40L),
    RETTUNGSDIENST_LS(41L),
    PATIENTENVORANMELDUNG_KH(43L),
    ACCOUNTVERWALTUNG(44L),
    BETREUUNG(45L);

    private static final Map<Long, RoleId> BY_ROLE_ID =
            Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(RoleId::getRoleId, Function.identity()));

    @JsonValue
    public final Long roleId;

    RoleId(Long roleId) {
        this.roleId = roleId;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static RoleId deserialize(Object value) {
        if (value == null) {
            return null;
        }

        RoleId result = null;

        // Fall 1: Es wurde eine Zahl gesendet (z.B. 3)
        if (value instanceof Number) {
            long id = ((Number) value).longValue();
            result = BY_ROLE_ID.get(id);
        } else {
            // Fall 2: Es wurde ein String gesendet (z.B. "ERFASSUNG" oder "3")
            String strValue = value.toString().trim();

            // Optionale Sicherheit: Falls die ID fälschlicherweise als String "3" gesendet wurde
            try {
                long id = Long.parseLong(strValue);
                result = BY_ROLE_ID.get(id);
            } catch (NumberFormatException e) {
                // Als Enum-Name verarbeiten
                try {
                    result = RoleId.valueOf(strValue.toUpperCase());
                } catch (IllegalArgumentException iae) {
                    // Bleibt null
                }
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("Invalid role: " + value);
        }

        return result;
    }

    public static RoleId fromRoleId(Long roleId) {
        RoleId rid = BY_ROLE_ID.get(roleId);
        return rid != null ? rid : UNKNOWN;
    }
}
