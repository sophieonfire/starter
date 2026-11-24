package de.nrw.it.ignrw.repository;

import de.nrw.it.ignrw.datatype.RoleId;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface UserRoleRepo {

    List<RoleId> findRolesByKeycloakId(@Param("keycloakId") String keycloakId, @Param("orgUuid") UUID orgUuid);
}
