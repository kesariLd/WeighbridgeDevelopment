package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.RoleMasterDto;

import java.util.List;

public interface RoleMasterService {
    RoleMasterDto createRole(RoleMasterDto role);

    void deleteRole(int roleId);


    List<String> getAllStringRole();

    List<RoleMasterDto> getAllRole();
}
