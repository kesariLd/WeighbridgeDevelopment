package com.weighbridge.admin.services;

import com.weighbridge.admin.dtos.ProductMasterDto;
import com.weighbridge.admin.payloads.ProductAndTypeRequest;
import com.weighbridge.admin.payloads.ProductParameterResponse;
import com.weighbridge.admin.payloads.ProductWithParameters;

import java.util.List;

public interface ProductMasterService {
    String createProductWithParameterAndRange(ProductWithParameters productWithParameters);

    List<ProductMasterDto> getAllProducts();

    List<String> getAllProductNames();

    List<String> getTypeWithProduct(String productName);

    void deleteProduct(String productName);

    List<ProductWithParameters> getQualityRangesByProductName(String productName);

    String saveProductAndProductType(ProductAndTypeRequest productAndTypeRequest,String userId);

    List<ProductParameterResponse> getProductParameters(String productName);
}
