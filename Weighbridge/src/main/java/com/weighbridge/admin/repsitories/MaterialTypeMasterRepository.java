package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.MaterialTypeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialTypeMasterRepository extends JpaRepository<MaterialTypeMaster, Long> {
    @Query("SELECT mtm FROM MaterialTypeMaster mtm WHERE mtm.materialTypeName = :materialTypeName")
    MaterialTypeMaster findByMaterialTypeName(@Param("materialTypeName") String materialTypeName);

    @Query("SELECT mtm.materialTypeName FROM MaterialTypeMaster mtm WHERE mtm.materialMaster.materialName = :materialName")
    List<String> findByMaterialMasterMaterialName(@Param("materialName") String materialName);
}
