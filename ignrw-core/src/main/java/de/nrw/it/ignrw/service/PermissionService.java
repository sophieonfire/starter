package de.nrw.it.ignrw.service;

import de.nrw.it.ignrw.datatype.RoleId;

import java.util.List;
import java.util.UUID;

public interface PermissionService {

    List<RoleId> getUserRolesByKeycloakId(String keycloakId, UUID orgUuid);
}
