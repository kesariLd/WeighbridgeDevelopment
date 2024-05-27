package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.CompanyMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyMasterRepository extends JpaRepository<CompanyMaster, String> {
    CompanyMaster findByCompanyName(String company);

    @Query("SELECT c.companyName FROM CompanyMaster c")
    List<String> findAllCompanyListName();

    long countByCompanyIdStartingWith(String companyAbbreviation);

    @Query("SELECT c.companyId FROM CompanyMaster c WHERE c.companyName = :companyName")
    String findCompanyIdByCompanyName(@Param("companyName") String companyName);

    @Query("SELECT c.companyName FROM CompanyMaster c WHERE c.companyId = :companyId")
    String findCompanyNameByCompanyId(@Param("companyId") String companyId);

    @Query("SELECT 1 FROM CompanyMaster cm WHERE cm.companyName = :companyName")
    boolean existsByCompanyName(@Param("companyName") String companyName);

    @Query("DELETE FROM CompanyMaster cm WHERE cm.companyName = :companyName")
    void deleteByCompanyName(@Param("companyName") String companyName);

    @Query("SELECT COUNT(cm) FROM CompanyMaster cm WHERE cm.companyName <> 'ALL_COMP'")
    long countByCompanyNameNot();

    @Query("SELECT cm FROM CompanyMaster cm WHERE cm.companyId <> 'all'")
    List<CompanyMaster> findByCompanyIdNot(@Param("all") String all);

    @Query("SELECT c.companyAddress FROM CompanyMaster c WHERE c.companyId=:companyId")
    String findCompanyAddressByCompanyId(String companyId);
}
    