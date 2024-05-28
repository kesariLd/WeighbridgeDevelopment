package com.weighbridge.admin.services;

import com.weighbridge.admin.payloads.MaterialMasterResponse;
import com.weighbridge.admin.payloads.MaterialWithParameters;

import java.util.List;

public interface MaterialMasterService {
    List<MaterialMasterResponse> getAllMaterials();

    List<String> getAllMaterialNames();

    void deleteMaterial(String materialId);

    String createMaterialWithParameterAndRange(MaterialWithParameters materialWithParameters);

    List<String> getTypeWithMaterial(String materialName);

    List<MaterialWithParameters> getQualityRangesByMaterialNameAndSupplierNameAndAddress(String materialName, String supplierName, String supplierAddress);
}
