package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.MaterialMasterDto;
import com.weighbridge.admin.payloads.MaterialWithParametersRequest;

import java.util.List;

public interface MaterialMasterService {
    List<MaterialMasterDto> getAllMaterials();

    List<String> getAllMaterialNames();

    void deleteMaterial(String materialId);

    String createMaterialWithParameterAndRange(MaterialWithParametersRequest materialWithParametersRequest);

    List<String> getTypeWithMaterial(String materialName);
}
