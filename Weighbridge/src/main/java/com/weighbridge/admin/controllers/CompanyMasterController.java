package com.weighbridge.admin.controllers;

import com.weighbridge.admin.services.CompanyMasterService;
import com.weighbridge.admin.dtos.CompanyMasterDto;
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
    public ResponseEntity<String> createCompany(@Validated @RequestBody CompanyMasterDto companyMasterDto){
        CompanyMasterDto savedCompany = companyMasterService.createCompany(companyMasterDto);
        return new ResponseEntity<>("Company added Successfully", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CompanyMasterDto>> GetAllCompany(){
        List<CompanyMasterDto> savedCompany = companyMasterService.getAllCompany();
        return ResponseEntity.ok(savedCompany);
    }

    @GetMapping("/get/list")
    public ResponseEntity<List<String>> getAllListStringCompanyName(){
        List<String> allCompanyNameOnly = companyMasterService.getAllCompanyNameOnly();
        return ResponseEntity.ok(allCompanyNameOnly);
    }
}
