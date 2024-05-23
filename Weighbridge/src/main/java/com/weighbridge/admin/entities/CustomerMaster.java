package com.weighbridge.admin.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer_master")
public class CustomerMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_name",nullable = false)
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email format")
    @Column(name = "customer_email", unique = true)
    private String customerEmail;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid contact number format")
    @Column(name = "customer_contactNo")
    private String customerContactNo;
    @Column(nullable = false)
    private String customerAddressLine1;
    private String customerAddressLine2;

    private String city;
    private String state;
    private String country;
    private String zip;

    @Column(name = "customer_status")
    private String customerStatus = "ACTIVE";

    @Column(name = "customer_created_by")
    private String customerCreatedBy;

    @Column(name = "customer_created_date")
    private LocalDateTime customerCreatedDate;

    @Column(name = "customer_modified_by")
    private String customerModifiedBy;

    @Column(name = "customer_modified_date")
    private LocalDateTime customerModifiedDate;

}
