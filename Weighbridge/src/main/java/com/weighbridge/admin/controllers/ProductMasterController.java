package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.MaterialMasterDto;
import com.weighbridge.admin.dtos.ProductMasterDto;
import com.weighbridge.admin.payloads.*;
import com.weighbridge.admin.services.ProductMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductMasterController {

    private final ProductMasterService productMasterService;

    public ProductMasterController(ProductMasterService productMasterService) {
        this.productMasterService = productMasterService;
    }

    @PostMapping
    public ResponseEntity<String> createProductWithParameterAndRange(@RequestBody ProductWithParameters productWithParameters) {
        String response = productMasterService.createProductWithParameterAndRange(productWithParameters);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductMasterDto>> getAllProducts() {
        List<ProductMasterDto> allProducts = productMasterService.getAllProducts();
        return ResponseEntity.ok(allProducts);
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllProductNames(){
        List<String> allProductNames = productMasterService.getAllProductNames();
        return ResponseEntity.ok(allProductNames);
    }

    @GetMapping("/{productName}/types")
    public ResponseEntity<List<String>> getTypeWithProduct(@PathVariable String productName) {
        List<String> allProductTypeNames = productMasterService.getTypeWithProduct(productName);
        return ResponseEntity.ok(allProductTypeNames);
    }

    @DeleteMapping("/{productName}")
    public ResponseEntity<String> deleteMaterial(@PathVariable String productName){
        productMasterService.deleteProduct(productName);
        return ResponseEntity.ok("Product is deleted successfully");
    }

    @GetMapping("/parameters")
    public ResponseEntity<List<ProductWithParameters>> getQualityRangesByProductName(@RequestParam String productName) {
        List<ProductWithParameters> acceptableQualityRanges = productMasterService.getQualityRangesByProductName(productName);
        return ResponseEntity.ok(acceptableQualityRanges);
    }

    @PostMapping("/withType")
    public ResponseEntity<String> saveProductAndProductType(@RequestBody ProductAndTypeRequest productAndTypeRequest,@RequestParam String userId) {
        String response = productMasterService.saveProductAndProductType(productAndTypeRequest,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/view/{productName}/parameters")
    public ResponseEntity<List<ProductParameterResponse>> getMaterialParameters(@PathVariable String productName) {
        List<ProductParameterResponse> response = productMasterService.getProductParameters(productName);
        return ResponseEntity.ok(response);
    }
}
