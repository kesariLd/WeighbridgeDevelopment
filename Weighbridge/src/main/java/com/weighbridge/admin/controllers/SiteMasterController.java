package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.SiteMasterDto;
import com.weighbridge.admin.payloads.SiteRequest;
import com.weighbridge.admin.services.SiteMasterService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sites")
public class SiteMasterController {

    private final SiteMasterService siteMasterService;
    /**
     * Constructs a new SiteMasterController with the provided SiteMasterService.
     * @param siteMasterService The service for handling site-related operations.
     */
    public SiteMasterController(SiteMasterService siteMasterService) {
        this.siteMasterService = siteMasterService;
    }

    /**
     * Endpoint for creating and saving a new site, and assigning it to a company.
     * @param siteRequest The request body containing site information to be saved.
     * @return ResponseEntity with a success message indicating the site was saved successfully and HTTP status OK.
     */
    @PostMapping()
    public ResponseEntity<String> createSite(@Validated @RequestBody SiteRequest siteRequest) {
        String response = siteMasterService.createSite(siteRequest);
        return ResponseEntity.ok(response);
    }
    /**
     * Endpoint for retrieving all saved sites.
     * @return ResponseEntity containing a list of all saved sites and HTTP status OK.
     */
    @GetMapping
    public ResponseEntity<List<SiteMasterDto>> GetAllSites() {
        List<SiteMasterDto> savedSites = siteMasterService.getAllSite();
        return ResponseEntity.ok(savedSites);
    }
    /**
     * Endpoint for retrieving all sites of a specific company.
     * @param companyName The name of the company for which to retrieve sites.
     * @return ResponseEntity containing a list of all sites belonging to the specified company and HTTP status OK.
     */
    @GetMapping("/company/{companyName}")
    public ResponseEntity<List<Map<String, String>>> GetAllSitesOfCompany(@PathVariable String companyName) {
        List<Map<String, String>> allByCompanySites = siteMasterService.findAllByCompanySites(companyName);
        return ResponseEntity.ok(allByCompanySites);
    }
}