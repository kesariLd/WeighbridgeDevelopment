package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.LoginDto;
import com.weighbridge.admin.dtos.ResetPasswordDto;
import com.weighbridge.admin.entities.UserAuthentication;
import com.weighbridge.admin.payloads.LoginResponse;

public interface UserAuthenticationService {

    LoginResponse loginUser(LoginDto dto);

    UserAuthentication resetPassword(String userId, ResetPasswordDto resetPasswordDto);
}
