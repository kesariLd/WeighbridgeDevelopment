package com.weighbridge.admin.payloads;

import lombok.Data;

@Data
public class MaterialAndTypeRequest {
    private String materialName;
    private String materialTypeName;
}
