package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.AcceptableQualityRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AcceptableQualityRangeRepository extends JpaRepository<AcceptableQualityRange, Long> {
    @Query("SELECT aqr FROM AcceptableQualityRange aqr JOIN aqr.material m WHERE aqr.material.materialName = :materialName")
    List<AcceptableQualityRange> findByMaterialMaterialName(@Param("materialName") String materialName);
}

