package com.weighbridge.admin.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_master")
public class UserMaster {

    @Id
    @Column(name = "user_id", unique = true)
    @NotBlank(message = "UserId is required")
    @Size(min=5, max = 15, message = "UserId id must be between 5 and 15 characters")
    private String userId;

    //@NotNull(message = "Site is required")
    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "  site_id")
    private SiteMaster site;

   // @NotNull(message = "Company is required")
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "company_id")
    private CompanyMaster company;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email format")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "EmailId does not match the required format")
    @Column(name = "user_email_id", unique = true )
    private String userEmailId;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid contact number format")
    @Column(name = "user_contact_no")
    private String userContactNo;

    @NotBlank(message = "Firstname is required")
    @Size(min = 2, max = 50, message = "Firstname must be between 2 and 50 characters")
    @Column(name = "user_first_name")
    private String userFirstName;

    @Column(name = "user_middle_name")
    private String userMiddleName;

    @NotBlank(message = "Lastname is required")
    @Size(min = 2, max = 50, message = "Firstname must be between 2 and 50 characters")
    @Column(name = "user_last_name")
    private String userLastName;

    @Column(name = "user_status")
    private String userStatus="ACTIVE";

    @Column(name = "user_created_by")
    private String userCreatedBy;

    @Column(name = "user_created_date")
    private LocalDateTime userCreatedDate;

    @Column(name = "user_modified_by")
    private String userModifiedBy;

    @Column(name = "user_modified_date")
    private LocalDateTime userModifiedDate;

}