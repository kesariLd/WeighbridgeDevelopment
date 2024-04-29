package com.weighbridge.admin.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyMasterRequest {
    @NotBlank(message = "Company is required")
    private String companyName;
    @NotBlank(message = "Email is required")
    private String companyEmail;
    @NotBlank(message = "Contact No is required")
    private String companyContactNo;
    private String companyAddress;
}