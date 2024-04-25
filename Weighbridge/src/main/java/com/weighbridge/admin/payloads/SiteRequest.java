package com.weighbridge.admin.payloads;

import lombok.Data;

@Data
public class SiteRequest {
    private String siteId;
    private String companyName;
    private String siteName;
    private String siteAddress;

}
