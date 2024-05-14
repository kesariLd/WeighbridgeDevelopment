package com.weighbridge.admin.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransporterDto {
    private long id;
    private String transporterName;
    private String transporterContactNo;
    private String transporterEmailId;
    private String transporterAddress;
    private String status;
    private String transporterCreatedBy;
    private LocalDateTime transporterCreatedDate;
    private String transporterModifiedBy;
    private LocalDateTime transporterModifiedDate;
}
