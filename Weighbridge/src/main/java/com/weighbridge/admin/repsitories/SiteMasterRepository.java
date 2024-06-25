package com.weighbridge.admin.repsitories;

import com.weighbridge.admin.entities.SiteMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface SiteMasterRepository extends JpaRepository<SiteMaster, String> {
    @Query("SELECT new map(s.siteName as siteName, s.siteAddress as siteAddress) FROM SiteMaster s WHERE s.company.companyId = :companyId")
    List<Map<String, String>> findAllByCompanyId(@Param("companyId") String companyId);

    List<SiteMaster> findBySiteName(String site);

    long countBySiteNameStartingWith(String siteAbbreviation);

    SiteMaster findBySiteNameAndSiteAddress(String siteName, String siteAddress);

    @Query("SELECT s.siteId FROM SiteMaster s WHERE s.siteName = :siteName AND s.siteAddress = :siteAddress")
    String findSiteIdBySiteNameAndSiteAddress(@Param("siteName") String siteName, @Param("siteAddress") String siteAddress);

    @Query("SELECT  s.siteId from SiteMaster s where s.siteName=:siteName and s.siteAddress = :siteAddress")
    String findSiteIdBySiteName(@Param("siteName") String siteName,@Param("siteAddress") String siteAddress);

    @Query("SELECT s.siteName FROM SiteMaster s WHERE s.siteId = :siteId")
    String findSiteNameBySiteId(@Param("siteId") String siteId);

    @Query("SELECT s.siteId FROM SiteMaster s WHERE s.siteName = :siteName AND s.siteAddress = :siteAddress AND s.company.companyId = :companyId")
    String findSiteIdBySiteNameAndSiteAddressAndCompanyId(@Param("siteName") String siteName,
                                                          @Param("siteAddress") String siteAddress,
                                                          @Param("companyId") String companyId);
    SiteMaster findBySiteNameAndSiteAddressAndCompanyCompanyId(String siteName, String siteAddress, String companyId);

}
