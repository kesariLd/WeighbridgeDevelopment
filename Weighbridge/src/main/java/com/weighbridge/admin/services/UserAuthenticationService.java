package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.LoginDto;
import com.weighbridge.admin.dtos.ResetPasswordDto;
import com.weighbridge.admin.entities.UserAuthentication;
import com.weighbridge.admin.payloads.LoginResponse;
import com.weighbridge.admin.payloads.ResetRequest;

public interface UserAuthenticationService {

    LoginResponse loginUser(LoginDto dto);

    UserAuthentication resetPassword(String userId, ResetPasswordDto resetPasswordDto);
    boolean forgotPassword(String emailId);

    boolean ResetPassword(ResetRequest resetRequest);
}
