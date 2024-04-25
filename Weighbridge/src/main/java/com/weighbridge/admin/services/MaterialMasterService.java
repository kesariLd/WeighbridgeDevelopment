package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.MaterialMasterDto;

import java.util.List;

public interface MaterialMasterService {
    MaterialMasterDto saveMaterials(MaterialMasterDto materialMasterDto);

    List<MaterialMasterDto> getAllMaterials();

    List<String> getAllMaterialNames();

    void deleteMaterial(String materialId);
}
