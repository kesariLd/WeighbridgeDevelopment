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

    long countByCompanyNameStartingWith(String companyAbbreviation);

    @Query("SELECT c.companyId FROM CompanyMaster c WHERE c.companyName = :companyName")
    String findCompanyIdByCompanyName(@Param("companyName") String companyName);

    @Query("SELECT c.companyName FROM CompanyMaster c WHERE c.companyId = :companyId")
    String findCompanyNameByCompanyId(@Param("companyId") String companyId);
}
