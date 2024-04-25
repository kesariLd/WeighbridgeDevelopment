package com.weighbridge.admin.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SiteMasterDto {
    private String siteId;
    @NotBlank(message = "Site is required")
    private String siteName;

    @NotBlank(message = "Address is required")
    private String siteAddress;


}
