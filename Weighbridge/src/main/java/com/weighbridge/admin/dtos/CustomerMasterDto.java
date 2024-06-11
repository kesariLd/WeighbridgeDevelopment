package com.weighbridge.admin.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerMasterDto {
    private long customerId;
    @NotBlank(message = "supplier Name is required")
    private String customerName;
    @NotBlank(message = "Email is required")
    private String customerEmail;
    @NotBlank(message = "Contact no is required")
    private String customerContactNo;
    private String customerStatus;
    private String customerAddressLine1;
    private String customerAddressLine2;
    private String city;
    private String state;
    private String country;
    private String zip;
}
