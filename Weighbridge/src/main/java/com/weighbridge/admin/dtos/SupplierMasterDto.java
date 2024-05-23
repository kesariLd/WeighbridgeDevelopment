package com.weighbridge.admin.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierMasterDto {

    private Long supplierId;

    @NotBlank(message = "supplier Name is required")
    private String supplierName;
    private String supplierEmail;
    private String supplierContactNo;
    private String supplierAddressLine1;
    private String supplierAddressLine2;
    private String supplierStatus;
    private String city;
    private String state;
    private String country;
    private String zip;
}
