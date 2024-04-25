package com.weighbridge.admin.dtos;


import com.weighbridge.admin.entities.CompanyMaster;
import com.weighbridge.admin.entities.SiteMaster;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserMasterDto {
    @NotBlank(message = "UserId is required!!")
    private String userId;

    @NotBlank(message = "CompanySite is required!!")
    private SiteMaster companySite;

    @NotBlank(message = "Company is required!!")
    private CompanyMaster company;

    @NotBlank
    @Email(message = "EmailId is required!!")
    private String emailId;

    @NotBlank(message = "Firstname is required!!")
    private String firstName;

    @NotBlank(message = "Lastname is required!!")
    private String lastName;
}
