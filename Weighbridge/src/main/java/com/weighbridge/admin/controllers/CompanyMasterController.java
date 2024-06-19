package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.CompanyDto;
import com.weighbridge.admin.services.CompanyMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Controller class for managing operations related to company entities.
 * Provides endpoints for creating, retrieving, and deleting company information.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/company")
public class CompanyMasterController {

   private final CompanyMasterService companyMasterService;
    /**
     * Endpoint for creating a new company.
     * Endpoint : <a href="http://hostname:port/api/v1/company">CREATE</a> , Method: POST
     * @param companyDto The DTO containing the information of the company to be created.
     * @return ResponseEntity containing a success message and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<String> createCompany(@Validated @RequestBody CompanyDto companyDto,@RequestParam String userId) {
        String response = companyMasterService.createCompany(companyDto,userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    /**
     * Endpoint for retrieving all companies.
     ** Endpoint : <a href="http://hostname:port/api/v1/company">GET</a> , Method: GET
     * @return ResponseEntity containing a list of all companies and HTTP status OK.
     */
    @GetMapping
    public ResponseEntity<List<CompanyDto>> GetAllCompany() {
        List<CompanyDto> savedCompany = companyMasterService.getAllCompany();
        return ResponseEntity.ok(savedCompany);
    }
    /**
     * Endpoint for retrieving the names of all companies.
     ** Endpoint : <a href="http://hostname:port/api/v1/company/names">GET ONLY NAMES</a> , Method: GET
     * @return ResponseEntity containing a list of company names and HTTP status OK.
     */
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllCompanyNames(){
        List<String> allCompanyNameOnly = companyMasterService.getAllCompanyNameOnly();
        return ResponseEntity.ok(allCompanyNameOnly);
    }
    /**
     * Endpoint for deleting a company by its name.
     ** Endpoint : <a href="http://hostname:port/api/v1/companyName">DELETE</a> , Method: DELETE
     * @param companyName The name of the company to be deleted.
     * @return ResponseEntity with HTTP status NO_CONTENT if the company was deleted successfully,
     *         or HTTP status NOT_FOUND if the company was not found.
     */
    @DeleteMapping("{companyName}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("companyName") String companyName) {
        boolean deleted = companyMasterService.deleteCompanyByName(companyName);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}