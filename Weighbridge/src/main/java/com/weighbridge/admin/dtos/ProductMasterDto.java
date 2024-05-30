package com.weighbridge.admin.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ProductMasterDto {
    private long productId;
    private String productName;
    private String productTypeName;
    private String productStatus;
}
