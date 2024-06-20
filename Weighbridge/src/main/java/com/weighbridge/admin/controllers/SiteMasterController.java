package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.SiteMasterDto;
import com.weighbridge.admin.payloads.SiteRequest;
import com.weighbridge.admin.services.SiteMasterService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * This class serves as a REST API controller for managing site master data.
 * It handles CRUD (Create, Read, Update, Delete) operations on site entities
 * through a set of well-defined API endpoints.
 * @RequestMapping("/api/v1/sites") - This annotation maps all methods of this controller
 * to the base URI "/api/v1/sites".
 */
@RestController
@RequestMapping("/api/v1/sites")
public class SiteMasterController {

    private final SiteMasterService siteMasterService;

    /**
     * Constructor to inject the `SiteMasterService` dependency.
     *
     * @param siteMasterService - The service class responsible for site data access and manipulation logic.
     */
    public SiteMasterController(SiteMasterService siteMasterService) {
        this.siteMasterService = siteMasterService;
    }

    /**
     * Creates a new site record and associates it with a company.
     *
     * @param siteRequest - A DTO object containing the site information to be saved.
     *                     The DTO (Data Transfer Object) pattern is often used to decouple the API layer
     *                     from the underlying data model.
     *                     The `@Validated` annotation indicates that the request body should be validated
     *                     using JSR-380 annotations before processing the request.
     * @return A ResponseEntity object with status code CREATED (201) and a success message
     *         upon successful creation, or an appropriate error response otherwise.
     */
    @PostMapping
    public ResponseEntity<String> createSite(@Validated @RequestBody SiteRequest siteRequest,@RequestParam String userId) {
        String response = siteMasterService.createSite(siteRequest,userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all saved site records.
     *
     * @return A ResponseEntity object with status code OK (200) containing a list of all site DTOs.
     */
    @GetMapping
    public ResponseEntity<List<SiteMasterDto>> getAllSites() {
        List<SiteMasterDto> savedSites = siteMasterService.getAllSite();
        return ResponseEntity.ok(savedSites);
    }

    /**
     * Retrieves a list of sites belonging to a specific company.
     *
     * @param companyName - The name of the company to retrieve sites for.
     *                      This value is extracted from the path variable "{companyName}".
     * @return A ResponseEntity object with status code OK (200) containing a list of all sites
     *         belonging to the specified company. An empty list is returned if the company
     *         does not exist or has no associated sites.
     */
    @GetMapping("/company/{companyName}")
    public ResponseEntity<List<Map<String, String>>> getAllSitesOfCompany(@PathVariable String companyName) {
        List<Map<String, String>> allByCompanySites = siteMasterService.findAllByCompanySites(companyName);
        return ResponseEntity.ok(allByCompanySites);
    }
}
