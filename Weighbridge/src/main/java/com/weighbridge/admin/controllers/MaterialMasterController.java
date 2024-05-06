package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.MaterialMasterDto;
import com.weighbridge.admin.payloads.MaterialWithParametersRequest;
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
 * Controller class for managing material master data.
 */
@RestController
@RequestMapping("/api/v1/materials")
public class MaterialMasterController {

    private MaterialMasterService materialMasterService;

    /**
     * Constructor for MaterialMasterController.
     * @param materialMasterService The service to handle material master operations.
     */
    public MaterialMasterController(MaterialMasterService materialMasterService) {
        this.materialMasterService = materialMasterService;
    }

    /**
     * Saves a new material.
     * @param materialWithParametersRequest The payload containing information about the material, material type and its parameters with quality ranges to be saved.
     * @return ResponseEntity containing the saved material DTO and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<String> createMaterialWithParameterAndRange(@RequestBody MaterialWithParametersRequest materialWithParametersRequest) {
        String response = materialMasterService.createMaterialWithParameterAndRange(materialWithParametersRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Retrieves all materials.
     * @return ResponseEntity containing a list of all material DTOs and HTTP status OK.
     */
    @GetMapping
    public ResponseEntity<List<MaterialMasterDto>> getAllMaterials(){
        List<MaterialMasterDto> allMaterials = materialMasterService.getAllMaterials();
        return ResponseEntity.ok(allMaterials);
    }

    /**
     * Retrieves names of all materials.
     * @return ResponseEntity containing a list of all material names and HTTP status OK.
     */
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllMaterialNames(){
        List<String> allMaterialNames = materialMasterService.getAllMaterialNames();
        return ResponseEntity.ok(allMaterialNames);
    }

    @GetMapping("/{materialName}/types")
    public ResponseEntity<List<String>> getTypeWithMaterial(@PathVariable String materialName){
        List<String> allMaterialTypeNames = materialMasterService.getTypeWithMaterial(materialName);
        return ResponseEntity.ok(allMaterialTypeNames);
    }

    /**
     * Deletes a material by its name.
     * @param materialName The name of the material to be deleted.
     * @return ResponseEntity with a success message and HTTP status OK.
     */
    @DeleteMapping("/{materialName}")
    public ResponseEntity<String> deleteMaterial(@PathVariable String materialName){
        materialMasterService.deleteMaterial(materialName);
        return ResponseEntity.ok("Material is deleted successfully");
    }



}
