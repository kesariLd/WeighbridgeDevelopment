package com.weighbridge.admin.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequest {
    private long customerId;
    @NotBlank(message = "Customer number is required")
    private String customerName;
    @NotBlank(message = "Customer number is required")
    private String customerEmail;
    private String customerContactNo;
    @NotBlank(message = "Customer number is required")
    private String customerAddressLine1;
    @NotBlank(message = "Customer number is required")
    private String customerAddressLine2;
    private String city;
    private String state;
    private String country;
    private String zip;
}
