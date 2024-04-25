package com.weighbridge.admin.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class SupplierMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    @NotBlank(message = "supplier Name is required")
    private String supplierName;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email format")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "EmailId does not match the required format")
    @Column(name = "supplier_email_id", unique = true )
    private String supplierEmail;
    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid contact number format")
    @Column(name = "supplier_contact_no", unique = true)
    private String supplierContactNo;
    private String supplierAddressLine1;
    private String supplierAddressLine2;
    
    private String city;
    private String state;
    private String country;
    private String zip;
    @Column(name = "supplier_status")
    private String supplierStatus="ACTIVE";

    @Column(name = "supplier_created_by")
    private String supplierCreatedBy;

    @Column(name = "supplier_created_date")
    private LocalDateTime supplierCreatedDate;

    @Column(name = "supplier_modified_by")
    private String supplierModifiedBy;

    @Column(name = "supplier_modified_date")
    private LocalDateTime supplierModifiedDate;
}
