package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.QualityRangeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    List<QualityRangeMaster> findByMaterialMasterMaterialNameAndSupplierNameAndSupplierAddress(String materialName, String supplierName, String supplierAddress);

    boolean existsByParameterNameAndProductMasterProductId(String parameterName, long productId);

    List<QualityRangeMaster> findByProductMasterProductName(String productName);

    @Query("SELECT q.qualityRangeId FROM QualityRangeMaster q " +
            "WHERE q.parameterName = :key " +
            "AND q.materialMaster.materialName = :materialName " +
            "AND q.supplierName = :supplierName " +
            "AND q.supplierAddress = :supplierAddress")
    Long findQualityRangeIdByParameterNameAndMaterialMasterMaterialNameAndSupplierNameAndSupplierAddress(
            @Param("key") String key,
            @Param("materialName") String materialName,
            @Param("supplierName") String supplierName,
            @Param("supplierAddress") String supplierAddress
    );

    @Query("SELECT q.qualityRangeId FROM QualityRangeMaster q WHERE q.parameterName = :key AND q.productMaster.productName = :productName")
    Long findQualityRangeIdByParameterNameAndProductMasterProductName(@Param("key") String key, @Param("productName") String productName);

    @Query("SELECT q FROM QualityRangeMaster q WHERE q.materialMaster IS NOT NULL")
    List<QualityRangeMaster> findByMaterialMasterIsNotNull();

    boolean existsByParameterNameAndMaterialMasterMaterialIdAndSupplierNameAndSupplierAddress(String parameterName, long materialId, String supplierName, String supplierAddress);


}

