package com.weighbridge.admin.controllers;

import com.weighbridge.admin.payloads.ProductMasterResponse;
import com.weighbridge.admin.payloads.ProductWithParameters;
import com.weighbridge.admin.services.ProductMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<ProductMasterResponse>> getAllProducts() {
        List<ProductMasterResponse> allProducts = productMasterService.getAllProducts();
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
}
