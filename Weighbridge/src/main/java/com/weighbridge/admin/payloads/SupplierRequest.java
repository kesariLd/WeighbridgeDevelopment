package com.weighbridge.admin.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierRequest {
    private long SupplierId;
    @NotBlank(message = "Supplier number is required")
    private String SupplierName;
    @NotBlank(message = "Supplier number is required")
    private String SupplierEmail;
    private String SupplierContactNo;
    @NotBlank(message = "Supplier number is required")
    private String SupplierAddressLine1;
    @NotBlank(message = "Supplier number is required")
    private String SupplierAddressLine2;
    private String city;
    private String state;
    private String country;
    private String zip;
}
