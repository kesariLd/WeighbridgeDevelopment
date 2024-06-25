package com.weighbridge.admin.payloads;

import lombok.Data;

@Data
public class CameraMasterResponse {


    private Long cameraId;

    private String companyName;
    private String siteName;
    private String role;
    private String topCamUrl1;
    private String bottomCamUrl2;
    private String frontCamUrl3;
    private String backCamUrl4;
    private String leftCamUrl5;
    private String RightCamUrl6;
}
