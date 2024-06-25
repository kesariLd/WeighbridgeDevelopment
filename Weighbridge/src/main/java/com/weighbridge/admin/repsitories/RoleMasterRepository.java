package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.RoleMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface RoleMasterRepository extends JpaRepository<RoleMaster, Integer> {
    RoleMaster findByRoleName(String role);

    @Query("select r.roleId from RoleMaster r where r.roleName = :roleName")
    Integer findRoleIdByRoleName(@Param("roleName") String roleName);

    @Query("SELECT rm.roleName FROM RoleMaster rm")
    List<String> findAllRoleListName();

    Iterable<RoleMaster> findAllByRoleNameIn(Set<String> setOfRoles);
}
