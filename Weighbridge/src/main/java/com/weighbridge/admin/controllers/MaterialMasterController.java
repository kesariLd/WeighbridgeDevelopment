package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.MaterialMasterDto;
import com.weighbridge.admin.services.MaterialMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @param materialMasterDto The DTO containing information about the material to be saved.
     * @return ResponseEntity containing the saved material DTO and HTTP status CREATED.
     */
    @PostMapping
    public ResponseEntity<MaterialMasterDto> saveMaterials(@RequestBody MaterialMasterDto materialMasterDto){
        MaterialMasterDto savedMaterial = materialMasterService.saveMaterials(materialMasterDto);
        return new ResponseEntity<>(savedMaterial, HttpStatus.CREATED);
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
