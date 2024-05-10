package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.QualityRangeMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QualityRangeMasterRepository extends JpaRepository<QualityRangeMaster, Long> {

//    @Query("SELECT qr FROM QualityRangeMaster qr " +
//            "JOIN qr.materialMaster mm " +
//            "JOIN qr.materialTypeMaster mtm " +
//            "WHERE mm.materialName = :materialName " +
//            "AND mtm.materialTypeName = :materialTypeName")
//    List<QualityRangeMaster> findByMaterialMasterMaterialNameAndMaterialTypeMasterMaterialTypeName(
//            @Param("materialName") String materialName,
//            @Param("materialTypeName") String materialTypeName);

//    List<QualityRangeMaster> findByMaterialMaster_MaterialNameAndMaterialTypeMaster_MaterialTypeName(String materialName, String materialTypeName);

    List<QualityRangeMaster> findByMaterialMasterMaterialName(String materialName);

    boolean existsByParameterNameAndMaterialMasterMaterialId(String parameterName, long materialId);
}

