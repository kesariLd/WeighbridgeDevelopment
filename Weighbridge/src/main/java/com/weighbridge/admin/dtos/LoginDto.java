package com.weighbridge.admin.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDto {

    @Column(name = "user_id", unique = true)
    @NotBlank(message = "UserId is required")
    @Size(min=5, max = 15, message = "UserId id must be between 5 and 15 characters")
  //@Pattern(regexp = "^[A-Za-z0-9]+$", message = "UserId must be alphanumeric")
    private String userId;
    @NotBlank(message = "Password is required!!")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(name = "password")
    private String userPassword;




}
