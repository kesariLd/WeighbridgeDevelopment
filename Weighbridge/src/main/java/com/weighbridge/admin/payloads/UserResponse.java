package com.weighbridge.admin.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private String userId;
    private String site;
    private String company;
    private String emailId;
    private String contactNo;
    private Set<String> role;
    private String firstName;
    private String middleName;
    private String lastName;
    private String status;
}