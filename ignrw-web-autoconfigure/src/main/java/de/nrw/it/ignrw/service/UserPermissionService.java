package de.nrw.it.ignrw.service;

import de.nrw.it.ignrw.datatype.RoleId;
import de.nrw.it.ignrw.repository.UserRoleRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserPermissionService implements PermissionService {

    private final UserRoleRepo mapper;

    public UserPermissionService(UserRoleRepo mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<RoleId> getUserRolesByKeycloakId(String keycloakId, UUID orgUuid) {
        return mapper.findRolesByKeycloakId(keycloakId, orgUuid);
    }
}
