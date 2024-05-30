package com.weighbridge.management.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ManagementPayload {
    @JsonFormat(pattern = "yyyy-mm-dd")
    private LocalDate fromDtae;
    @JsonFormat(pattern = "yyyy-mm-dd")
    private LocalDate toDate;
    @NotBlank
    private String companyName;
    @NotBlank
    private String siteName;
}
