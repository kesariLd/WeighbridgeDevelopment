package com.weighbridge.admin.dtos;

import lombok.Data;

@Data
public class ProductMasterDto {
    private long productId;
    private String productName;
    private String productStatus;
    private String productCreatedBy;
    private String productCreatedDate;
    private String productModifiedBy;
    private String productModifiedDate;
}
