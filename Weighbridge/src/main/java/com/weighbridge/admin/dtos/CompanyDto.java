package com.weighbridge.admin.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompanyDto {
    private String companyId;
    @NotBlank(message = "Company is required")
    private String companyName;
    @NotBlank(message = "Email is required")
    private String companyEmail;
    @NotBlank(message = "Contact No is required")
    private String companyContactNo;
    private String companyAddress;
}