package com.weighbridge.admin.payloads;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class TransporterRequest {
    @NotBlank(message = "transporter name required")
    private String transporterName;


    private String transporterContactNo;

    private String transporterEmailId;

    @NotBlank(message = "transporter Address required")
    private String transporterAddress;

//    private String createdBy;
//    private  String modifiedBy;
//    private LocalDateTime createdDate;
//    private LocalDateTime modifiedDate;
}
