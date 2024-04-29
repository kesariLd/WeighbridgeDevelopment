package com.weighbridge.admin.services;

import com.weighbridge.admin.payloads.CompanyMasterRequest;

import java.util.List;

public interface CompanyMasterService {

    String createCompany(CompanyMasterRequest companyMasterRequest);

    List<CompanyMasterRequest> getAllCompany();

    List<String> getAllCompanyNameOnly();
}
