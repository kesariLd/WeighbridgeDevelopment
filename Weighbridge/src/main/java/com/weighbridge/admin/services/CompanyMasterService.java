package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.CompanyMasterDto;

import java.util.List;

public interface CompanyMasterService {

    CompanyMasterDto createCompany(CompanyMasterDto companyMasterDto);

    List<CompanyMasterDto> getAllCompany();

    List<String> getAllCompanyNameOnly();
}
