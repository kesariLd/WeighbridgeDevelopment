package com.weighbridge.admin.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_master")
public class RoleMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int roleId;

    @NotBlank
    @Column(name = "role_name",nullable = false)
    private String roleName;

    @Column(name = "role_status")
    private String roleStatus="ACTIVE";

    @Column(name = "role_created_by")
    private String roleCreatedBy;

    @Column(name = "role_created_date")
    private LocalDateTime roleCreatedDate;

    @Column(name = "role_modified_by")
    private String roleModifiedBy;

    @Column(name = "role_modified_date")
    private LocalDateTime roleModifiedDate;
}