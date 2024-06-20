package com.weighbridge.admin.services.impls;

import com.weighbridge.admin.entities.CompanyMaster;
import com.weighbridge.admin.entities.SiteMaster;
import com.weighbridge.admin.payloads.SiteRequest;
import com.weighbridge.admin.repsitories.CompanyMasterRepository;
import com.weighbridge.admin.repsitories.SiteMasterRepository;
import com.weighbridge.admin.services.SiteMasterService;
import com.weighbridge.admin.dtos.SiteMasterDto;
import com.weighbridge.admin.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SiteMasterServiceImpl implements SiteMasterService {

    private final SiteMasterRepository siteMasterRepository;
    private final ModelMapper modelMapper;
    private final CompanyMasterRepository companyMasterRepository;
    @Autowired
    private HttpServletRequest httpServletRequest;

    private String generateSiteId(String siteName) {
        // Extract the first three letters of the site name (or abbreviation)
        String siteAbbreviation = siteName.substring(0, Math.min(siteName.length(), 3)).toUpperCase();

        // Retrieve the count of existing site names that start with the same abbreviation
        long siteCount = siteMasterRepository.countBySiteNameStartingWith(siteAbbreviation);

        // Generate the site ID based on the count
        String siteId;
        if (siteCount > 0) {
            // If other sites with the same abbreviation exist, append a numeric suffix
            siteId = String.format("%s%02d", siteAbbreviation, siteCount + 1);
        } else {
            // Otherwise, use the abbreviation without a suffix
            siteId = siteAbbreviation + "01";
            ;
        }
        return siteId;
    }

    @Override
    public List<SiteMasterDto> getAllSite() {
        List<SiteMaster> sites = siteMasterRepository.findAll();
        return sites.stream().map(site -> modelMapper.map(site, SiteMasterDto.class)).collect(Collectors.toList());
    }

    @Override
    public String createSite(SiteRequest siteRequest,String userId) {
        // Find the company by name
        CompanyMaster company = companyMasterRepository.findByCompanyName(siteRequest.getCompanyName());
        if (company == null) {
            throw new ResourceNotFoundException("Company", "companyName", siteRequest.getCompanyName());
        }

        String siteName = siteRequest.getSiteName().trim();
        String siteAddress = siteRequest.getSiteAddress().trim();
        try {
            if (siteName != null && siteAddress != null) {
                // Check if a site with the same name and address exists
                SiteMaster existingSite = siteMasterRepository.findBySiteNameAndSiteAddressAndCompanyCompanyId(siteName, siteAddress,company.getCompanyId());
                if (existingSite != null) {
                    // Associate the company with the existing site(s)
                 /*   if(!existingSite.getCompany().getCompanyId().equals(company.getCompanyId())) {
                        existingSite.setCompany(company);
                        siteMasterRepository.save(existingSite);
                        return "Site(s) assigned to company successfully";
                    }
                    else {*/
                        return "company already assigned to this site";
                  //  }
                } else {
                    // Create a new site if it doesn't exist
                    SiteMaster newSite = new SiteMaster();
                  /*  HttpSession session = httpServletRequest.getSession();
                    String userId = String.valueOf(session.getAttribute("userId"));*/
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    newSite.setSiteId(generateSiteId(siteRequest.getSiteName()));
                    newSite.setSiteName(siteName);
                    newSite.setSiteAddress(siteAddress);
                    newSite.setCompany(company);
                    newSite.setSiteCreatedBy(userId);
                    newSite.setSiteCreatedDate(currentDateTime);
                    newSite.setSiteModifiedBy(userId);
                    newSite.setSiteModifiedDate(currentDateTime);
                    siteMasterRepository.save(newSite);
                    return "Site assigned to company successfully";
                }
            }
            else{
                return "Site and company are null";
            }
        } catch (IllegalArgumentException e) {
            // Handle invalid site info format
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            // Handle other exceptions
            return "Error assigning site: " + e.getMessage();
        }
    }



    @Override
    public List<Map<String, String>> findAllByCompanySites(String companyName) {
        CompanyMaster companyMaster;

        companyMaster = companyMasterRepository.findByCompanyName(companyName);
        if(companyMaster==null){
            throw new ResourceNotFoundException("company does not exist");
        }
        List<Map<String, String>> allByCompanyId = siteMasterRepository.findAllByCompanyId(companyMaster.getCompanyId());
        return allByCompanyId;
    }
}
// todo: do validation if a site is assigned to company , it show a popup to override it