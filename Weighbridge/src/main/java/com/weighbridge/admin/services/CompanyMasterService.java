package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.CompanyDto;

import java.util.List;

public interface CompanyMasterService {

    String createCompany(CompanyDto companyDto,String userId);

    List<CompanyDto> getAllCompany();

    List<String> getAllCompanyNameOnly();

    boolean deleteCompanyByName(String companyName);
}
