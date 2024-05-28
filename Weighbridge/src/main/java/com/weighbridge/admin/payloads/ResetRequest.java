package com.weighbridge.admin.payloads;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetRequest {
    private String emailId;
    private String otp;
    private String newPassword;
}
