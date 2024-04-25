package com.weighbridge.admin.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleMasterDto {
    private Integer roleId;

    @NotBlank(message = "Role is required")
    private String roleName;

    private String roleCreatedBy;


    private LocalDateTime roleCreatedDate;


    private String roleModifiedBy;


    private LocalDateTime roleModifiedDate;
}

