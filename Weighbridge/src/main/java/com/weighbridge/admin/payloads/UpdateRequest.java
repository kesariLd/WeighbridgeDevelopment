package com.weighbridge.admin.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateRequest {
    @NotBlank(message = "Site is required")
    private String site;


    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Email id is required")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "EmailId does not match the required format")
    private String emailId;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid contact number format")
    private String contactNo;
    private Set<String> role;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "Firstname must be between 2 and 50 characters")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Firstname must be between 2 and 50 characters")
    private String lastName;

}
