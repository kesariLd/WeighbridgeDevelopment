package com.weighbridge.admin.dtos;


import lombok.Data;

@Data
public class ResetPasswordDto {


    private String password;
    private String rePassword;
}
