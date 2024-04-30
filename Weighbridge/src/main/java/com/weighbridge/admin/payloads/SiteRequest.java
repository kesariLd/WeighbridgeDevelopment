package com.weighbridge.admin.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SiteRequest {
    @NotBlank(message = "Company name is required")
    private String companyName;
    @NotBlank(message = "Site name is required")
    private String siteName;
    private String siteAddress;
}
