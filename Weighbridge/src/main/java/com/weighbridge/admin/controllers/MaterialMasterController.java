package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.MaterialMasterDto;
import com.weighbridge.admin.payloads.MaterialWithParameters;
import com.weighbridge.admin.services.MaterialMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @throws Exception - If any unexpected error occurs during material creation.
     */
    @PostMapping
    public ResponseEntity<String> createMaterialWithParameterAndRange(@RequestBody MaterialWithParameters materialWithParameters) {
        String response = materialMasterService.createMaterialWithParameterAndRange(materialWithParameters);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all material records.0
     * @return A ResponseEntity object with status code OK (200) containing a list of all material DTOs.
     * @throws Exception - If any unexpected error occurs during material retrieval.
     */
    @GetMapping
    public ResponseEntity<List<MaterialMasterDto>> getAllMaterials() throws Exception {
        List<MaterialMasterDto> allMaterials = materialMasterService.getAllMaterials();
        return ResponseEntity.ok(allMaterials);
    }

    /**
     * Retrieves a list of all material names.
     *
     * @return A ResponseEntity object with status code OK (200) containing a list of all material names.
     * @throws Exception - If any unexpected error occurs during material name retrieval.
     */
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllMaterialNames() throws Exception {
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
     * @throws Exception - If any unexpected error occurs during material type retrieval.
     */
    @GetMapping("/{materialName}/types")
    public ResponseEntity<List<String>> getTypeWithMaterial(@PathVariable String materialName) throws Exception {
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
     * @throws Exception - If any unexpected error occurs during material deletion.
     */
    @DeleteMapping("/{materialName}")
    public ResponseEntity<String> deleteMaterial(@PathVariable String materialName){
        materialMasterService.deleteMaterial(materialName);
        return ResponseEntity.ok("Material is deleted successfully");
    }
    @GetMapping("/{materialName}/types/{materialTypeName}")
    public ResponseEntity<List<MaterialWithParameters>> getQualityRangesByMaterialName(@PathVariable String materialName, @PathVariable String materialTypeName) {
        List<MaterialWithParameters> acceptableQualityRanges = materialMasterService.getQualityRangesByMaterialName(materialName, materialTypeName);
        return ResponseEntity.ok(acceptableQualityRanges);
    }
}
