package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.QualityRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QualityRangeRepository extends JpaRepository<QualityRange, Long> {

    @Query("SELECT qr FROM QualityRange qr " +
            "JOIN qr.materialMaster mm " +
            "JOIN qr.materialTypeMaster mtm " +
            "WHERE mm.materialName = :materialName " +
            "AND mtm.materialTypeName = :materialTypeName")
    List<QualityRange> findByMaterialMasterMaterialNameAndMaterialTypeMasterMaterialTypeName(
            @Param("materialName") String materialName,
            @Param("materialTypeName") String materialTypeName);
}

