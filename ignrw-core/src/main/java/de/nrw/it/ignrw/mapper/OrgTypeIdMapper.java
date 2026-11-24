package de.nrw.it.ignrw.mapper;

import de.nrw.it.ignrw.datatype.OrgTypeId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class OrgTypeIdMapper {

    public Long map(OrgTypeId orgTypeId) {
        return orgTypeId != null ? orgTypeId.getOrgTypeId() : null;
    }

    public OrgTypeId map(Long orgTypeId) {
        return orgTypeId != null ? OrgTypeId.fromOrgTypeId(orgTypeId) : null;
    }
}
