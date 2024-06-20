package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.SiteMasterDto;
import com.weighbridge.admin.payloads.SiteRequest;

import java.util.List;
import java.util.Map;

public interface SiteMasterService {

    List<SiteMasterDto> getAllSite();

    String createSite(SiteRequest siteRequest,String userId);

    List<Map<String, String>> findAllByCompanySites(String companyName);
}
