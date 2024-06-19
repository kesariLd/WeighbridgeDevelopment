package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.RoleMasterDto;

import java.util.List;

public interface RoleMasterService {
    RoleMasterDto createRole(RoleMasterDto role,String userId);

    boolean deleteRole(String roleName);

    List<String> getAllRoleNames();

    List<RoleMasterDto> getAllRole();
}
