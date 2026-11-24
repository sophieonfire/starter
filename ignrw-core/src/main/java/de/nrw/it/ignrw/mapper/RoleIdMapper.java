package de.nrw.it.ignrw.mapper;

import de.nrw.it.ignrw.datatype.RoleId;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class RoleIdMapper {

    public Long map(RoleId roleId) {
        return roleId != null ? roleId.getRoleId() : null;
    }

    public RoleId map(Long roleId) {
        return roleId != null ? RoleId.fromRoleId(roleId) : null;
    }
}
