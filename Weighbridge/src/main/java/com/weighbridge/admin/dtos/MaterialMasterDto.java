package com.weighbridge.admin.dtos;

import lombok.Data;

@Data
public class MaterialMasterDto {
    private long materialId;
    private String materialName;
    private String materialStatus;
    private String materialCreatedBy;
    private String materialCreatedDate;
    private String materialModifiedBy;
    private String materialModifiedDate;
}
