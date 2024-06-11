package com.weighbridge.admin.dtos;

import lombok.Data;

import java.util.List;

@Data
public class MaterialMasterDto {
    private long materialId;
    private String materialName;
    private String materialStatus;
    private String materialTypeName;
}
