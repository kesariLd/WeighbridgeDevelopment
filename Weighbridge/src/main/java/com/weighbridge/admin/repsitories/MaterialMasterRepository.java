package com.weighbridge.admin.repsitories;


import com.weighbridge.admin.entities.MaterialMaster;
import com.weighbridge.gateuser.entities.GateEntryTransaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MaterialMasterRepository extends JpaRepository<MaterialMaster, Long> {
    boolean existsByMaterialName(String materialName);

//    @Query("SELECT mm.materialName FROM MaterialMaster mm")
//    List<String> findAllMaterialName();

    @Query("SELECT mm FROM MaterialMaster mm WHERE mm.materialName = :materialName")
    MaterialMaster findByMaterialName(@Param("materialName") String materialName);


    @Query("SELECT mm.materialId FROM MaterialMaster mm WHERE mm.materialName = :materialName")
    long findByMaterialIdByMaterialName(@Param("materialName") String materialName);

    @Query("SELECT m.materialName FROM MaterialMaster m WHERE m.materialId = :materialId")
    String findMaterialNameByMaterialId(@Param("materialId") long materialId);

    @Query("SElECT mm.materialName FROM MaterialMaster mm WHERE mm.materialStatus = :status")
    List<String> findAllMaterialNameByMaterialStatus(@Param("status")String status);

	MaterialMaster findByMaterialId(long materialId);
}