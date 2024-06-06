package com.weighbridge.admin.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "site_master")
public class SiteMaster {
    @Id
    @Column(name = "site_id")
    private String siteId;

    @NotBlank
    @Column(name = "site_name",nullable = false)
    private String siteName;

    @ManyToOne
    @JsonManagedReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "company_id", referencedColumnName = "company_id")
    private CompanyMaster company;

    @Column(name = "site_address")
    private String siteAddress;

    @Column(name = "site_status")
    private String siteStatus="ACTIVE";

    @Column(name = "site_created_by")
    private String siteCreatedBy;

    @Column(name = "site_created_date")
    private LocalDateTime siteCreatedDate;

    @Column(name = "site_modified_by")
    private String siteModifiedBy;

    @Column(name = "site_modified_date")
    private LocalDateTime siteModifiedDate;
}