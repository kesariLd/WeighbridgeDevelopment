package com.weighbridge.admin.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CompanyMasterDto {
    private String companyId;

    @NotBlank(message = "Company is required")
    private String companyName;


    private String companyCreatedBy;


    private LocalDateTime companyCreatedDate;


    private String companyModifiedBy;


    private LocalDateTime companyModifiedDate;
}
