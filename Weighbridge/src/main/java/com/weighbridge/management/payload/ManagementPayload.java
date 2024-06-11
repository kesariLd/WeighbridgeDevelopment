package com.weighbridge.management.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ManagementPayload {
    private LocalDate fromDate;
    private LocalDate toDate;
    @NotBlank
    private String companyName;
    @NotBlank
    private String siteName;
}
