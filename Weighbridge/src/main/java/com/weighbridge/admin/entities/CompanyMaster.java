package com.weighbridge.admin.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company_master")
public class CompanyMaster {

    @Id
    @Column(name = "company_id")
    private String companyId;

    @NotBlank
    @Column(name = "company_name",nullable = false)
    private String companyName;

    @NotBlank(message = "Email is required")
    @Column(name = "company_email")
    private String companyEmail;

    @NotBlank(message = "Contact No is required")
    @Column(name = "company_contact_no")
    private String companyContactNo;

    @Column(name = "company_address")
    private String companyAddress;

    @Column(name = "company_status")
    private String companyStatus="ACTIVE";

    @Column(name = "company_created_by")
    private String companyCreatedBy;

    @Column(name = "company_created_date")
    private LocalDateTime companyCreatedDate;

    @Column(name = "company_modified_by")
    private String companyModifiedBy;

    @Column(name = "company_modified_date")
    private LocalDateTime companyModifiedDate;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<SiteMaster> sites;
}
