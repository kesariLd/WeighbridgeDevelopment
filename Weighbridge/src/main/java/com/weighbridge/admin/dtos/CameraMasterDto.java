package com.weighbridge.admin.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CameraMasterDto {
    private String companyName;
    private String siteName;
    private String role;
    private String topCamUrl1;
    private String bottomCamUrl2;
    private String frontCamUrl3;
    private String backCamUrl4;
    private String leftCamUrl5;
    private String RightCamUrl6;
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
