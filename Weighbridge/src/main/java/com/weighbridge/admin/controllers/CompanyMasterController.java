package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.CompanyDto;
import com.weighbridge.admin.services.CompanyMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/company")
public class CompanyMasterController {

   private final CompanyMasterService companyMasterService;

    @PostMapping
    public ResponseEntity<String> createCompany(@Validated @RequestBody CompanyDto companyDto) {
        String response = companyMasterService.createCompany(companyDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CompanyDto>> GetAllCompany() {
        List<CompanyDto> savedCompany = companyMasterService.getAllCompany();
        return ResponseEntity.ok(savedCompany);
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllCompanyNames(){
        List<String> allCompanyNameOnly = companyMasterService.getAllCompanyNameOnly();
        return ResponseEntity.ok(allCompanyNameOnly);
    }

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
