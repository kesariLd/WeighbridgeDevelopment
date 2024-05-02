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

    @PostMapping
    public ResponseEntity<MaterialMasterDto> saveMaterials(@RequestBody MaterialMasterDto materialMasterDto){
        MaterialMasterDto savedMaterial = materialMasterService.saveMaterials(materialMasterDto);
        return new ResponseEntity<>(savedMaterial, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MaterialMasterDto>> getAllMaterials(){
        List<MaterialMasterDto> allMaterials = materialMasterService.getAllMaterials();
        return ResponseEntity.ok(allMaterials);
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllMaterialNames(){
        List<String> allMaterialNames = materialMasterService.getAllMaterialNames();
        return ResponseEntity.ok(allMaterialNames);
    }

    @DeleteMapping("/{materialName}")
    public ResponseEntity<String> deleteMaterial(@PathVariable String materialName){
        materialMasterService.deleteMaterial(materialName);
        return ResponseEntity.ok("Material is deleted successfully");
    }

}
