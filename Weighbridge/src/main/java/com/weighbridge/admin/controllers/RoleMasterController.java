package com.weighbridge.admin.controllers;

import com.weighbridge.admin.dtos.RoleMasterDto;
import com.weighbridge.admin.services.RoleMasterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleMasterController {

    private final RoleMasterService roleMasterService;

    public RoleMasterController(RoleMasterService roleMasterService) {
        this.roleMasterService = roleMasterService;
    }

    @PostMapping
    public ResponseEntity<RoleMasterDto> createRole(@Validated @RequestBody RoleMasterDto roleDto) {
        RoleMasterDto roleMasterDto = roleMasterService.createRole(roleDto);
        return new ResponseEntity<>(roleMasterDto, HttpStatus.CREATED);
    }


    // Get all roles as object
    @GetMapping()
    public ResponseEntity<List<RoleMasterDto>> getAllRoles() {
        List<RoleMasterDto> roleMasterList = roleMasterService.getAllRole();
        return new ResponseEntity<>(roleMasterList,HttpStatus.OK);
    }

    // todo: This API should be changed
    @GetMapping("/get/all/role")
    public ResponseEntity<List<String>> getAllRoleNames(){
        List<String> roleMasterList = roleMasterService.getAllRoleNames();
        return new ResponseEntity<>(roleMasterList,HttpStatus.OK);
    }

    @DeleteMapping("/{roleName}")
    public ResponseEntity<Void> deleteRole(@PathVariable("roleName") String roleName) {
        boolean deleted = roleMasterService.deleteRole(roleName);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
