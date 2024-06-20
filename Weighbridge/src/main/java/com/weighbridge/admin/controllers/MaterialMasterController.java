package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.MaterialMasterDto;
import com.weighbridge.admin.payloads.MaterialAndTypeRequest;
import com.weighbridge.admin.payloads.MaterialParameterResponse;
import com.weighbridge.admin.payloads.MaterialWithParameters;
import com.weighbridge.admin.services.MaterialMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This class acts as a REST API controller for managing material master data.
 * It facilitates CRUD (Create, Read, Update, Delete) operations on material entities
 * by exposing well-defined API endpoints.
 * "/api/v1/materials" - This annotation maps all methods of this controller
 * to the base URI "/api/v1/materials".
 */
@RestController
@RequestMapping("/api/v1/materials")
public class MaterialMasterController {

    private final MaterialMasterService materialMasterService;

    /**
     * Constructor to inject the `MaterialMasterService` dependency.
     *
     * @param materialMasterService - The service class responsible for material data access and manipulation logic.
     */
    public MaterialMasterController(MaterialMasterService materialMasterService) {
        this.materialMasterService = materialMasterService;
    }

    /**
     * Creates a new material record along with its associated parameters and quality ranges.
     *
     * @param materialWithParameters - A payload object encapsulating the material data, material type,
     *                                       and its parameters with quality ranges to be saved.
     * @return A ResponseEntity containing message material added successfully with HTTP status CREATED(201).
     */
    @PostMapping
    public ResponseEntity<String> createMaterialWithParameterAndRange(@RequestBody MaterialWithParameters materialWithParameters) {
        String response = materialMasterService.createMaterialWithParameterAndRange(materialWithParameters);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all material records.0
     * @return A ResponseEntity object with status code OK (200) containing a list of all material DTOs.
     */
    @GetMapping
    public ResponseEntity<List<MaterialMasterDto>> getAllMaterials() {
        List<MaterialMasterDto> allMaterials = materialMasterService.getAllMaterials();
        return ResponseEntity.ok(allMaterials);
    }

    /**
     * Retrieves a list of all material names.
     *
     * @return A ResponseEntity object with status code OK (200) containing a list of all material names.
     */
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllMaterialNames() {
        List<String> allMaterialNames = materialMasterService.getAllMaterialNames();
        return ResponseEntity.ok(allMaterialNames);
    }

    /**
     * Retrieves all material types associated with a specific material.
     *
     * @param materialName - The name of the material to retrieve types for.
     *                      This value is extracted from the path variable "{materialName}".
     * @return A ResponseEntity object with status code OK (200) containing a list of
     *         material types associated with the specified material. An empty list is returned
     *         if the material is not found.
     */
    @GetMapping("/{materialName}/types")
    public ResponseEntity<List<String>> getTypeWithMaterial(@PathVariable String materialName) {
        List<String> allMaterialTypeNames = materialMasterService.getTypeWithMaterial(materialName);
        return ResponseEntity.ok(allMaterialTypeNames);
    }

    /**
     * Deletes a material by its name.
     *
     * @param materialName - The name of the material to be deleted.
     *                      This value is extracted from the path variable "{materialName}".
     * @return A ResponseEntity object with status code OK (200) containing a success message
     *         upon successful deletion, or an appropriate error response otherwise.
     */
    @DeleteMapping("/{materialName}")
    public ResponseEntity<String> deleteMaterial(@PathVariable String materialName){
        materialMasterService.deleteMaterial(materialName);
        return ResponseEntity.ok("Material is deleted successfully");
    }

    // https:localhost:8080/api/v1/materials/parameters?materialName=Coal&supplierName=MCL&supplierAddress=MCL Bhubaneswari
    @GetMapping("/parameters")
    public ResponseEntity<List<MaterialWithParameters>> getQualityRangesByMaterialName(@RequestParam String materialName,
                                                                                       @RequestParam(required = false) String supplierName,
                                                                                       @RequestParam(required = false) String supplierAddress) {
        List<MaterialWithParameters> acceptableQualityRanges = materialMasterService.getQualityRangesByMaterialNameAndSupplierNameAndAddress(materialName, supplierName, supplierAddress);
        return ResponseEntity.ok(acceptableQualityRanges);
    }

    @PostMapping("/withType")
    public ResponseEntity<String> saveMaterialAndMaterialType(@RequestBody MaterialAndTypeRequest materialAndTypeRequest,@RequestParam String userId) {
        String response = materialMasterService.saveMaterialAndMaterialType(materialAndTypeRequest,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/view/{materialName}/parameters")
    public ResponseEntity<List<MaterialWithParameters>> getMaterialParameters(@PathVariable String materialName) {
        List<MaterialWithParameters> response = materialMasterService.getMaterialParameters(materialName);
        return ResponseEntity.ok(response);
    }

}